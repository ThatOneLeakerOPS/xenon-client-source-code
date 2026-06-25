/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;

public final class SwingSpeed
extends Module {
    public static SwingSpeed instance;
    private final Setting<Float> swingSpeed = new Setting("Swing Speed", 1f, 0.1f, 2f);

    public SwingSpeed() {
        super("SwingSpeed", Category.MISC);
        instance = this;
        this.addSetting(this.swingSpeed);
    }

    public float getSwingSpeed() {
        float value = this.swingSpeed.getValue() == null ? 1f : (Float)this.swingSpeed.getValue();
        if (value < 0.1f) {
            return 0.1f;
        }
        return Math.min(value, 2f);
    }
}
