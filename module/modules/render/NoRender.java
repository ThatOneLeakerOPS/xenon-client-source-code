/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import net.minecraft.class_1959;
import net.minecraft.class_3414;
import net.minecraft.class_3417;

public final class NoRender
extends Module {
    public static NoRender instance;
    private final Setting<Boolean> rain = new Setting("Rain", true);
    private final Setting<Boolean> snow = new Setting("Snow", true);
    private final Setting<Boolean> thunder = new Setting("Thunder", true);

    public NoRender() {
        super("NoRender", Category.RENDER);
        instance = this;
        this.addSetting(this.rain);
        this.addSetting(this.snow);
        this.addSetting(this.thunder);
    }

    public static boolean isActive() {
        return instance != null && instance.isEnabled() && mc != null && mc.field_1687 != null;
    }

    public static boolean hideRain() {
        return NoRender.isActive() && ((Boolean)instance.rain.getValue()).booleanValue();
    }

    public static boolean hideSnow() {
        return NoRender.isActive() && ((Boolean)instance.snow.getValue()).booleanValue();
    }

    public static boolean hideThunder() {
        return NoRender.isActive() && ((Boolean)instance.thunder.getValue()).booleanValue();
    }

    public static boolean hideAllPrecipitation() {
        return NoRender.hideRain() && NoRender.hideSnow();
    }

    public static boolean hideRainGradient() {
        return NoRender.hideAllPrecipitation();
    }

    public static class_1959.class_1963 filterPrecipitation(class_1959.class_1963 precipitation) {
        if (!NoRender.isActive() || precipitation == null) {
            return precipitation;
        }
        if (precipitation == class_1959.class_1963.field_9382 && NoRender.hideRain()) {
            return class_1959.class_1963.field_9384;
        }
        if (precipitation == class_1959.class_1963.field_9383 && NoRender.hideSnow()) {
            return class_1959.class_1963.field_9384;
        }
        return precipitation;
    }

    public static boolean shouldCancelWeatherSound(class_3414 soundEvent) {
        if (!NoRender.isActive() || soundEvent == null) {
            return false;
        }
        if (NoRender.hideRain()) {
            if (soundEvent == class_3417.field_14946 || soundEvent == class_3417.field_15020) {
                return true;
            }
        }
        if (soundEvent == class_3417.field_14865) return NoRender.hideThunder();
        if (soundEvent == class_3417.field_14956) {
        }
        return NoRender.hideThunder();
    }
}
