/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.client;

import com.xenon.gui.CloudConfigScreen;
import com.xenon.module.Category;
import com.xenon.module.Module;

public final class CloudConfigs
extends Module {
    public CloudConfigs() {
        super("Cloud Configs", Category.CLIENT);
    }

    public void onEnable() {
        if (mc == null) {
            return;
        }
        mc.execute(this::lambda$onEnable$0);
    }

    public void onDisable() {
        if (mc != null) {
            if (mc.field_1755 instanceof CloudConfigScreen) {
                mc.execute(CloudConfigs::lambda$onDisable$1);
            }
        }
    }
}
