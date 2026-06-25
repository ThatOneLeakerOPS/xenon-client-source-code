/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_12249;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_9799;
import org.lwjgl.opengl.GL11;

public final class HoleESP
extends Module {
    private static final int MAX_CHUNKS_PER_TICK = 200;
    private static final int FIXED_MIN_DEPTH = 7;
    private final Setting<Double> alpha = new Setting("Fill Alpha", 60, 0, 255);
    private final Setting<Color> color = new Setting("Color", new Color(255, 100, 0));
    private final Setting<Double> range = new Setting("Range", 64, 16, 128);
    private final Setting<Boolean> gradientFill = new Setting("Gradient Fill", true);
    private final Map<Long, TrackedChunk> chunks = new ConcurrentHashMap();
    private final Queue<Long> chunkQueue = new ArrayDeque();
    private final Set<Long> queuedChunks = ConcurrentHashMap.newKeySet();
    private final Set<HoleData> holes = ConcurrentHashMap.newKeySet();
    private ExecutorService executor;
    private class_638 currentWorld;

    public HoleESP() {
        super("Hole ESP", Category.RENDER);
        this.addSetting(this.alpha);
        this.addSetting(this.color);
        this.addSetting(this.range);
        this.addSetting(this.gradientFill);
    }

    public void onEnable() {
        this.currentWorld = mc.field_1687;
        this.ensureExecutor();
        this.clear();
    }

    public void onDisable() {
        this.shutdownExecutor();
        this.clear();
        this.currentWorld = null;
    }

    public void onTick() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        if (mc.field_1687 != this.currentWorld) {
            this.currentWorld = mc.field_1687;
            this.clear();
        }
        this.ensureExecutor();
        this.updateChunks();
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null || this.holes.isEmpty()) {
            return;
        }
        class_4184 cam = RenderUtils.getCamera();
        if (cam == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(cam);
        int fillAlpha = this.clampAlpha(((Double)this.alpha.getValue()).doubleValue());
        boolean renderGradient = ((Boolean)this.gradientFill.getValue()).booleanValue();
        class_9799 allocator = new class_9799(2097152);
        class_4598 immediate = class_4597.method_22991(allocator);
        class_4588 fillConsumer = immediate.method_73477(class_12249.method_76019());
        class_4665 entry = matrices.method_23760();
        boolean rendered = false;
        for (HoleData hole : this.holes) {
            while (!hole.isReadyToRender()) {
            }
            class_238 worldBox = hole.box;
            while (!RenderUtils.isWorldBoxVisible(worldBox.field_1323, worldBox.field_1322, worldBox.field_1321, worldBox.field_1320, worldBox.field_1325, worldBox.field_1324)) {
            }
            Color baseColor = this.color.getValue();
            Color fillColor = this.withAlpha(baseColor, fillAlpha);
            class_238 relativeBox = new class_238(worldBox.field_1323 - camPos.field_1352, worldBox.field_1322 - camPos.field_1351, worldBox.field_1321 - camPos.field_1350, worldBox.field_1320 - camPos.field_1352, worldBox.field_1325 - camPos.field_1351, worldBox.field_1324 - camPos.field_1350);
            if (renderGradient) {
                this.renderGradientBox(fillConsumer, entry, relativeBox, baseColor, fillAlpha);
            } else {
                this.renderFilledBox(fillConsumer, entry, relativeBox, this.toArgb(fillColor));
            }
            rendered = true;
        }
        if (!rendered) {
            allocator.close();
            return;
        }
        depthWasEnabled = GL11.glIsEnabled(2929);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        immediate.method_22993();
        GL11.glDepthMask(true);
        if (depthWasEnabled) {
            GL11.glEnable(2929);
        }
        allocator.close();
    }

    private void renderFilledBox(class_4588 consumer, class_4587.class_4665 entry, class_238 box, int color) {
        float minX = box.field_1323;
        float minY = box.field_1322;
        float minZ = box.field_1321;
        float maxX = box.field_1320;
        float maxY = box.field_1325;
        float maxZ = box.field_1324;
        this.emitQuad(consumer, entry, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, color);
        this.emitQuad(consumer, entry, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, color);
        this.emitQuad(consumer, entry, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, color);
        this.emitQuad(consumer, entry, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, color);
        this.emitQuad(consumer, entry, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, color);
        this.emitQuad(consumer, entry, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, color);
    }

    private void renderGradientBox(class_4588 consumer, class_4587.class_4665 entry, class_238 box, Color baseColor, int maxAlpha) {
        double height = Math.max(0.001, box.field_1325 - box.field_1322);
        int slices = Math.max(1, class_3532.method_15384(height));
        int baseAlpha = Math.max(6, Math.round((float)maxAlpha * 0.18f));
        float minX = box.field_1323;
        float minZ = box.field_1321;
        float maxX = box.field_1320;
        float maxZ = box.field_1324;
        int topCapColor = 0;
        int bottomCapColor = 0;
        for (int slice = 0; slice < slices; slice++) {
            double startProgress = slice / (double)slices;
            double endProgress = (slice + 1) / (double)slices;
            float sliceMinY = class_3532.method_16436(startProgress, box.field_1322, box.field_1325);
            float sliceMaxY = class_3532.method_16436(endProgress, box.field_1322, box.field_1325);
            float bottomFade = 1f - (float)slice / (float)Math.max(1, slices - 1);
            float topFade = 1f - (float)(slice + 1) / (float)Math.max(1, slices);
            int sliceBottomColor = this.toArgb(this.withAlpha(baseColor, Math.max(baseAlpha, Math.round((float)maxAlpha * bottomFade))));
            int sliceTopColor = this.toArgb(this.withAlpha(baseColor, Math.max(baseAlpha, Math.round((float)maxAlpha * topFade))));
            if (slice != 0) continue;
            bottomCapColor = sliceBottomColor;
            if (slice != slices - 1) continue;
            topCapColor = sliceTopColor;
            this.emitVerticalGradientQuad(consumer, entry, minX, sliceMinY, minZ, minX, sliceMaxY, minZ, maxX, sliceMaxY, minZ, maxX, sliceMinY, minZ, sliceBottomColor, sliceTopColor);
            this.emitVerticalGradientQuad(consumer, entry, minX, sliceMinY, maxZ, maxX, sliceMinY, maxZ, maxX, sliceMaxY, maxZ, minX, sliceMaxY, maxZ, sliceBottomColor, sliceTopColor);
            this.emitVerticalGradientQuad(consumer, entry, minX, sliceMinY, minZ, minX, sliceMinY, maxZ, minX, sliceMaxY, maxZ, minX, sliceMaxY, minZ, sliceBottomColor, sliceTopColor);
            this.emitVerticalGradientQuad(consumer, entry, maxX, sliceMinY, minZ, maxX, sliceMaxY, minZ, maxX, sliceMaxY, maxZ, maxX, sliceMinY, maxZ, sliceBottomColor, sliceTopColor);
        }
        this.emitQuad(consumer, entry, minX, (float)box.field_1325, minZ, minX, (float)box.field_1325, maxZ, maxX, (float)box.field_1325, maxZ, maxX, (float)box.field_1325, minZ, topCapColor);
        this.emitQuad(consumer, entry, minX, (float)box.field_1322, minZ, maxX, (float)box.field_1322, minZ, maxX, (float)box.field_1322, maxZ, minX, (float)box.field_1322, maxZ, bottomCapColor);
    }

    private void emitVerticalGradientQuad(class_4588 consumer, class_4587.class_4665 entry, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int bottomColor, int topColor) {
        consumer.method_56824(entry, x1, y1, z1).method_39415(bottomColor);
        consumer.method_56824(entry, x2, y2, z2).method_39415(topColor);
        consumer.method_56824(entry, x3, y3, z3).method_39415(topColor);
        consumer.method_56824(entry, x4, y4, z4).method_39415(bottomColor);
    }

    private void emitQuad(class_4588 consumer, class_4587.class_4665 entry, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
        consumer.method_56824(entry, x1, y1, z1).method_39415(color);
        consumer.method_56824(entry, x2, y2, z2).method_39415(color);
        consumer.method_56824(entry, x3, y3, z3).method_39415(color);
        consumer.method_56824(entry, x4, y4, z4).method_39415(color);
    }

    private void updateChunks() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        for (TrackedChunk trackedChunk : this.chunks.values()) {
            trackedChunk.marked = false;
        }
        viewDist = Math.max(1, this.getRange() / 16);
        int playerChunkX = mc.field_1724.method_31476().field_9181;
        int playerChunkZ = mc.field_1724.method_31476().field_9180;
        for (int cx = playerChunkX - viewDist; cx <= playerChunkX + viewDist; cx++) {
            for (int cz = playerChunkZ - viewDist; cz <= playerChunkZ + viewDist; cz++) {
                class_2818 chunk = mc.field_1687.method_2935().method_12126(cx, cz, false);
                if (chunk != null) {
                    long key = class_1923.method_8331(cx, cz);
                    TrackedChunk trackedChunk = this.chunks.get(key);
                    if (trackedChunk != null) {
                        trackedChunk.marked = true;
                    } else {
                        if (!this.queuedChunks.add(key)) continue;
                        this.chunkQueue.add(key);
                    }
                }
            }
        }
        this.processChunkQueue();
        this.chunks.entrySet().removeIf(HoleESP::lambda$updateChunks$0);
        Set<Long> activeKeys = this.chunks.keySet();
        this.holes.removeIf(this::lambda$updateChunks$1 /* captured: activeKeys */);
    }

    private boolean isBoxInActiveChunks(class_238 box, Set<Long> activeKeys) {
        int chunkX = Math.floor(box.method_1005().field_1352) >> 4;
        int chunkZ = Math.floor(box.method_1005().field_1350) >> 4;
        return activeKeys.contains(class_1923.method_8331(chunkX, chunkZ));
    }

    private void processChunkQueue() {
        if (this.executor == null || mc.field_1687 == null) {
            return;
        }
        int processed = 0;
        while (!this.chunkQueue.isEmpty()) {
            if (processed >= 200) break;
            Long chunkKey = this.chunkQueue.poll();
            while (chunkKey == null) {
            }
            this.queuedChunks.remove(chunkKey);
            int chunkX = class_1923.method_8325(chunkKey.longValue());
            int chunkZ = class_1923.method_8332(chunkKey.longValue());
            class_2818 chunk = mc.field_1687.method_2935().method_12126(chunkX, chunkZ, false);
            while (chunk == null) {
            }
            this.chunks.put(chunkKey, new TrackedChunk(chunkX, chunkZ));
            this.executor.execute(this::lambda$processChunkQueue$2 /* captured: chunk */);
            processed++;
        }
    }

    private void searchChunk(class_2818 chunk) {
        class_638 world = mc.field_1687;
        if (world == null || world != this.currentWorld || !this.isEnabled()) {
            return;
        }
        class_2826[] sections = chunk.method_12006();
        int minY = world.method_31607();
        int maxY = world.method_31607() + world.method_31605();
        int sectionY = minY;
        for (class_2826 section : sections) {
            if (section != null && !(section.method_38292())) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            int currentY = sectionY + y;
                            if (!(currentY <= minY)) {
                                if (!(currentY >= maxY)) {
                                    class_2338 pos = new class_2338(chunk.method_12004().method_8326() + x, currentY, chunk.method_12004().method_8328() + z);
                                    this.checkHole(pos);
                                    this.check3x1Hole(pos);
                                }
                            }
                        }
                    }
                }
            }
            sectionY += 16;
        }
    }

    private void checkHole(class_2338 pos) {
        if (!this.isValidHoleSection(pos) || this.isValidHoleSection(pos.method_10084())) {
            return;
        }
        class_2339 currentPos = pos.method_25503();
        while (this.isValidHoleSection(currentPos)) {
            currentPos.method_10098(class_2350.field_11033);
        }
        int depth = pos.method_10264() - currentPos.method_10264();
        if (depth < this.getMinDepth()) {
            return;
        }
        class_238 box = new class_238((double)pos.method_10263(), (double)(currentPos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1));
        if (!this.containsIntersecting(box)) {
            this.holes.add(new HoleData(box, depth, true));
        }
    }

    private void check3x1Hole(class_2338 pos) {
        if (!this.isValid3x1HoleSectionX(pos)) { /* goto L138; */ }
        if (this.isValid3x1HoleSectionX(pos.method_10084())) { /* goto L138; */ }
        class_2339 currentPos = pos.method_25503();
        while (this.isValid3x1HoleSectionX(currentPos)) {
            currentPos.method_10098(class_2350.field_11033);
        }
        int depth = pos.method_10264() - currentPos.method_10264();
        if (depth >= this.getMinDepth()) {
            class_238 box = new class_238((double)pos.method_10263(), (double)(currentPos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 3), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1));
            if (!this.containsIntersecting(box)) {
                this.holes.add(new HoleData(box, depth, false));
            }
        }
        if (!this.isValid3x1HoleSectionZ(pos)) { /* goto L276; */ }
        if (this.isValid3x1HoleSectionZ(pos.method_10084())) { /* goto L276; */ }
        class_2339 currentPos = pos.method_25503();
        while (this.isValid3x1HoleSectionZ(currentPos)) {
            currentPos.method_10098(class_2350.field_11033);
        }
        int depth = pos.method_10264() - currentPos.method_10264();
        if (depth >= this.getMinDepth()) {
            class_238 box = new class_238((double)pos.method_10263(), (double)(currentPos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 3));
            if (!this.containsIntersecting(box)) {
                this.holes.add(new HoleData(box, depth, false));
            }
        }
    }

    private boolean containsIntersecting(class_238 box) {
        for (HoleData data : this.holes) {
            if (!data.box.equals(box) || data.box.method_994(box)) continue;
            return true;
        }
        return false;
    }

    private boolean isTransparentBlock(class_2680 state) {
        return state.method_26204() == class_2246.field_10503 || state.method_26204() == class_2246.field_9988 || state.method_26204() == class_2246.field_10539 || state.method_26204() == class_2246.field_10335 || state.method_26204() == class_2246.field_10098 || state.method_26204() == class_2246.field_10035 || state.method_26204() == class_2246.field_42731 || state.method_26204() == class_2246.field_37551 || state.method_26204() == class_2246.field_28673 || state.method_26204() == class_2246.field_28674 || state.method_26204() == class_2246.field_10033 || state.method_26204() == class_2246.field_10285 || state.method_26204() == class_2246.field_10597 || state.method_26204() == class_2246.field_28675 || state.method_26204() == class_2246.field_28676 || state.method_26204() == class_2246.field_22123 || state.method_26204() == class_2246.field_22124 || state.method_26204() == class_2246.field_23078 || state.method_26204() == class_2246.field_23079 || state.method_26204() == class_2246.field_28411 || state.method_26204() == class_2246.field_28686 || state.method_26204() == class_2246.field_28677 || state.method_26204() == class_2246.field_10211 || state.method_26204() == class_2246.field_10108 || state.method_26204() == class_2246.field_9993 || state.method_26204() == class_2246.field_10463 || state.method_26204() == class_2246.field_10376 || state.method_26204() == class_2246.field_10238 || state.method_26204() == class_2246.field_10479 || state.method_26204() == class_2246.field_10214 || state.method_26204() == class_2246.field_10112 || state.method_26204() == class_2246.field_10313 || state.method_26204() == class_2246.field_10424 || state.method_26204() == class_2246.field_10428 || state.method_26204() == class_2246.field_16999;
    }

    private boolean isSolidWall(class_2338 pos) {
        if (mc.field_1687 == null) {
            return false;
        }
        class_2680 state = mc.field_1687.method_8320(pos);
        return !state.method_26215() && !this.isTransparentBlock(state);
    }

    private boolean isValidHoleSection(class_2338 pos) {
        return this.isPassable(pos) && this.isSolidWall(pos.method_10095()) && this.isSolidWall(pos.method_10072()) && this.isSolidWall(pos.method_10078()) && this.isSolidWall(pos.method_10067());
    }

    private boolean isValid3x1HoleSectionX(class_2338 pos) {
        return this.isPassable(pos) && this.isPassable(pos.method_10078()) && this.isPassable(pos.method_10089(2)) && this.isSolidWall(pos.method_10095()) && this.isSolidWall(pos.method_10072()) && this.isSolidWall(pos.method_10067()) && this.isSolidWall(pos.method_10089(3));
    }

    private boolean isValid3x1HoleSectionZ(class_2338 pos) {
        return this.isPassable(pos) && this.isPassable(pos.method_10072()) && this.isPassable(pos.method_10077(2)) && this.isSolidWall(pos.method_10078()) && this.isSolidWall(pos.method_10067()) && this.isSolidWall(pos.method_10095()) && this.isSolidWall(pos.method_10077(3));
    }

    private boolean isPassable(class_2338 pos) {
        if (mc.field_1687 == null) {
            return false;
        }
        class_2680 state = mc.field_1687.method_8320(pos);
        if (!state.method_26215()) {
            return false;
        }
        class_2680 below = mc.field_1687.method_8320(pos.method_10074());
        class_2680 above = mc.field_1687.method_8320(pos.method_10084());
        return !this.isPlantBlock(below) && !this.isPlantBlock(above) && !this.isMineshaftBlock(below) && !this.isMineshaftBlock(above);
    }

    private boolean isPlantBlock(class_2680 state) {
        return state.method_26204() == class_2246.field_9993 || state.method_26204() == class_2246.field_10463 || state.method_26204() == class_2246.field_10376 || state.method_26204() == class_2246.field_10238 || state.method_26204() == class_2246.field_10597 || state.method_26204() == class_2246.field_28675 || state.method_26204() == class_2246.field_28676 || state.method_26204() == class_2246.field_22123 || state.method_26204() == class_2246.field_22124 || state.method_26204() == class_2246.field_23078 || state.method_26204() == class_2246.field_23079 || state.method_26204() == class_2246.field_28411 || state.method_26204() == class_2246.field_28686 || state.method_26204() == class_2246.field_28677;
    }

    private boolean isMineshaftBlock(class_2680 state) {
        return state.method_26204() == class_2246.field_10167 || state.method_26204() == class_2246.field_10425 || state.method_26204() == class_2246.field_10025 || state.method_26204() == class_2246.field_10546 || state.method_26204() == class_2246.field_10620 || state.method_26204() == class_2246.field_10132 || state.method_26204() == class_2246.field_10020 || state.method_26204() == class_2246.field_10343;
    }

    private void clear() {
        this.chunks.clear();
        this.chunkQueue.clear();
        this.queuedChunks.clear();
        this.holes.clear();
    }

    private void ensureExecutor() {
        if (this.executor != null && !this.executor.isShutdown()) {
            return;
        }
        this.executor = Executors.newFixedThreadPool(2, HoleESP::lambda$ensureExecutor$3);
    }

    private void shutdownExecutor() {
        ExecutorService existing = this.executor;
        this.executor = null;
        if (existing == null) {
            return;
        }
        existing.shutdown();
        try {
            if (!existing.awaitTermination(500L, TimeUnit.MILLISECONDS)) {
                existing.shutdownNow();
            }
        }
        catch (InterruptedException ignored) {
            existing.shutdownNow();
            Thread.currentThread().interrupt();
            return;
        }
    }

    private int getRange() {
        return class_3532.method_15340((int)Math.round(((Double)this.range.getValue()).doubleValue()), 16, 128);
    }

    private int getMinDepth() {
        return 7;
    }

    private int clampAlpha(double value) {
        return class_3532.method_15340((int)Math.round(value), 0, 255);
    }

    private Color withAlpha(Color base, int alphaValue) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), class_3532.method_15340(alphaValue, 0, 255));
    }

    private int toArgb(Color color) {
        return color.getAlpha() << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }
}
