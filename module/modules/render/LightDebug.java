/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.ModeSetting;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_12249;
import net.minecraft.class_1923;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2818;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_9799;
import org.lwjgl.opengl.GL11;

public class LightDebug
extends Module {
    private static final int FACE_DOWN = 1;
    private static final int FACE_UP = 2;
    private static final int FACE_NORTH = 4;
    private static final int FACE_SOUTH = 8;
    private static final int FACE_WEST = 16;
    private static final int FACE_EAST = 32;
    private static final int FACE_VERTEX_STRIDE = 12;
    private static final int BLOCK_DATA_STRIDE = 5;
    private static final String COLOR_GRAYSCALE = "Grayscale";
    private static final String COLOR_OCEAN = "Ocean";
    private static final String COLOR_WARM = "Warm";
    private static final String COLOR_COOL = "Cool";
    private static final String COLOR_CLASSIC = "Classic";
    private static final int DEFAULT_CHUNK_RADIUS = 8;
    private static final int MIN_CHUNK_RADIUS = 1;
    private static final int MAX_CHUNK_RADIUS = 12;
    private static final int DEFAULT_MAX_Y = -50;
    private static final int MIN_MAX_Y = -63;
    private static final int MAX_MAX_Y = 16;
    private static final float DEFAULT_ALPHA = 160f;
    private static final int REFRESH_INTERVAL_TICKS = 16;
    private final ModeSetting colorMode;
    private final Setting<Float> alpha;
    private final Setting<Integer> maxY;
    private final Setting<Integer> chunkRadius;
    private static final int FIXED_MIN_Y = -64;
    private static final ChunkRenderData[] EMPTY_RENDER_CHUNKS = new ChunkRenderData[0];
    private final Map<Long, ChunkRenderData> chunkCache;
    private volatile ChunkRenderData[] renderChunks;
    private volatile class_1923 lastPlayerChunk;
    private volatile class_638 lastWorld;
    private volatile int lastScannedRadius;
    private volatile int lastScannedMaxY;
    private final ExecutorService executor;
    private final AtomicBoolean isScanning;
    private final AtomicBoolean scanQueued;
    private final AtomicBoolean fullRescanRequested;
    private final AtomicBoolean refreshVisibleRequested;
    private final AtomicBoolean recolorVisibleRequested;
    private volatile long tickCounter;
    private volatile long lastRefreshTick;
    private volatile String lastColorMode;

    public LightDebug() {
        super("Light Debug", Category.RENDER);
        this.colorMode = new ModeSetting("Color", "Classic", "ColorMode", "Color Mode", "Grayscale", "Ocean", "Warm", "Cool", "Classic");
        this.alpha = new Setting("Alpha", 160f, 0f, 255f);
        this.maxY = new LightDebug.1(this, "MaxY", -50, -63, 16);
        this.chunkRadius = new LightDebug.2(this, "ChunkRadius", 8, 1, 12);
        this.chunkCache = new ConcurrentHashMap();
        this.renderChunks = EMPTY_RENDER_CHUNKS;
        this.lastPlayerChunk = null;
        this.lastWorld = null;
        this.lastScannedRadius = -2147483648;
        this.lastScannedMaxY = -2147483648;
        this.executor = Executors.newSingleThreadExecutor(LightDebug::lambda$new$0);
        this.isScanning = new AtomicBoolean(false);
        this.scanQueued = new AtomicBoolean(false);
        this.fullRescanRequested = new AtomicBoolean(true);
        this.refreshVisibleRequested = new AtomicBoolean(false);
        this.recolorVisibleRequested = new AtomicBoolean(false);
        this.tickCounter = 0L;
        this.lastRefreshTick = -9223372036854775808L;
        this.lastColorMode = "";
        this.addSetting(this.colorMode);
        this.addSetting(this.alpha);
        this.addSetting(this.maxY);
        this.addSetting(this.chunkRadius);
    }

    public void onEnable() {
        this.resetCache();
        this.fullRescanRequested.set(true);
        this.refreshVisibleRequested.set(true);
        this.recolorVisibleRequested.set(true);
        this.triggerScan();
    }

    public void onDisable() {
        this.resetCache();
    }

    public void onTick() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        this.tickCounter = this.tickCounter + 1L;
        class_1923 current = mc.field_1724.method_31476();
        long currentTick = this.tickCounter;
        int scanRadius = this.getScanRadius();
        int scanMaxY = this.getScanMaxY();
        String currentColorMode = this.colorMode.getValue();
        boolean moved = !current.equals(this.lastPlayerChunk);
        boolean worldChanged = mc.field_1687 != this.lastWorld;
        boolean boundsChanged = scanRadius != this.lastScannedRadius || scanMaxY != this.lastScannedMaxY;
        boolean colorModeChanged = !currentColorMode.equalsIgnoreCase(this.lastColorMode);
        if (worldChanged || boundsChanged) {
            this.fullRescanRequested.set(true);
        }
        if (colorModeChanged) {
            this.recolorVisibleRequested.set(true);
        }
        if (currentTick - this.lastRefreshTick >= 16L) {
            this.refreshVisibleRequested.set(true);
        }
        if (moved) {
            this.lastPlayerChunk = current;
        }
        if (moved || this.fullRescanRequested.get() || this.refreshVisibleRequested.get() || this.recolorVisibleRequested.get()) {
            this.triggerScan();
        }
        this.lastColorMode = currentColorMode;
    }

    private void triggerScan() {
        this.scanQueued.set(true);
        if (this.isScanning.compareAndSet(false, true)) {
            this.executor.submit(this::runPendingScans);
        }
    }

    private void runPendingScans() {
        try {
            while (this.scanQueued.getAndSet(false)) {
                this.rebuildVisibleChunks();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.isScanning.set(false);
            if (this.scanQueued.get() && this.isScanning.compareAndSet(false, true)) {
                this.executor.submit(this::runPendingScans);
            }
        }
        finally {
            this.isScanning.set(false);
            if (this.scanQueued.get()) {
                if (this.isScanning.compareAndSet(false, true)) {
                    this.executor.submit(this::runPendingScans);
                }
            }
            throw var2;
        }
    }

    private void rebuildVisibleChunks() {
        if (!this.isEnabled() || mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        class_638 world = mc.field_1687;
        class_1923 center = mc.field_1724.method_31476();
        int radius = this.getScanRadius();
        int scanMaxY = this.getScanMaxY();
        boolean fullRescan = this.fullRescanRequested.getAndSet(false) || world != this.lastWorld || radius != this.lastScannedRadius || scanMaxY != this.lastScannedMaxY;
        boolean refreshVisible = fullRescan || this.refreshVisibleRequested.getAndSet(false);
        boolean recolorVisible = fullRescan || refreshVisible || this.recolorVisibleRequested.getAndSet(false);
        long colorTick = this.tickCounter;
        LongSet visibleChunkKeys = this.collectVisibleChunkKeys(center, radius);
        Map<Long, ChunkRenderData> nextCache = fullRescan ? new HashMap(visibleChunkKeys.size()) : new HashMap(this.chunkCache);
        if (!fullRescan) {
            nextCache.keySet().removeIf(LightDebug::lambda$rebuildVisibleChunks$1 /* captured: visibleChunkKeys */);
        }
        LongIterator var12 = visibleChunkKeys.iterator();
        while (var12.hasNext()) {
            long chunkKey = ((Long)var12.next()).longValue();
            ChunkRenderData existing = nextCache.get(chunkKey);
            while (!refreshVisible) {
                if (existing == null) break;
                if (!recolorVisible) continue;
                nextCache.put(chunkKey, this.recolorChunk(existing));
            }
            int chunkX = class_1923.method_8325(chunkKey);
            int chunkZ = class_1923.method_8332(chunkKey);
            while (!world.method_8393(chunkX, chunkZ)) {
                nextCache.remove(chunkKey);
            }
            class_2818 chunk = world.method_2935().method_12126(chunkX, chunkZ, false);
            while (chunk == null) {
                nextCache.remove(chunkKey);
            }
            ChunkRenderData chunkData = this.scanChunk(world, chunk, scanMaxY);
            if (chunkData == null) {
                nextCache.remove(chunkKey);
            } else {
                nextCache.put(chunkKey, chunkData);
            }
        }
        if (!this.isEnabled()) {
            return;
        }
        this.chunkCache.clear();
        this.chunkCache.putAll(nextCache);
        this.renderChunks = nextCache.values().toArray(new ChunkRenderData[0]);
        this.lastWorld = world;
        this.lastPlayerChunk = center;
        this.lastScannedRadius = radius;
        this.lastScannedMaxY = scanMaxY;
        if (refreshVisible) {
            this.lastRefreshTick = colorTick;
        }
    }

    private ChunkRenderData scanChunk(class_638 world, class_2818 chunk, int scanMaxY) {
        class_1923 chunkPos = chunk.method_12004();
        int startX = chunkPos.method_8326();
        int startZ = chunkPos.method_8328();
        int endX = startX + 15;
        int endZ = startZ + 15;
        int yRange = scanMaxY - -64 + 1;
        Long2IntOpenHashMap litBlocks = new Long2IntOpenHashMap(Math.max(512, 324 * yRange / 4));
        litBlocks.defaultReturnValue(0);
        class_2339 mutablePos = new class_2338.class_2339();
        for (int wx = startX - 1; wx <= endX + 1; wx++) {
            for (int wz = startZ - 1; wz <= endZ + 1; wz++) {
                for (int y = -64; y <= scanMaxY; y++) {
                    mutablePos.method_10103(wx, y, wz);
                    int blockLight = world.method_8314(class_1944.field_9282, mutablePos);
                    if (!(blockLight <= 0)) {
                        int skyLight = world.method_8314(class_1944.field_9284, mutablePos);
                        if ((blockLight <= skyLight)) continue;
                        litBlocks.put(class_2338.method_10064(wx, y, wz), blockLight);
                    }
                }
            }
        }
        if (litBlocks.isEmpty()) {
            return null;
        }
        LongSet litSet = litBlocks.keySet();
        int[] data = new int[litBlocks.size() * 5];
        int count = 0;
        int minY = Integer.MAX_VALUE;
        for (Entry entry : litBlocks.long2IntEntrySet()) {
            long key = entry.getLongKey();
            int wx = class_2338.method_10061(key);
            int wy = class_2338.method_10071(key);
            int wz = class_2338.method_10083(key);
            if (wx < startX) continue;
            if (wx > endX) continue;
            if (wz < startZ) continue;
            while (wz > endZ) {
            }
            int flags = 0;
            if (litSet.contains(class_2338.method_10064(wx, wy - 1, wz))) continue;
            flags = flags | 1;
            if (litSet.contains(class_2338.method_10064(wx, wy + 1, wz))) continue;
            flags = flags | 2;
            if (litSet.contains(class_2338.method_10064(wx, wy, wz - 1))) continue;
            flags = flags | 4;
            if (litSet.contains(class_2338.method_10064(wx, wy, wz + 1))) continue;
            flags = flags | 8;
            if (litSet.contains(class_2338.method_10064(wx - 1, wy, wz))) continue;
            flags = flags | 16;
            if (litSet.contains(class_2338.method_10064(wx + 1, wy, wz))) continue;
            flags = flags | 32;
            while (flags == 0) {
            }
            int idx = count * 5;
            data[idx] = wx;
            data[idx + 1] = wy;
            data[idx + 2] = wz;
            data[idx + 3] = flags;
            data[idx + 4] = entry.getIntValue();
            count++;
            if (wy >= minY) continue;
            minY = wy;
        }
        if (count == 0) {
            return null;
        }
        blockData = Arrays.copyOf(data, count * 5);
        return this.buildChunkRenderData(startX, startZ, minY, blockData, count);
    }

    private LongSet collectVisibleChunkKeys(class_1923 center, int radius) {
        int diameter = radius * 2 + 1;
        LongOpenHashSet visibleChunks = new LongOpenHashSet(diameter * diameter);
        for (int cx = center.field_9181 - radius; cx <= center.field_9181 + radius; cx++) {
            for (int cz = center.field_9180 - radius; cz <= center.field_9180 + radius; cz++) {
                visibleChunks.add(class_1923.method_8331(cx, cz));
            }
        }
        return visibleChunks;
    }

    private int getScanRadius() {
        return class_3532.method_15340(((Integer)this.chunkRadius.getValue()).intValue(), 1, 12);
    }

    private int getScanMaxY() {
        return class_3532.method_15340(((Integer)this.maxY.getValue()).intValue(), -64, 16);
    }

    private void resetCache() {
        this.chunkCache.clear();
        this.renderChunks = EMPTY_RENDER_CHUNKS;
        this.lastPlayerChunk = null;
        this.lastWorld = null;
        this.lastScannedRadius = -2147483648;
        this.lastScannedMaxY = -2147483648;
        this.scanQueued.set(false);
        this.fullRescanRequested.set(true);
        this.refreshVisibleRequested.set(false);
        this.recolorVisibleRequested.set(false);
        this.tickCounter = 0L;
        this.lastRefreshTick = -9223372036854775808L;
        this.lastColorMode = this.colorMode.getValue();
    }

    private ChunkRenderData buildChunkRenderData(int minX, int minZ, int minY, int[] blockData, int blockCount) {
        int faceCount = this.countFaces(blockData, blockCount);
        short[] faceVertices = this.bakeFaceVertices(blockData, blockCount, minX, minZ, minY, faceCount);
        int[] faceColors = this.bakeFaceColors(blockData, blockCount, faceCount);
        return new ChunkRenderData(minX, minZ, minY, blockData, blockCount, faceVertices, faceColors, faceCount);
    }

    private ChunkRenderData recolorChunk(ChunkRenderData source) {
        int[] recoloredFaces = this.bakeFaceColors(source.blockData, source.blockCount, source.faceCount);
        return new ChunkRenderData(source.minX, source.minZ, source.minY, source.blockData, source.blockCount, source.faceVertices, recoloredFaces, source.faceCount);
    }

    private int countFaces(int[] data, int count) {
        int faces = 0;
        int i = 0;
        int end = count * 5;
        while (i < end) {
            faces = faces + Integer.bitCount(data[i + 3]);
            i += 5;
        }
        return faces;
    }

    private short[] bakeFaceVertices(int[] data, int count, int startX, int startZ, int minY, int faceCount) {
        short[] faceVertices = new short[faceCount * 12];
        int faceIndex = 0;
        int i = 0;
        int end = count * 5;
        while (i < end) {
            short x1 = (data[i] - startX);
            short y1 = (data[i + 1] - minY);
            short z1 = (data[i + 2] - startZ);
            short x2 = (x1 + 1);
            short y2 = (y1 + 1);
            short z2 = (z1 + 1);
            int flags = data[i + 3];
            if (flags & 1 == 0) continue;
            this.writeFaceVertices(faceVertices, faceIndex++, x1, y1, z1, x1, y1, z2, x2, y1, z2, x2, y1, z1);
            if (flags & 2 == 0) continue;
            this.writeFaceVertices(faceVertices, faceIndex++, x1, y2, z1, x2, y2, z1, x2, y2, z2, x1, y2, z2);
            if (flags & 4 == 0) continue;
            this.writeFaceVertices(faceVertices, faceIndex++, x1, y1, z1, x2, y1, z1, x2, y2, z1, x1, y2, z1);
            if (flags & 8 == 0) continue;
            this.writeFaceVertices(faceVertices, faceIndex++, x1, y1, z2, x1, y2, z2, x2, y2, z2, x2, y1, z2);
            if (flags & 16 == 0) continue;
            this.writeFaceVertices(faceVertices, faceIndex++, x1, y1, z1, x1, y2, z1, x1, y2, z2, x1, y1, z2);
            if (flags & 32 == 0) continue;
            this.writeFaceVertices(faceVertices, faceIndex++, x2, y1, z1, x2, y1, z2, x2, y2, z2, x2, y2, z1);
            i += 5;
        }
        return faceVertices;
    }

    private int[] bakeFaceColors(int[] data, int count, int faceCount) {
        int[] faceColors = new int[faceCount];
        int faceIndex = 0;
        int i = 0;
        int end = count * 5;
        while (i < end) {
            int color = this.computePackedColor(data[i + 4]);
            int flags = data[i + 3];
            int repeats = Integer.bitCount(flags);
            Arrays.fill(faceColors, faceIndex, faceIndex + repeats, color);
            faceIndex = faceIndex + repeats;
            i += 5;
        }
        return faceColors;
    }

    private void writeFaceVertices(short[] faceVertices, int faceIndex, short x1, short y1, short z1, short x2, short y2, short z2, short x3, short y3, short z3, short x4, short y4, short z4) {
        int vertexOffset = faceIndex * 12;
        faceVertices[vertexOffset] = x1;
        faceVertices[vertexOffset + 1] = y1;
        faceVertices[vertexOffset + 2] = z1;
        faceVertices[vertexOffset + 3] = x2;
        faceVertices[vertexOffset + 4] = y2;
        faceVertices[vertexOffset + 5] = z2;
        faceVertices[vertexOffset + 6] = x3;
        faceVertices[vertexOffset + 7] = y3;
        faceVertices[vertexOffset + 8] = z3;
        faceVertices[vertexOffset + 9] = x4;
        faceVertices[vertexOffset + 10] = y4;
        faceVertices[vertexOffset + 11] = z4;
    }

    private int computePackedColor(int lightLevel) {
        float t = class_3532.method_15363((float)lightLevel / 15f, 0f, 1f);
        String selected = this.colorMode.getValue();
        var var4 = selected;
        int var5 = -1;
        switch (var4.hashCode()) {
            case 1098556583:
                if (var4.equals("Grayscale")) {
                    var5 = 0;
                }
            case 76007646:
                if (var4.equals("Ocean")) {
                    var5 = 1;
                }
            case 2688677:
                if (var4.equals("Warm")) {
                    var5 = 2;
                }
            case 2106217:
                if (var4.equals("Cool")) {
                    var5 = 3;
                }
            case -1776693134:
                if (var4.equals("Classic")) {
                    var5 = 4;
                }
            default:
                switch (var5) {
                    case 0:
                        return this.grayscaleColor(t);
                    case 1:
                        return this.threeStepColor(663373, 2057176, 4908799, t);
                    case 2:
                        return this.warmColor(t);
                    case 3:
                        return this.coolColor(t);
                    case 4:
                        return this.classicColor(t);
                    default:
                        return this.classicColor(t);
                }
        }
    }

    private int classicColor(float t) {
        int r = (t * 255f);
        int g = (50f + t * 205f);
        int b = (200f * (1f - t));
        return r << 16 | g << 8 | b;
    }

    private int grayscaleColor(float t) {
        int value = class_3532.method_15340(Math.round(t * 255f), 0, 255);
        return value << 16 | value << 8 | value;
    }

    private int warmColor(float t) {
        return this.threeStepColor(11540504, 16742938, 16771418, t);
    }

    private int coolColor(float t) {
        return this.threeStepColor(1195419, 1497343, 16777215, t);
    }

    private int threeStepColor(int lowRgb, int midRgb, int highRgb, float t) {
        float clamped = class_3532.method_15363(t, 0f, 1f);
        if (clamped <= 0.5f) {
            return this.lerpColor(lowRgb, midRgb, clamped * 2f);
        }
        return this.lerpColor(midRgb, highRgb, (clamped - 0.5f) * 2f);
    }

    private int lerpColor(int fromRgb, int toRgb, float t) {
        float clamped = class_3532.method_15363(t, 0f, 1f);
        int r1 = fromRgb >> 16 & 255;
        int g1 = fromRgb >> 8 & 255;
        int b1 = fromRgb & 255;
        int r2 = toRgb >> 16 & 255;
        int g2 = toRgb >> 8 & 255;
        int b2 = toRgb & 255;
        int r = Math.round((float)r1 + (float)(r2 - r1) * clamped);
        int g = Math.round((float)g1 + (float)(g2 - g1) * clamped);
        int b = Math.round((float)b1 + (float)(b2 - b1) * clamped);
        return r << 16 | g << 8 | b;
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        ChunkRenderData[] chunks = this.renderChunks;
        if (chunks.length == 0) {
            return;
        }
        class_4184 cam = RenderUtils.getCamera();
        if (cam == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(cam);
        int alphaMask = this.clampAlpha(((Float)this.alpha.getValue()).floatValue()) << 24;
        if (alphaMask == 0) {
            return;
        }
        double px = camPos.field_1352;
        double py = camPos.field_1351;
        double pz = camPos.field_1350;
        class_9799 allocator = new class_9799(2097152);
        class_4598 immediate = class_4597.method_22991(allocator);
        class_4588 buf = immediate.method_73477(class_12249.method_76019());
        boolean hasGeometry = false;
        for (ChunkRenderData chunk : chunks) {
            if (chunk != null) {
                matrices.method_22903();
                matrices.method_22904((double)chunk.minX - px, (double)chunk.minY - py, (double)chunk.minZ - pz);
                class_4665 chunkEntry = matrices.method_23760();
                short[] faceVertices = chunk.faceVertices;
                int[] faceColors = chunk.faceColors;
                int face = 0;
                int vertexOffset = 0;
                while (face < chunk.faceCount) {
                    int argb = alphaMask | faceColors[face];
                    buf.method_56824(chunkEntry, (float)faceVertices[vertexOffset], (float)faceVertices[vertexOffset + 1], (float)faceVertices[vertexOffset + 2]).method_39415(argb);
                    buf.method_56824(chunkEntry, (float)faceVertices[vertexOffset + 3], (float)faceVertices[vertexOffset + 4], (float)faceVertices[vertexOffset + 5]).method_39415(argb);
                    buf.method_56824(chunkEntry, (float)faceVertices[vertexOffset + 6], (float)faceVertices[vertexOffset + 7], (float)faceVertices[vertexOffset + 8]).method_39415(argb);
                    buf.method_56824(chunkEntry, (float)faceVertices[vertexOffset + 9], (float)faceVertices[vertexOffset + 10], (float)faceVertices[vertexOffset + 11]).method_39415(argb);
                    hasGeometry = true;
                    face++;
                    vertexOffset += 12;
                }
                matrices.method_22909();
            }
        }
        if (!hasGeometry) {
            allocator.close();
            return;
        }
        depthWasEnabled = GL11.glIsEnabled(2929);
        polyOffsetWasEnabled = GL11.glIsEnabled(32823);
        GL11.glEnable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(32823);
        GL11.glPolygonOffset(-1f, -1f);
        immediate.method_22993();
        GL11.glPolygonOffset(0f, 0f);
        if (!polyOffsetWasEnabled) {
            GL11.glDisable(32823);
        }
        GL11.glDepthMask(true);
        if (!depthWasEnabled) {
            GL11.glDisable(2929);
        }
        allocator.close();
    }

    private int clampAlpha(float value) {
        return class_3532.method_15340(Math.round(value), 0, 255);
    }
}
