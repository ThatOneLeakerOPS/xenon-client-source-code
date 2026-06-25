/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.modules.render.LightDebug;
import com.xenon.setting.Setting;

class LightDebug.1
extends Setting<Integer> {
    LightDebug.1(LightDebug this_0, String name, Integer value, Integer min, Integer max) {
        super(name, value, min, max);
    }

    public boolean matchesName(String settingName) {
        return super.matchesName(settingName) || "Max Y".equalsIgnoreCase(settingName) || "Height".equalsIgnoreCase(settingName);
    }
}
