/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.modules.donut.SpawnerProtect;
import com.xenon.setting.Setting;

class SpawnerProtect.1
extends Setting<String> {
    SpawnerProtect.1(SpawnerProtect this_0, String name, String value) {
        super(name, value);
    }

    public boolean matchesName(String settingName) {
        return super.matchesName(settingName) || "Webhook URL".equalsIgnoreCase(settingName);
    }
}
