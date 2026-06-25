/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.class_7172;

public final class FullBright
extends Module {
    private static final double FULL_BRIGHT_GAMMA = 10;
    private double previousGamma = 1;
    private boolean gammaApplied = false;
    private static Field valueField;

    public FullBright() {
        super("FullBright", Category.RENDER);
    }

    public void onEnable() {
        if (mc.field_1690 == null) {
            return;
        }
        this.previousGamma = ((Double)mc.field_1690.method_42473().method_41753()).doubleValue();
        this.setGamma(10);
        this.gammaApplied = true;
    }

    public void onDisable() {
        if (!this.gammaApplied) {
            return;
        }
        this.setGamma(this.previousGamma);
        this.gammaApplied = false;
    }

    public void onTick() {
        if (!this.gammaApplied || mc.field_1690 == null) {
            return;
        }
        try {
            double current = ((Double)mc.field_1690.method_42473().method_41753()).doubleValue();
            if (Math.abs(current - 10) > 0.0001) {
                this.setGamma(10);
            }
        }
        catch (Exception current) {
            return;
        }
    }

    private void setGamma(double gamma) {
        class_7172<Double> opt = mc.field_1690.method_42473();
        Field field = valueField;
        if (field == null) {
            field = this.resolveValueField(opt);
            valueField = field;
        }
        try {
            field.set(opt, gamma);
            Double applied = opt.method_41753();
            if (applied != null) {
                if (Math.abs(applied.doubleValue() - gamma) <= 0.0001) {
                    return;
                }
            }
        }
        catch (Exception applied) {
            opt.method_41748(gamma);
            return;
        }
        try {
            valueField = null;
        }
        catch (Exception applied) {
            opt.method_41748(gamma);
            return;
        }
        try {
            opt.method_41748(gamma);
        }
        catch (Exception applied) {
            return;
        }
    }

    private Field resolveValueField(class_7172<?> option) {
        try {
            Object current = option.method_41753();
        }
        catch (Exception ignored) {
            Object current = null;
            Field fallback = null;
            Field[] var4 = class_7172.class.getDeclaredFields();
            var var5 = var4.length;
            int var6 = 0;
            if (var6 >= var5) return field;
            Field field = var4[var6];
            if (!Modifier.isStatic(field.getModifiers())) return field;
        }
        fallback = null;
        var4 = class_7172.class.getDeclaredFields();
        var5 = var4.length;
        var6 = 0;
        if (var6 >= var5) return null;
        field = var4[var6];
        if (!(Modifier.isStatic(field.getModifiers()))) {
            if ("value".equals(field.getName())) {
                fallback = field;
            }
            field.setAccessible(true);
        }
        try {
            Object value = field.get(option);
            if (current != null) {
            }
            return field;
        }
        catch (Exception value) {
            var6++;
            /* goto @28; */
            if (fallback != null) {
                fallback.setAccessible(true);
                return fallback;
            }
        }
        return null;
    }
}
