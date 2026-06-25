/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import net.minecraft.class_4587;

public final class JumpCircles
extends Module {
    private static final int SEGMENTS = 128;
    private final Setting<Float> lifetime = new Setting("Lifetime (s)", 1.5f, 0.1f, 5f);
    private final Setting<Float> startRadius = new Setting("Start Radius", 0.55f, 0.1f, 3f);
    private final Setting<Float> endRadius = new Setting("End Radius", 1.8f, 0.2f, 6f);
    private final Setting<Float> lineWidth = new Setting("Line Width", 2f, 0.5f, 6f);
    private final Setting<Color> color = new Setting("Color", new Color(120, 220, 255, 255));
    private final Setting<Boolean> glowMode = new Setting("Glow Mode", false);
    private final Setting<Boolean> glowFilled = new Setting("Glow Filled", false);
    private final List<Circle> circles = new ArrayList();
    private boolean wasOnGround = true;
    private double lastGroundX = 0;
    private double lastGroundY = 0;
    private double lastGroundZ = 0;

    public JumpCircles() {
        super("JumpCircles", Category.RENDER);
        this.addSetting(this.lifetime);
        this.addSetting(this.startRadius);
        this.addSetting(this.endRadius);
        this.addSetting(this.lineWidth);
        this.addSetting(this.color);
        this.addSetting(this.glowMode);
        this.addSetting(this.glowFilled);
    }

    public void onEnable() {
        this.circles.clear();
        this.wasOnGround = true;
    }

    public void onDisable() {
        this.circles.clear();
    }

    public void onTick() {
        if (mc.field_1724 == null) {
            return;
        }
        boolean onGround = mc.field_1724.method_24828();
        if (onGround) {
            this.lastGroundX = mc.field_1724.method_23317();
            this.lastGroundY = mc.field_1724.method_23318();
            this.lastGroundZ = mc.field_1724.method_23321();
        }
        if (this.wasOnGround) {
            if (!onGround) {
                if (mc.field_1724.method_18798().field_1351 > 0) {
                    this.circles.add(new Circle(this.lastGroundX, this.lastGroundY + 0.02, this.lastGroundZ, System.currentTimeMillis()));
                }
            }
        }
        this.wasOnGround = onGround;
        long now = System.currentTimeMillis();
        long lifeMs = (((Float)this.lifetime.getValue()).floatValue() * 1000f);
        Iterator<Circle> it = this.circles.iterator();
        while (it.hasNext()) {
            if (now - ((Circle)it.next()).bornMs < lifeMs) continue;
            it.remove();
        }
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null || this.circles.isEmpty()) {
            return;
        }
        class_4184 cam = RenderUtils.getCamera();
        if (cam == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(cam);
        long now = System.currentTimeMillis();
        long lifeMs = (((Float)this.lifetime.getValue()).floatValue() * 1000f);
        double sR = ((Float)this.startRadius.getValue()).floatValue();
        double eR = ((Float)this.endRadius.getValue()).floatValue();
        float lw = ((Float)this.lineWidth.getValue()).floatValue();
        Color base = this.color.getValue();
        int baseAlpha = base.getAlpha();
        matrices.method_22903();
        WorldBatch batch = RenderUtils.beginWorldBatch(matrices);
        for (Circle c : this.circles) {
            float age = (now - c.bornMs) / (float)lifeMs;
            if (age >= 0f) continue;
            age = 0f;
            if (age <= 1f) continue;
            age = 1f;
            float ease = 1f - (1f - age) * (1f - age) * (1f - age);
            double radius = sR + (eR - sR) * (double)ease;
            int alpha = ((float)baseAlpha * (1f - ease));
            while (alpha <= 0) {
            }
            Color col = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
            double cx = c.x - camPos.field_1352;
            double cy = c.y - camPos.field_1351;
            double cz = c.z - camPos.field_1350;
            class_243[] points = new class_243[129];
            for (int i = 0; i <= 128; i++) {
                double ang = 6.283185307179586 * ((double)i / 128);
                points[i] = new class_243(cx + Math.cos(ang) * radius, cy, cz + Math.sin(ang) * radius);
            }
            if (!((Boolean)this.glowFilled.getValue()).booleanValue()) { /* goto L624; */ }
            int r0 = base.getRed();
            int g0 = base.getGreen();
            int b0 = base.getBlue();
            int rings = 32;
            float maxLw = lw * 8f;
            for (int k = 1; k <= rings; k++) {
                double t = k / (double)rings;
                double innerRadius = radius * (1 - t);
                if (!(innerRadius < 0.05)) {
                    int aFill = Math.max(1, (int)((double)alpha * (1 - t * 0.6) * 0.55));
                    Color fillCol = new Color(r0, g0, b0, aFill);
                    class_243[] ring = new class_243[129];
                    for (int i = 0; i <= 128; i++) {
                        double ang = 6.283185307179586 * ((double)i / 128);
                        ring[i] = new class_243(cx + Math.cos(ang) * innerRadius, cy, cz + Math.sin(ang) * innerRadius);
                    }
                    JumpCircles.drawRing(batch, ring, fillCol, maxLw);
                }
            }
            if (((Boolean)this.glowMode.getValue()).booleanValue()) {
                int r = base.getRed();
                int g = base.getGreen();
                int b = base.getBlue();
                Color h1 = new Color(r, g, b, Math.max(1, alpha / 16));
                Color h2 = new Color(r, g, b, Math.max(1, alpha / 12));
                Color h3 = new Color(r, g, b, Math.max(1, alpha / 9));
                Color h4 = new Color(r, g, b, Math.max(1, alpha / 6));
                Color h5 = new Color(r, g, b, Math.max(1, alpha / 4));
                Color h6 = new Color(r, g, b, Math.max(1, alpha / 2));
                Color core1 = new Color(r, g, b, Math.min(255, (int)((float)alpha * 1f)));
                Color core2 = new Color(255, 255, 255, Math.min(255, (int)((float)alpha * 1.4f)));
                JumpCircles.drawRing(batch, points, h1, lw * 18f);
                JumpCircles.drawRing(batch, points, h2, lw * 14f);
                JumpCircles.drawRing(batch, points, h3, lw * 11f);
                JumpCircles.drawRing(batch, points, h4, lw * 8.5f);
                JumpCircles.drawRing(batch, points, h5, lw * 6f);
                JumpCircles.drawRing(batch, points, h6, lw * 4f);
                JumpCircles.drawRing(batch, points, core1, lw * 2.8f);
                JumpCircles.drawRing(batch, points, core2, lw * 1.6f);
            } else {
                JumpCircles.drawRing(batch, points, col, lw);
            }
        }
        batch.flush();
        matrices.method_22909();
    }

    private static void drawRing(RenderUtils.WorldBatch batch, class_243[] points, Color col, float width) {
        for (int i = 1; i < points.length; i++) {
            batch.renderLine(col, points[i - 1], points[i], width);
        }
    }
}
