/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;

public final class Hitbox
extends Module {
    public static Hitbox INSTANCE;
    public final Setting<Float> size = new Setting("Expand", 1f, 0.5f, 2f);

    public Hitbox() {
        super("Hitbox", Category.COMBAT);
        this.addSetting(this.size);
        INSTANCE = this;
    }
}
