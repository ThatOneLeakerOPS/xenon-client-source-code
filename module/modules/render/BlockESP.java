/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.gui.notification.NotificationManager;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.setting.BlocksSetting;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1799;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2672;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2960;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_7923;
import org.lwjgl.opengl.GL11;

public final class BlockESP
extends Module {
    private static final int RESCAN_INTERVAL_TICKS = 40;
    private static final int CHUNKS_PER_TICK = 6;
    private static final long NOTIFY_COOLDOWN_MS = 750L;
    private static final int DEFAULT_ALPHA = 110;
    private static final double BOX_INSET = 0.0625;
    private static final double TRACER_START_DISTANCE = 150;
    private static final double TRACER_END_DISTANCE = 24;
    private static final double TRACER_BEHIND_MIN_SPREAD = 2.75;
    private final BlocksSetting blocks;
    private final Setting<Boolean> notify;
    private final Setting<Boolean> esp;
    private final Setting<Boolean> tracers;
    private final Map<Long, Set<class_2338>> cachedBlocks;
    private final Map<class_2338, class_2248> posTypeMap;
    private final Map<Long, Long> lastNotifiedAt;
    private final ArrayDeque<Long> scanQueue;
    private final Set<Long> queuedChunks;
    private final Object queueLock;
    private volatile Set<class_2248> targets;
    private long lastBlocksVersion;
    private int tickCounter;
    private boolean fullRescanRequested;
    private class_1923 lastCenterChunk;
    private int lastChunkRadius;

    public BlockESP() {
        super("Block ESP", Category.RENDER);
        this.blocks = new BlocksSetting("Blocks", class_2246.field_10260);
        this.notify = new Setting("Notification", true);
        this.esp = new Setting("ESP", true);
        this.tracers = new Setting("Tracers", false);
        this.cachedBlocks = new ConcurrentHashMap();
        this.posTypeMap = new ConcurrentHashMap();
        this.lastNotifiedAt = new ConcurrentHashMap();
        this.scanQueue = new ArrayDeque();
        this.queuedChunks = new HashSet();
        this.queueLock = new Object();
        this.targets = Collections.emptySet();
        this.lastBlocksVersion = -1L;
        this.tickCounter = 0;
        this.fullRescanRequested = true;
        this.lastChunkRadius = -1;
        this.addSetting(this.blocks);
        this.addSetting(this.notify);
        this.addSetting(this.esp);
        this.addSetting(this.tracers);
    }

    public void onEnable() {
        this.clearCaches();
        this.lastBlocksVersion = -1L;
        this.fullRescanRequested = true;
        this.tickCounter = 0;
        this.lastCenterChunk = null;
        this.lastChunkRadius = -1;
    }

    public void onDisable() {
        this.clearCaches();
        this.lastCenterChunk = null;
        this.lastChunkRadius = -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onTick() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        this.updateTargets();
        if (this.targets.isEmpty()) {
            this.clearCaches();
            return;
        }
        this.tickCounter = this.tickCounter + 1;
        class_1923 currentChunk = mc.field_1724.method_31476();
        int currentChunkRadius = this.getChunkRadius();
        boolean forceRescan = this.fullRescanRequested || this.tickCounter % 40 == 0;
        if (forceRescan || this.lastCenterChunk == null || !this.lastCenterChunk.equals(currentChunk) || this.lastChunkRadius != currentChunkRadius) {
            this.rebuildLoadedChunkQueue(forceRescan);
            this.fullRescanRequested = false;
            this.lastCenterChunk = currentChunk;
            this.lastChunkRadius = currentChunkRadius;
        }
        int i = 0;
        while (i < 6) {
            int chunkX = this.queueLock;
            synchronized (this.queueLock) {
            Long chunkKey = this.scanQueue.poll();
            if (chunkKey == null) continue;
            this.queuedChunks.remove(chunkKey);
            }
            if (chunkKey != null) {
                chunkX = class_1923.method_8325(chunkKey.longValue());
                int chunkZ = class_1923.method_8332(chunkKey.longValue());
                class_2818 chunk = mc.field_1687.method_2935().method_12126(chunkX, chunkZ, false);
                if (chunk == null) continue;
                this.scanChunk(chunk);
                i++;
            }
        }
    }

    public void onPacketReceive(class_2596<?> packet) {
        if (mc.field_1687 == null) {
            return;
        }
        if (packet instanceof class_2672) {
            class_2672 chunkData = packet;
            this.queueChunk(class_1923.method_8331(chunkData.method_11523(), chunkData.method_11524()), true);
            return;
        }
        if (packet instanceof class_2637) {
            class_2637 deltaUpdate = packet;
            deltaUpdate.method_30621(this::lambda$onPacketReceive$0);
            return;
        }
        if (packet instanceof class_2626) {
            class_2626 blockUpdate = packet;
            this.queueChunk(new class_1923(blockUpdate.method_11309()).method_8324(), true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null || this.cachedBlocks.isEmpty()) {
            return;
        }
        if (!((Boolean)this.esp.getValue()).booleanValue() && !((Boolean)this.tracers.getValue()).booleanValue()) {
            return;
        }
        Set<class_2248> localTargets = this.targets;
        if (localTargets.isEmpty()) {
            return;
        }
        class_4184 cam = RenderUtils.getCamera();
        if (cam == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(cam);
        class_243 cameraForward = RenderUtils.getCameraForward(cam);
        class_243 cameraRight = RenderUtils.getCameraRight(cam);
        class_243 cameraUp = RenderUtils.getCameraUp(cameraForward, cameraRight);
        class_243 tracerStart = cameraForward.method_1021(150);
        double maxDistanceSq = this.getMaxRenderDistanceSq();
        WorldBatch batch = RenderUtils.beginWorldBatch(matrices);
        boolean rendered = false;
        GL11.glDisable(2929);
        try {
            for (Set<class_2338> positions : this.cachedBlocks.values()) {
                for (class_2338 pos : positions) {
                    while (pos.method_40081(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321()) > maxDistanceSq) {
                    }
                    class_2248 block = this.posTypeMap.get(pos);
                    if (block == null) {
                        class_2680 state = mc.field_1687.method_8320(pos);
                        block = state.method_26204();
                    }
                    while (!localTargets.contains(block)) {
                    }
                    Color color = this.getBlockColor(block, 110);
                    double x = pos.method_10263() - camPos.field_1352;
                    double y = pos.method_10264() - camPos.field_1351;
                    double z = pos.method_10260() - camPos.field_1350;
                    Color outline = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
                    if (((Boolean)this.esp.getValue()).booleanValue()) {
                        batch.renderOutlineBox(x + 0.0625, y + 0.0625, z + 0.0625, x + 1 - 0.0625, y + 1 - 0.0625, z + 1 - 0.0625, outline);
                        batch.renderFilledBox(x + 0.0625, y + 0.0625, z + 0.0625, x + 1 - 0.0625, y + 1 - 0.0625, z + 1 - 0.0625, color);
                    }
                    if (((Boolean)this.tracers.getValue()).booleanValue()) {
                        class_243 relativeTarget = new class_243(x + 0.5, y + 0.5, z + 0.5);
                        class_243 tracerEnd = RenderUtils.getSpreadTracerEnd(relativeTarget, cameraForward, cameraRight, cameraUp, 24, 2.75);
                        batch.renderLine(outline, tracerStart, tracerEnd, XenonPlus.tracerLineWidth());
                    }
                    rendered = true;
                }
            }
            if (rendered) {
                batch.flush();
            }
        }
        finally {
            GL11.glEnable(2929);
            throw var29;
        }
    }

    private void updateTargets() {
        long version = this.blocks.getVersion();
        if (version == this.lastBlocksVersion) {
            return;
        }
        this.lastBlocksVersion = version;
        this.targets = Set.copyOf(this.blocks.getSelectedBlocks());
        this.clearCaches();
        this.fullRescanRequested = true;
    }

    public boolean isSelected(class_2248 block) {
        this.updateTargets();
        return this.blocks.contains(block);
    }

    public int getSelectedCount() {
        this.updateTargets();
        return this.blocks.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void rebuildLoadedChunkQueue(boolean forceRescan) {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        int viewDist = this.getChunkRadius();
        class_1923 center = mc.field_1724.method_31476();
        List<class_2818> loadedChunks = new ArrayList();
        Set<Long> loadedChunkKeys = new HashSet();
        for (int x = -viewDist; x <= viewDist; x++) {
            for (int z = -viewDist; z <= viewDist; z++) {
                class_2818 chunk = mc.field_1687.method_2935().method_12126(center.field_9181 + x, center.field_9180 + z, false);
                if (chunk != null) {
                    loadedChunks.add(chunk);
                    loadedChunkKeys.add(chunk.method_12004().method_8324());
                }
            }
        }
        loadedChunks.sort(Comparator.comparingInt(this::lambda$rebuildLoadedChunkQueue$1 /* captured: center */));
        var x = this.queueLock;
        synchronized (this.queueLock) {
        this.scanQueue.removeIf(BlockESP::lambda$rebuildLoadedChunkQueue$2 /* captured: loadedChunkKeys */);
        this.queuedChunks.retainAll(loadedChunkKeys);
        for (class_2818 chunk : loadedChunks) {
            long chunkKey = chunk.method_12004().method_8324();
            boolean shouldQueue = forceRescan || !this.cachedBlocks.containsKey(chunkKey);
            if (shouldQueue) {
                if (!this.queuedChunks.add(chunkKey)) continue;
                this.scanQueue.addLast(chunkKey);
            }
        }
        }
        this.pruneOutOfRange(center, viewDist);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void queueChunk(long chunkKey, boolean prioritized) {
        var var4 = this.queueLock;
        synchronized (this.queueLock) {
        if (prioritized) {
            if (this.queuedChunks.contains(chunkKey)) {
                this.scanQueue.remove(chunkKey);
                this.scanQueue.addFirst(chunkKey);
                }
                return;
            }
        }
    }

    private void scanChunk(class_2818 chunk) {
        Set<class_2248> localTargets = this.targets;
        if (localTargets.isEmpty()) {
            return;
        }
        int worldBottom = mc.field_1687.method_31607();
        int worldTopExclusive = mc.field_1687.method_31607() + mc.field_1687.method_31605();
        int minSection = mc.field_1687.method_32891();
        class_1923 chunkPos = chunk.method_12004();
        long chunkKey = chunkPos.method_8324();
        Set<class_2338> oldSet = this.cachedBlocks.get(chunkKey);
        Set<class_2338> newSet = new HashSet();
        class_2248 firstNewBlock = null;
        class_2338 firstNewPos = null;
        class_2826[] sections = chunk.method_12006();
        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            class_2826 section = sections[sectionIndex];
            if (section != null) {
                if (!(section.method_38292())) {
                    int sectionYBase = (minSection + sectionIndex) * 16;
                    if (!(sectionYBase + 16 <= worldBottom)) {
                        if (!(sectionYBase >= worldTopExclusive)) {
                            for (int localX = 0; localX < 16; localX++) {
                                for (int localZ = 0; localZ < 16; localZ++) {
                                    for (int localY = 0; localY < 16; localY++) {
                                        class_2680 state = section.method_12254(localX, localY, localZ);
                                        class_2248 block = state.method_26204();
                                        if (localTargets.contains(block)) {
                                            class_2338 pos = new class_2338(chunkPos.method_8326() + localX, sectionYBase + localY, chunkPos.method_8328() + localZ);
                                            newSet.add(pos);
                                            this.posTypeMap.put(pos, block);
                                            if (firstNewBlock == null) {
                                                if (oldSet == null || !oldSet.contains(pos)) {
                                                    firstNewBlock = block;
                                                    firstNewPos = pos;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (oldSet != null) {
            for (class_2338 pos : oldSet) {
                if (newSet.contains(pos)) continue;
                this.posTypeMap.remove(pos);
            }
        }
        if (newSet.isEmpty()) {
            this.removeChunkCache(chunkKey);
            this.lastNotifiedAt.remove(chunkKey);
            return;
        }
        this.cachedBlocks.put(chunkKey, newSet);
        if (firstNewBlock != null) {
            if (firstNewPos != null) {
                this.maybeNotify(chunkKey, firstNewBlock, firstNewPos, chunkPos);
            }
        }
    }

    private void maybeNotify(long chunkKey, class_2248 block, class_2338 pos, class_1923 chunkPos) {
        if (!((Boolean)this.notify.getValue()).booleanValue() || mc.field_1724 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        long last = ((Long)this.lastNotifiedAt.getOrDefault(chunkKey, 0L)).longValue();
        if (now - last < 750L) {
            return;
        }
        this.lastNotifiedAt.put(chunkKey, now);
        NotificationManager.INSTANCE.push(this.safeBlockName(block) + " found", "X " + pos.method_10263() + "  Y " + pos.method_10264() + "  Z " + pos.method_10260(), this.createNotificationStack(block), this.getBlockColor(block, 255).getRGB());
        mc.field_1687.method_43128(mc.field_1724, mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), class_3417.field_14627, class_3419.field_15250, 0.6f, 0.95f);
    }

    private class_1799 createNotificationStack(class_2248 block) {
        class_1799 stack = new class_1799(block.method_8389());
        return stack.method_7960() ? class_1799.field_8037 : stack;
    }

    private int getChunkDistanceSq(class_1923 origin, class_1923 target) {
        int dx = target.field_9181 - origin.field_9181;
        int dz = target.field_9180 - origin.field_9180;
        return dx * dx + dz * dz;
    }

    private int getChunkRadius() {
        return mc.field_1690.method_38521();
    }

    private double getMaxRenderDistanceSq() {
        double maxDistance = this.getChunkRadius() * 16 + 16;
        return maxDistance * maxDistance;
    }

    private void pruneOutOfRange(class_1923 center, int chunkRadius) {
        List<Long> toRemove = new ArrayList();
        for (Long chunkKey : this.cachedBlocks.keySet()) {
            class_1923 chunkPos = new class_1923(class_1923.method_8325(chunkKey.longValue()), class_1923.method_8332(chunkKey.longValue()));
            if (Math.abs(chunkPos.field_9181 - center.field_9181) <= chunkRadius || Math.abs(chunkPos.field_9180 - center.field_9180) > chunkRadius) continue;
            toRemove.add(chunkKey);
        }
        for (Long chunkKey : toRemove) {
            this.removeChunkCache(chunkKey.longValue());
            this.lastNotifiedAt.remove(chunkKey);
        }
    }

    private void removeChunkCache(long chunkKey) {
        Set<class_2338> removed = this.cachedBlocks.remove(chunkKey);
        if (removed == null) {
            return;
        }
        for (class_2338 pos : removed) {
            this.posTypeMap.remove(pos);
        }
    }

    private Color getBlockColor(class_2248 block, int alphaValue) {
        class_2960 id = class_7923.field_41175.method_10221(block);
        String path = id == null ? "" : id.method_12832();
        if (block == class_2246.field_10260) {
            return new Color(138, 126, 166, alphaValue);
        }
        if (path.contains("diamond")) {
            return new Color(0, 255, 255, alphaValue);
        }
        if (path.contains("ancient_debris")) {
            return new Color(196, 120, 72, alphaValue);
        }
        if (path.contains("emerald")) {
            return new Color(0, 255, 127, alphaValue);
        }
        if (path.contains("gold")) {
            return new Color(255, 215, 0, alphaValue);
        }
        if (path.contains("iron")) {
            return new Color(213, 213, 213, alphaValue);
        }
        if (path.contains("redstone")) {
            return new Color(255, 70, 70, alphaValue);
        }
        if (path.contains("lapis")) {
            return new Color(70, 110, 255, alphaValue);
        }
        return new Color(255, 255, 0, alphaValue);
    }

    private String safeBlockName(class_2248 block) {
        try {
            return block.method_9518().getString();
        }
        catch (Exception ignored) {
            class_2960 id = class_7923.field_41175.method_10221(block);
            return id == null ? "Block" : id.toString();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearCaches() {
        this.cachedBlocks.clear();
        this.posTypeMap.clear();
        this.lastNotifiedAt.clear();
        var var1 = this.queueLock;
        synchronized (this.queueLock) {
        this.scanQueue.clear();
        this.queuedChunks.clear();
        }
    }
}
