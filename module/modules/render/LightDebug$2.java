/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.modules.render.LightDebug;
import com.xenon.setting.Setting;

class LightDebug.2
extends Setting<Integer> {
    LightDebug.2(LightDebug this_0, String name, Integer value, Integer min, Integer max) {
        super(name, value, min, max);
    }

    public boolean matchesName(String settingName) {
        return super.matchesName(settingName) || "Chunk Radius".equalsIgnoreCase(settingName) || "RenderDistance".equalsIgnoreCase(settingName);
    }
}
