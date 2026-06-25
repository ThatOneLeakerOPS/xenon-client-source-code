/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2541;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2902;
import net.minecraft.class_3830;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_5689;

public final class GrowthFinder
extends Module {
    private final Setting<Boolean> renderVines = new Setting("Render Vines", true);
    private final Setting<Boolean> renderDripstone = new Setting("Render Dripstone", true);
    private final Setting<Boolean> renderBerries = new Setting("Render Berries", true);
    private final Setting<Boolean> renderStandardChunks = new Setting("Render Gray Chunks", true);
    private final Setting<Float> alpha = new Setting("Alpha", 80f, 0f, 255f);
    private static final int SCAN_RADIUS = 8;
    private static final int CHUNKS_PER_TICK = 12;
    private static final int MIN_VINE_LENGTH = 6;
    private static final int MAX_VINE_SCAN_PER_CHUNK = 2;
    private static final double PLATE_HEIGHT = 0.08;
    private static final Color HIGH_SUSPICION_GRAY = new Color(135, 135, 135);
    private static final Color LOW_SUSPICION_GRAY = new Color(100, 100, 100);
    private static final Color SOURCE_PLATE_COLOR_BASE = new Color(203, 64, 255);
    private static final Color EXTREME_PLATE_COLOR_BASE = new Color(255, 198, 64);
    private final Set<class_1923> scannedChunks = new HashSet();
    private final Map<class_1923, SuspiciousGrowthData> suspiciousChunks = new HashMap();
    private final List<class_1923> scanQueue = new ArrayList();
    private class_1923 lastQueueCenter = null;
    private int scanCursor = 0;
    private final List<class_1923> lockedBaseChunks = new ArrayList();
    private final Map<class_1923, Double> sourceHistory = new HashMap();

    public GrowthFinder() {
        super("Growth Finder", Category.DONUT);
        this.addSetting(this.renderVines);
        this.addSetting(this.renderDripstone);
        this.addSetting(this.renderBerries);
        this.addSetting(this.renderStandardChunks);
        this.addSetting(this.alpha);
    }

    public void onEnable() {
        this.clearData();
    }

    public void onDisable() {
        this.clearData();
    }

    private void clearData() {
        this.suspiciousChunks.clear();
        this.scannedChunks.clear();
        this.scanQueue.clear();
        this.scanCursor = 0;
        this.lastQueueCenter = null;
        this.lockedBaseChunks.clear();
        this.sourceHistory.clear();
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        class_1923 playerChunk = new class_1923(mc.field_1724.method_24515());
        this.rebuildScanQueueIfNeeded(playerChunk);
        this.processChunkBatch(mc.field_1687);
        this.pruneFarChunks(playerChunk, 8);
        if (mc.field_1687.method_75260() % 20L == 0L) {
            this.refreshLockedBaseChunk();
        }
    }

    private void rebuildScanQueueIfNeeded(class_1923 playerChunk) {
        if (this.lastQueueCenter != null) {
            if (playerChunk.field_9181 == this.lastQueueCenter.field_9181) {
                if (playerChunk.field_9180 != this.lastQueueCenter.field_9180) { /* goto @51; */ }
            }
        }
        this.scanQueue.clear();
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                this.scanQueue.add(new class_1923(playerChunk.field_9181 + x, playerChunk.field_9180 + z));
            }
        }
        this.scanCursor = 0;
        this.lastQueueCenter = playerChunk;
    }

    private void processChunkBatch(class_1937 world) {
        int processed = 0;
        while (this.scanCursor < this.scanQueue.size()) {
            if (processed >= 12) break;
            this.scanCursor = this.scanCursor + 1;
            class_1923 chunkPos = this.scanQueue.get(this.scanCursor);
            while (this.scannedChunks.contains(chunkPos)) {
            }
            class_2818 chunk = world.method_8398().method_12126(chunkPos.field_9181, chunkPos.field_9180, false);
            if (chunk != null) {
                this.analyzeChunk(chunk, chunkPos);
                this.scannedChunks.add(chunkPos);
            }
            processed++;
        }
    }

    private void pruneFarChunks(class_1923 playerChunk, int radius) {
        int maxDistance = radius + 2;
        int pX = playerChunk.field_9181;
        int pZ = playerChunk.field_9180;
        this.suspiciousChunks.entrySet().removeIf(GrowthFinder::lambda$pruneFarChunks$0 /* captured: pX, maxDistance, pZ */);
        this.scannedChunks.removeIf(GrowthFinder::lambda$pruneFarChunks$1 /* captured: pX, maxDistance, pZ */);
    }

    private void refreshLockedBaseChunk() {
        if (this.suspiciousChunks.isEmpty()) {
            this.sourceHistory.clear();
            return;
        }
        for (SuspiciousGrowthData data : this.suspiciousChunks.values()) {
            class_1923 sourceChunk = data.baseChunk;
            this.sourceHistory.merge(sourceChunk, (double)data.suspicionLevel, Double::sum);
        }
        this.sourceHistory.entrySet().removeIf(GrowthFinder::lambda$refreshLockedBaseChunk$2);
        ranked = new ArrayList(this.sourceHistory.entrySet());
        ranked.sort(GrowthFinder::lambda$refreshLockedBaseChunk$3);
        this.lockedBaseChunks.clear();
        for (int i = 0; i < Math.min(2, ranked.size()); i++) {
            this.lockedBaseChunks.add((class_1923)((Map.Entry)ranked.get(i)).getKey());
        }
    }

    private void analyzeChunk(class_2818 chunk, class_1923 chunkPos) {
        int xStart = chunkPos.method_8326();
        int zStart = chunkPos.method_8328();
        List<VineCluster> vineClusters = ((Boolean)this.renderVines.getValue()).booleanValue() ? this.detectTallGroundedVines(chunk, xStart, zStart) : Collections.emptyList();
        List<DripstoneCluster> dripstoneClusters = ((Boolean)this.renderDripstone.getValue()).booleanValue() ? this.detectMaxDripstoneClusters(chunk, xStart, zStart) : Collections.emptyList();
        List<BerryCluster> berryClusters = ((Boolean)this.renderBerries.getValue()).booleanValue() ? this.detectMaxGrownBerries(chunk, xStart, zStart) : Collections.emptyList();
        List<WeightedEvidence> evidencePoints = new ArrayList();
        double vineWeight = 0;
        int maxVineLength = 0;
        for (VineCluster cluster : vineClusters) {
            double weight = cluster.length;
            vineWeight = vineWeight + weight;
            if (cluster.length <= maxVineLength) continue;
            maxVineLength = cluster.length;
            evidencePoints.add(new WeightedEvidence(cluster.centroid(), weight));
        }
        dripWeight = 0;
        for (DripstoneCluster cluster : dripstoneClusters) {
            dripWeight = dripWeight + 2.5;
            evidencePoints.add(new WeightedEvidence(cluster.center, 2.5));
        }
        berryWeight = 0;
        for (BerryCluster cluster : berryClusters) {
            berryWeight = berryWeight + 1;
            evidencePoints.add(new WeightedEvidence(cluster.pos, 1));
        }
        suspicionLevel = (int)Math.round(vineWeight * 0.5 + dripWeight * 0.75 + berryWeight * 1);
        if (suspicionLevel > 0) {
            class_2338 estimatedSource = this.calculateWeightedSource(evidencePoints, chunkPos);
            boolean isExtreme = maxVineLength >= 100;
            boolean isSource = maxVineLength >= 25;
            this.suspiciousChunks.put(chunkPos, new SuspiciousGrowthData(chunkPos, suspicionLevel, new class_1923(estimatedSource), isExtreme, isSource, maxVineLength));
        } else {
            this.suspiciousChunks.remove(chunkPos);
        }
    }

    private List<VineCluster> detectTallGroundedVines(class_2818 chunk, int xStart, int zStart) {
        List<VineCluster> clusters = new ArrayList();
        Set<class_2338> visited = new HashSet();
        int groundY = chunk.method_31607();
        int tallClusters = 0;
        for (int x = xStart; x < xStart + 16; x += 2) {
            int z = zStart;
            while (z < zStart + 16) {
                if (!(tallClusters >= 2)) {
                    int y = chunk.method_12032(class_2902.class_2903.field_13197).method_12603(x - xStart, z - zStart);
                    while (y >= groundY) {
                        class_2338 pos = new class_2338(x, y, z);
                        while (visited.contains(pos)) {
                            y--;
                        }
                        class_2680 state = chunk.method_8320(pos);
                        while (!this.isVineBlock(state.method_26204())) {
                            y--;
                        }
                        List<class_2338> positions = new ArrayList();
                        class_2338 currTrace = pos;
                        while (currTrace.method_10264() >= groundY) {
                            if (!this.isVineBlock(chunk.method_8320(currTrace).method_26204())) break;
                            visited.add(currTrace);
                            positions.add(currTrace);
                            currTrace = currTrace.method_10074();
                        }
                        if (positions.size() >= 6) {
                            clusters.add(new VineCluster(positions));
                            tallClusters++;
                        }
                        y = currTrace.method_10264() - 1;
                    }
                    z += 2;
                }
            }
        }
        return clusters;
    }

    private boolean isVineBlock(class_2248 block) {
        return block instanceof class_2541 || block == class_2246.field_28675 || block == class_2246.field_28676 || block == class_2246.field_22123 || block == class_2246.field_22124 || block == class_2246.field_23078 || block == class_2246.field_23079;
    }

    private List<DripstoneCluster> detectMaxDripstoneClusters(class_2818 chunk, int xStart, int zStart) {
        List<DripstoneCluster> clusters = new ArrayList();
        for (int x = xStart; x < xStart + 16; x += 4) {
            for (int z = zStart; z < zStart + 16; z += 4) {
                int heightmapY = chunk.method_12032(class_2902.class_2903.field_13197).method_12603(x - xStart, z - zStart);
                int y = -64;
                while (y <= heightmapY) {
                    class_2338 pos = new class_2338(x, y, z);
                    if (chunk.method_8320(pos).method_26204() instanceof class_5689) {
                        clusters.add(new DripstoneCluster(pos));
                    } else {
                        y += 4;
                    }
                }
            }
        }
        return clusters;
    }

    private List<BerryCluster> detectMaxGrownBerries(class_2818 chunk, int xStart, int zStart) {
        List<BerryCluster> clusters = new ArrayList();
        for (int x = xStart; x < xStart + 16; x += 2) {
            int z = zStart;
            if (z < zStart + 16) {
                int heightmapY = chunk.method_12032(class_2902.class_2903.field_13197).method_12603(x - xStart, z - zStart);
                int y = heightmapY;
                if (y >= heightmapY - 5) {
                    class_2338 pos = new class_2338(x, y, z);
                    class_2680 state = chunk.method_8320(pos);
                }
            }
            try {
                if (((Integer)state.method_11654(class_3830.field_17000)).intValue() != 3) continue;
                clusters.add(new BerryCluster(pos));
            }
            catch (Exception e11) {
                y--;
                /* goto @57; */
                z += 2;
                /* goto @24; */
                x += 2;
            }
            y--;
            /* goto @57; */
            z += 2;
            /* goto @24; */
        }
        return clusters;
    }

    private class_2338 calculateWeightedSource(List<WeightedEvidence> evidencePoints, class_1923 chunkPos) {
        if (evidencePoints.isEmpty()) {
            return new class_2338(chunkPos.method_8326() + 8, 30, chunkPos.method_8328() + 8);
        }
        double sumX = 0;
        double sumY = 0;
        double sumZ = 0;
        double sumW = 0;
        for (WeightedEvidence e : evidencePoints) {
            sumX = sumX + (double)e.pos.method_10263() * e.weight;
            sumY = sumY + (double)e.pos.method_10264() * e.weight;
            sumZ = sumZ + (double)e.pos.method_10260() * e.weight;
            sumW = sumW + e.weight;
        }
        return new class_2338((int)(sumX / sumW), (int)(sumY / sumW), (int)(sumZ / sumW));
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        if (this.suspiciousChunks.isEmpty()) {
            return;
        }
        class_4184 cam = RenderUtils.getCamera();
        if (cam == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(cam);
        int alphaInt = Math.max(0, Math.min(255, ((Float)this.alpha.getValue()).intValue()));
        matrices.method_22903();
        WorldBatch batch = RenderUtils.beginWorldBatch(matrices);
        for (SuspiciousGrowthData data : this.suspiciousChunks.values()) {
            boolean isStandard = !data.extreme && !data.source;
            while (isStandard) {
                if (((Boolean)this.renderStandardChunks.getValue()).booleanValue()) break;
            }
            if (data.extreme) {
                Color baseColor = EXTREME_PLATE_COLOR_BASE;
            } else {
                if (data.source) {
                    Color baseColor = SOURCE_PLATE_COLOR_BASE;
                } else {
                    if (data.suspicionLevel >= 5 || data.maxVineLength >= 4) {
                        Color baseColor = HIGH_SUSPICION_GRAY;
                    } else {
                        Color baseColor = LOW_SUSPICION_GRAY;
                    }
                }
            }
            Color finalColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alphaInt);
            double startX = data.chunkPos.method_8326();
            double startZ = data.chunkPos.method_8328();
            double y = 30;
            double x1 = startX - camPos.field_1352;
            double z1 = startZ - camPos.field_1350;
            double x2 = startX + 16 - camPos.field_1352;
            double z2 = startZ + 16 - camPos.field_1350;
            double y1 = y - camPos.field_1351;
            double y2 = y + 0.08 - camPos.field_1351;
            batch.renderFilledBox(x1, y1, z1, x2, y2, z2, finalColor);
        }
        batch.flush();
        matrices.method_22909();
    }
}
