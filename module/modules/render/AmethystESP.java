/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1923;
import net.minecraft.class_1944;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import org.lwjgl.opengl.GL11;

public final class AmethystESP
extends Module {
    private static final double TRACER_START_DISTANCE = 150;
    private static final double TRACER_END_DISTANCE = 24;
    private static final double TRACER_BEHIND_SPREAD = 2.75;
    private static final double CHUNK_THICKNESS = 0.05;
    private final Setting<Float> simDistance = new Setting("Sim Distance", 8f, 1f, 32f);
    private final Setting<Float> clusterThreshold = new Setting("Min Cluster Size", 3f, 1f, 20f);
    private final Setting<Float> chunkY = new Setting("Chunk Y Level", 55f, -64f, 320f);
    private final Setting<Boolean> esp = new Setting("Block ESP", true);
    private final Setting<Boolean> chunkMark = new Setting("Chunk Mark", true);
    private final Setting<Boolean> tracers = new Setting("Show Tracers", true);
    private final Setting<Boolean> chatNotify = new Setting("Chat Alert", true);
    private final Setting<Color> espColor = new Setting("ESP Color", new Color(180, 100, 255));
    private final Setting<Color> chunkColor = new Setting("Chunk Color", new Color(180, 100, 255));
    private static AmethystESP INSTANCE;
    private final Map<class_1923, Set<class_2338>> foundClusters = new ConcurrentHashMap();
    private final Set<class_1923> notifiedChunks = ConcurrentHashMap.newKeySet();
    private int tickCounter = 0;

    public AmethystESP() {
        super("Amethyst ESP", Category.RENDER);
        INSTANCE = this;
        this.addSetting(this.simDistance);
        this.addSetting(this.clusterThreshold);
        this.addSetting(this.chunkY);
        this.addSetting(this.esp);
        this.addSetting(this.chunkMark);
        this.addSetting(this.tracers);
        this.addSetting(this.chatNotify);
        this.addSetting(this.espColor);
        this.addSetting(this.chunkColor);
    }

    public static AmethystESP instance() {
        return INSTANCE;
    }

    public void onEnable() {
        this.foundClusters.clear();
        this.notifiedChunks.clear();
        this.fullScan();
    }

    public void onDisable() {
        this.foundClusters.clear();
        this.notifiedChunks.clear();
    }

    private void fullScan() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        class_1923 center = mc.field_1724.method_31476();
        int radius = ((Float)this.simDistance.getValue()).intValue();
        int scanned = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                class_2818 chunk = mc.field_1687.method_2935().method_12126(center.field_9181 + x, center.field_9180 + z, false);
                if (chunk != null) {
                    this.scanChunk(chunk);
                    scanned++;
                }
            }
        }
        this.sendChat("\u00a77Scan: " + scanned + " Chunks | \u00a7d" + this.foundClusters.size() + " \u00a77Geoden");
    }

    private void scanChunk(class_2818 chunk) {
        if (mc.field_1687 == null) {
            return;
        }
        class_1923 cp = chunk.method_12004();
        Set<class_2338> hits = new HashSet();
        int baseX = cp.field_9181 << 4;
        int baseZ = cp.field_9180 << 4;
        for (int y = -64; y <= 70; y++) {
            for (int lx = 0; lx < 16; lx++) {
                for (int lz = 0; lz < 16; lz++) {
                    class_2338 pos = new class_2338(baseX + lx, y, baseZ + lz);
                    if (y <= 50) {
                        if (mc.field_1687.method_8314(class_1944.field_9282, pos) == 5) {
                            if (!this.isAmethystNearby(pos)) continue;
                            hits.add(pos.method_10062());
                        }
                    }
                }
            }
        }
        if (hits.size() >= ((Float)this.clusterThreshold.getValue()).intValue()) {
            this.foundClusters.put(cp, hits);
            if (((Boolean)this.chatNotify.getValue()).booleanValue()) {
                if (this.notifiedChunks.add(cp)) {
                    this.sendChat("Amethyst Clusters on " + cp.method_33940() + " " + cp.method_33942() + " \u00a77(" + hits.size() + " hits)");
                }
            }
        } else {
            this.foundClusters.remove(cp);
            this.notifiedChunks.remove(cp);
        }
    }

    private boolean isAmethystNearby(class_2338 pos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    class_2680 state = mc.field_1687.method_8320(pos.method_10069(dx, dy, dz));
                    if (!state.method_27852(class_2246.field_27161) || state.method_27852(class_2246.field_27162) || state.method_27852(class_2246.field_27163) || state.method_27852(class_2246.field_27164) || state.method_27852(class_2246.field_27160) || state.method_27852(class_2246.field_27159)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public void onTick() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        this.tickCounter = this.tickCounter + 1;
        if ((this.tickCounter + 1) % 40 != 0) { /* goto L129; */ }
        class_1923 center = mc.field_1724.method_31476();
        int radius = ((Float)this.simDistance.getValue()).intValue();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                class_2818 chunk = mc.field_1687.method_2935().method_12126(center.field_9181 + x, center.field_9180 + z, false);
                if (chunk == null) continue;
                this.scanChunk(chunk);
            }
        }
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null || this.foundClusters.isEmpty()) {
            return;
        }
        class_4184 camera = RenderUtils.getCamera();
        if (camera == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(camera);
        class_243 camForward = RenderUtils.getCameraForward(camera);
        class_243 camRight = RenderUtils.getCameraRight(camera);
        class_243 camUp = RenderUtils.getCameraUp(camForward, camRight);
        class_243 tracerStart = camForward.method_1021(150);
        Color espFill = this.withAlpha((Color)this.espColor.getValue(), 180);
        Color chunkFill = this.withAlpha((Color)this.chunkColor.getValue(), 200);
        Color tracer = this.withAlpha((Color)this.espColor.getValue(), 220);
        matrices.method_22903();
        GL11.glDisable(2929);
        WorldBatch batch = RenderUtils.beginWorldBatch(matrices);
        GL11.glDisable(2929);
        double fixedY = ((Float)this.chunkY.getValue()).doubleValue();
        for (Map.Entry<class_1923, Set<class_2338>> entry : this.foundClusters.entrySet()) {
            class_1923 cp = entry.getKey();
            Set<class_2338> positions = entry.getValue();
            while (positions.isEmpty()) {
            }
            if (((Boolean)this.chunkMark.getValue()).booleanValue()) {
                double x1 = cp.method_8326() - camPos.field_1352;
                double z1 = cp.method_8328() - camPos.field_1350;
                double x2 = cp.method_8327() - camPos.field_1352 + 1;
                double z2 = cp.method_8329() - camPos.field_1350 + 1;
                double y1 = fixedY - camPos.field_1351;
                double y2 = y1 + 0.05;
                batch.renderFilledBox(x1, y1, z1, x2, y2, z2, chunkFill);
            }
            if ((Boolean)this.esp.getValue()).booleanValue() {
                for (class_2338 p : positions) {
                    double x1 = p.method_10263() - camPos.field_1352;
                    double y1 = p.method_10264() - camPos.field_1351;
                    double z1 = p.method_10260() - camPos.field_1350;
                    batch.renderFilledBox(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1, espFill);
                }
            }
            if (!(Boolean)this.tracers.getValue()).booleanValue() continue;
            nearest = null;
            double nearestDist = 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000;
            for (class_2338 p : positions) {
                double dist = p.method_10262(mc.field_1724.method_24515());
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = p;
                }
            }
            if (nearest != null) {
                relTarget = new class_243((double)nearest.method_10263() + 0.5 - camPos.field_1352, (double)nearest.method_10264() + 0.5 - camPos.field_1351, (double)nearest.method_10260() + 0.5 - camPos.field_1350);
                class_243 tracerEnd = RenderUtils.getSpreadTracerEnd(relTarget, camForward, camRight, camUp, 24, 2.75);
                batch.renderLine(tracer, tracerStart, tracerEnd, XenonPlus.tracerLineWidth());
            }
        }
        batch.flush();
        GL11.glEnable(2929);
        matrices.method_22909();
    }

    private Color withAlpha(Color base, int alpha) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), Math.min(255, alpha));
    }

    public static void onChunkData(int cx, int cz) {
        if (INSTANCE == null || !INSTANCE.isEnabled()) {
            return;
        }
        class_2818 chunk = class_310.method_1551().field_1687.method_2935().method_12126(cx, cz, false);
        if (chunk != null) {
            INSTANCE.scanChunk(chunk);
        }
    }

    public static void onBlockUpdate(class_2338 pos, class_2680 state) {
        AmethystESP.onChunkData(pos.method_10263() >> 4, pos.method_10260() >> 4);
    }

    public static void onParticle(String type, double x, double y, double z) {
    }

    public static void renderHud(class_332 context, float delta) {
        if (INSTANCE == null || !INSTANCE.isEnabled()) {
            return;
        }
        context.method_51433(class_310.method_1551().field_1772, "\u00a7dAmethystESP: \u00a7f" + INSTANCE.foundClusters.size() + " Geoden", 10, 10, -1, true);
    }

    private void sendChat(String message) {
        if (mc.field_1724 != null) {
            mc.field_1724.method_7353(class_2561.method_43470("\u00a7d[AmethystESP] \u00a7f" + message), false);
        }
    }
}
