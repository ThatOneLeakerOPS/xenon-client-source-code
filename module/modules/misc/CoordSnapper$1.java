/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.modules.misc.CoordSnapper;
import com.xenon.setting.Setting;

class CoordSnapper.1
extends Setting<String> {
    CoordSnapper.1(CoordSnapper this_0, String name, String value) {
        super(name, value);
    }

    public boolean matchesName(String settingName) {
        return super.matchesName(settingName) || "Webhook URL".equalsIgnoreCase(settingName);
    }
}
