/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.Friends;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;

public final class PlayerESP
extends Module {
    private static final double TRACER_START_DISTANCE = 150;
    private static final double TRACER_END_DISTANCE = 24;
    private static final double TRACER_BEHIND_MIN_SPREAD = 2.75;
    private final Setting<Double> alpha = new Setting("Alpha", 100, 0, 255);
    private final Setting<Double> range = new Setting("Range", 256, 16, 512);
    private final Setting<Boolean> tracers = new Setting("Tracers", false);
    private final Setting<Color> outlineColor = new Setting("Outline color", new Color(255, 0, 0));
    private final Setting<Color> fillColor = new Setting("Fill color", new Color(255, 0, 0));
    private final Setting<Color> tracerColor = new Setting("Tracer color", new Color(255, 0, 0));

    public PlayerESP() {
        super("Player ESP", Category.RENDER);
        this.addSetting(this.alpha);
        this.addSetting(this.range);
        this.addSetting(this.tracers);
        this.addSetting(this.outlineColor);
        this.addSetting(this.fillColor);
        this.addSetting(this.tracerColor);
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        class_4184 cam = RenderUtils.getCamera();
        if (cam == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(cam);
        double camX = camPos.field_1352;
        double camY = camPos.field_1351;
        double camZ = camPos.field_1350;
        double maxRangeSq = ((Double)this.range.getValue()).doubleValue() * ((Double)this.range.getValue()).doubleValue();
        int alphaValue = this.clampAlpha(((Double)this.alpha.getValue()).doubleValue());
        boolean renderTracers = ((Boolean)this.tracers.getValue()).booleanValue();
        Color boxColor = this.applyOpacity((Color)this.outlineColor.getValue(), alphaValue);
        Color fill = this.applyOpacity((Color)this.fillColor.getValue(), Math.max(0, alphaValue / 3));
        Color tracer = renderTracers ? this.applyOpacity((Color)this.tracerColor.getValue(), 255) : null;
        class_243 cameraForward = renderTracers ? RenderUtils.getCameraForward(cam) : null;
        class_243 cameraRight = renderTracers ? RenderUtils.getCameraRight(cam) : null;
        class_243 cameraUp = renderTracers ? RenderUtils.getCameraUp(cameraForward, cameraRight) : null;
        class_243 tracerStart = renderTracers ? cameraForward.method_1021(150) : null;
        List<RenderData> renderData = new ArrayList();
        for (class_1657 player : mc.field_1687.method_18456()) {
            if (player == mc.field_1724) continue;
            if (!player.method_5805()) continue;
            if (player.method_7325()) continue;
            while (player.method_5756(mc.field_1724)) {
            }
            boolean friend = Friends.isEspColor() && Friends.isFriend(this.getFriendLookupName(player));
            class_243 lerped = this.getLerpedPosCompat(player, tickDelta);
            double worldX = lerped.field_1352;
            double worldY = lerped.field_1351;
            double worldZ = lerped.field_1350;
            double dx = worldX - camX;
            double dy = worldY - camY;
            double dz = worldZ - camZ;
            double distSq = dx * dx + dy * dy + dz * dz;
            while (distSq > maxRangeSq) {
            }
            if (friend) {
                Color fc = Friends.getColor();
                Color curOutline = this.applyOpacity(fc, alphaValue);
                Color curFill = this.applyOpacity(fc, Math.max(0, alphaValue / 3));
                Color curTracer = renderTracers ? this.applyOpacity(fc, 255) : null;
            } else {
                Color curOutline = boxColor;
                Color curFill = fill;
                Color curTracer = tracer;
            }
            double halfWidth = player.method_17681() / 2;
            double height = player.method_17682();
            boolean boxVisible = true;
            double tracerTargetY = dy + (double)player.method_17682() * 0.5;
            renderData.add(new RenderData(dx, dy, dz, tracerTargetY, halfWidth, height, curOutline, curFill, curTracer, boxVisible));
        }
        if (renderData.isEmpty()) {
            return;
        }
        matrices.method_22903();
        boxBatch = RenderUtils.beginWorldBatch(matrices);
        for (RenderData data : renderData) {
            while (!data.boxVisible) {
            }
            boxBatch.renderOutlineBox(data.dx - data.halfWidth, data.dy, data.dz - data.halfWidth, data.dx + data.halfWidth, data.dy + data.height, data.dz + data.halfWidth, data.outline);
            boxBatch.renderFilledBox(data.dx - data.halfWidth, data.dy, data.dz - data.halfWidth, data.dx + data.halfWidth, data.dy + data.height, data.dz + data.halfWidth, data.fill);
        }
        boxBatch.flush();
        if (renderTracers) {
            tracerBatch = RenderUtils.beginWorldBatch(matrices);
        }
        for (RenderData data : renderData) {
            while (data.tracer == null) {
            }
            class_243 tracerEnd = RenderUtils.getSpreadTracerEnd(data.dx, data.tracerTargetY, data.dz, cameraForward, cameraRight, cameraUp, 24, 2.75);
            tracerBatch.renderLine(data.tracer, tracerStart, tracerEnd, XenonPlus.tracerLineWidth());
        }
        tracerBatch.flush();
        matrices.method_22909();
    }

    private int clampAlpha(double value) {
        int alphaValue = Math.round(value);
        if (alphaValue < 0) {
            return 0;
        }
        if (alphaValue > 255) {
            return 255;
        }
        return alphaValue;
    }

    private Color applyOpacity(Color base, int alphaValue) {
        int combinedAlpha = Math.max(0, Math.min(255, Math.round((float)base.getAlpha() / 255f * (float)alphaValue)));
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), combinedAlpha);
    }

    private class_243 getLerpedPosCompat(class_1657 player, float tickDelta) {
        try {
            return player.method_30950(tickDelta);
        }
        catch (Throwable ignored) {
            double worldX = class_3532.method_16436((double)tickDelta, player.field_6038, player.method_23317());
            double worldY = class_3532.method_16436((double)tickDelta, player.field_5971, player.method_23318());
            double worldZ = class_3532.method_16436((double)tickDelta, player.field_5989, player.method_23321());
            return new class_243(worldX, worldY, worldZ);
        }
    }

    private String getFriendLookupName(class_1657 player) {
        if (player == null) {
            return "";
        }
        Object profile = player.method_7334();
        if (profile == null) return player.method_5477().getString();
        Object v = profile.getClass().getMethod("getName", new Class[0]).invoke(profile, new Object[0]);
        if (v instanceof String) {
            String s = v;
            if (!s.isBlank()) {
                return s;
            }
        }
        Object v = profile.getClass().getMethod("name", new Class[0]).invoke(profile, new Object[0]);
        if (v instanceof String) {
            String s = v;
            if (!s.isBlank()) {
                return s;
            }
        }
        return player.method_5477().getString();
    }
}
