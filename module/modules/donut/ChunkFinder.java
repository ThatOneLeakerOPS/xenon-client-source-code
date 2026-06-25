/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_4184;
import net.minecraft.class_4481;
import net.minecraft.class_4587;

public final class ChunkFinder
extends Module {
    private static final int BEEHIVE_MIN_LOADED_TICKS = 200;
    private static final int CHUNKS_PER_TICK = 8;
    private static final Color FILL_COLOR = new Color(5, 130, 45, 190);
    private final Set<class_1923> flaggedChunks = ConcurrentHashMap.newKeySet();
    private final Set<class_1923> notifiedChunks = ConcurrentHashMap.newKeySet();
    private final Map<class_1923, Integer> firstLoadedTicks = new ConcurrentHashMap();
    private int scanCursor;
    private int tickCounter;

    public ChunkFinder() {
        super("ChunkFinder", Category.DONUT);
    }

    public void onEnable() {
        this.reset();
    }

    public void onDisable() {
        this.reset();
    }

    public void onTick() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        this.tickCounter = this.tickCounter + 1;
        int radius = mc.field_1690.method_38521();
        class_1923 playerChunk = mc.field_1724.method_31476();
        int side = radius * 2 + 1;
        int total = side * side;
        for (int i = 0; i < 8; i++) {
            int index = this.scanCursor % total;
            this.scanCursor = (this.scanCursor + 1) % total;
            int dx = index % side - radius;
            int dz = index / side - radius;
            int cx = playerChunk.field_9181 + dx;
            int cz = playerChunk.field_9180 + dz;
            class_2818 chunk = mc.field_1687.method_2935().method_12126(cx, cz, false);
            if (chunk != null) {
                if (!(chunk.method_12223())) {
                    class_1923 pos = new class_1923(cx, cz);
                    this.firstLoadedTicks.putIfAbsent(pos, this.tickCounter);
                    boolean longLoaded = this.tickCounter - ((Integer)this.firstLoadedTicks.get(pos)).intValue() >= 200;
                    if (longLoaded) {
                        if (this.hasFullBeehive(chunk)) {
                            this.flaggedChunks.add(pos);
                            if (!this.notifiedChunks.add(pos)) continue;
                            this.showToast(pos);
                        }
                    } else {
                        this.flaggedChunks.remove(pos);
                    }
                }
            }
        }
        this.flaggedChunks.removeIf(this::lambda$onTick$0 /* captured: playerChunk, radius */);
        this.firstLoadedTicks.keySet().removeIf(this::lambda$onTick$1 /* captured: playerChunk, radius */);
    }

    private boolean hasFullBeehive(class_2818 chunk) {
        for (class_2826 section : chunk.method_12006()) {
            if (section != null) {
                if (!(section.method_38292())) {
                    if (section.method_19523(ChunkFinder::lambda$hasFullBeehive$2)) {
                        for (int lx = 0; lx < 16; lx++) {
                            for (int lz = 0; lz < 16; lz++) {
                                for (int ly = 0; ly < 16; ly++) {
                                    class_2680 state = section.method_12254(lx, ly, lz);
                                    if (state.method_27852(class_2246.field_20422) || state.method_27852(class_2246.field_20421)) {
                                        if (state.method_28498(class_4481.field_20420)) {
                                            if (((Integer)state.method_11654(class_4481.field_20420)).intValue() != 5) continue;
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null || this.flaggedChunks.isEmpty()) {
            return;
        }
        class_4184 camera = RenderUtils.getCamera();
        if (camera == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(camera);
        matrices.method_22903();
        WorldBatch batch = RenderUtils.beginWorldBatch(matrices);
        for (class_1923 chunk : this.flaggedChunks) {
            double x1 = chunk.method_8326() - camPos.field_1352;
            double z1 = chunk.method_8328() - camPos.field_1350;
            double x2 = x1 + 16;
            double z2 = z1 + 16;
            double y1 = 55 - camPos.field_1351;
            double y2 = y1 + 0.001;
            batch.renderFilledBox(x1, y1, z1, x2, y2, z2, FILL_COLOR);
        }
        batch.flush();
        matrices.method_22909();
    }

    private boolean outOfRange(class_1923 chunk, class_1923 player, int radius) {
        return Math.abs(chunk.field_9181 - player.field_9181) > radius || Math.abs(chunk.field_9180 - player.field_9180) > radius;
    }

    private void showToast(class_1923 chunk) {
        if (mc.method_1566() == null) {
            return;
        }
        mc.method_1566().method_1999(new ChunkFinderToast(class_2561.method_43470("ChunkFinder"), class_2561.method_43470("Full beehive at X:" + chunk.method_33940() + " Z:" + chunk.method_33942()), new class_1799(class_1802.field_8449)));
    }

    private void reset() {
        this.flaggedChunks.clear();
        this.notifiedChunks.clear();
        this.firstLoadedTicks.clear();
        this.scanCursor = 0;
        this.tickCounter = 0;
    }
}
