/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.setting.MobsSetting;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;

public final class MobESP
extends Module {
    private static final double TRACER_START_DISTANCE = 150;
    private static final double TRACER_END_DISTANCE = 24;
    private static final double TRACER_BEHIND_MIN_SPREAD = 2.75;
    private final MobsSetting mobs = new MobsSetting("Mobs", new class_1299[0]);
    private final Setting<Double> alpha = new Setting("Alpha", 100, 0, 255);
    private final Setting<Double> range = new Setting("Range", 128, 16, 512);
    private final Setting<Boolean> tracers = new Setting("Tracers", false);
    private final Setting<Color> outlineColor = new Setting("Outline color", new Color(255, 80, 80));
    private final Setting<Color> fillColor = new Setting("Fill color", new Color(255, 80, 80));
    private final Setting<Color> tracerColor = new Setting("Tracer color", new Color(255, 80, 80));

    public MobESP() {
        super("Mob ESP", Category.RENDER);
        this.addSetting(this.mobs);
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
        Set<class_1299<?>> targets = this.mobs.getSelectedMobs();
        if (targets.isEmpty()) {
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
        for (class_1297 entity : mc.field_1687.method_18112()) {
            while (entity == mc.field_1724) {
            }
            while (entity instanceof class_1657) {
            }
            if (!(entity instanceof class_1309)) continue;
            while (!entity.method_5805()) {
            }
            while (!targets.contains(entity.method_5864())) {
            }
            class_243 lerped = this.getLerpedPosCompat(entity, tickDelta);
            double dx = lerped.field_1352 - camX;
            double dy = lerped.field_1351 - camY;
            double dz = lerped.field_1350 - camZ;
            double distSq = dx * dx + dy * dy + dz * dz;
            while (distSq > maxRangeSq) {
            }
            double halfWidth = entity.method_17681() / 2;
            double height = entity.method_17682();
            double tracerTargetY = dy + height * 0.5;
            renderData.add(new RenderData(dx, dy, dz, tracerTargetY, halfWidth, height));
        }
        if (renderData.isEmpty()) {
            return;
        }
        matrices.method_22903();
        boxBatch = RenderUtils.beginWorldBatch(matrices);
        for (RenderData d : renderData) {
            boxBatch.renderOutlineBox(d.dx - d.halfWidth, d.dy, d.dz - d.halfWidth, d.dx + d.halfWidth, d.dy + d.height, d.dz + d.halfWidth, boxColor);
            boxBatch.renderFilledBox(d.dx - d.halfWidth, d.dy, d.dz - d.halfWidth, d.dx + d.halfWidth, d.dy + d.height, d.dz + d.halfWidth, fill);
        }
        boxBatch.flush();
        if (renderTracers) {
            tb = RenderUtils.beginWorldBatch(matrices);
        }
        for (RenderData d : renderData) {
            class_243 tracerEnd = RenderUtils.getSpreadTracerEnd(d.dx, d.tracerTargetY, d.dz, cameraForward, cameraRight, cameraUp, 24, 2.75);
            tb.renderLine(tracer, tracerStart, tracerEnd, XenonPlus.tracerLineWidth());
        }
        tb.flush();
        matrices.method_22909();
    }

    private int clampAlpha(double value) {
        int a = Math.round(value);
        return Math.max(0, Math.min(255, a));
    }

    private Color applyOpacity(Color base, int alphaValue) {
        int combined = Math.max(0, Math.min(255, Math.round((float)base.getAlpha() / 255f * (float)alphaValue)));
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), combined);
    }

    private class_243 getLerpedPosCompat(class_1297 e, float tickDelta) {
        try {
            return e.method_30950(tickDelta);
        }
        catch (Throwable ignored) {
            double x = class_3532.method_16436((double)tickDelta, e.field_6038, e.method_23317());
            double y = class_3532.method_16436((double)tickDelta, e.field_5971, e.method_23318());
            double z = class_3532.method_16436((double)tickDelta, e.field_5989, e.method_23321());
            return new class_243(x, y, z);
        }
    }
}
