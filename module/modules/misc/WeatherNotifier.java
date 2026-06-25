/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.gui.notification.NotificationManager;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.ModeSetting;
import com.xenon.setting.Setting;
import net.minecraft.class_1799;
import net.minecraft.class_2561;

public final class WeatherNotifier
extends Module {
    private final ModeSetting notificationMode;
    private final Setting<Boolean> notifyThunder;
    private Boolean wasRaining;
    private Boolean wasThundering;

    public WeatherNotifier() {
        super("WeatherNotifier", Category.MISC);
        this.notificationMode = new ModeSetting("Notification Mode", "Both", "Chat", "Toast", "Both");
        this.notifyThunder = new Setting("Notify Thunder", true);
        this.wasRaining = null;
        this.wasThundering = null;
        this.addSetting(this.notificationMode);
        this.addSetting(this.notifyThunder);
    }

    public void onEnable() {
        this.wasRaining = null;
        this.wasThundering = null;
    }

    public void onDisable() {
        this.wasRaining = null;
        this.wasThundering = null;
    }

    public void onTick() {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        boolean raining = mc.field_1687.method_8419();
        boolean thundering = mc.field_1687.method_8546();
        if (this.wasRaining == null) {
            this.wasRaining = raining;
            this.wasThundering = thundering;
            return;
        }
        if (raining) {
            if (!this.wasRaining.booleanValue()) {
                this.notify("The rain started.", "Rain Started", -10835482);
            }
        } else {
            if (!raining) {
                if (this.wasRaining.booleanValue()) {
                    this.notify("The rain stopped.", "Rain Stopped", -340971);
                }
            }
        }
        if ((Boolean)this.notifyThunder.getValue()).booleanValue() {
            if (thundering) {
                if (!this.wasThundering.booleanValue()) {
                    this.notify("A thunderstorm started.", "Thunder Started", -4879105);
                }
            } else {
                if (!thundering) {
                    if (this.wasThundering.booleanValue()) {
                        this.notify("The thunderstorm ended.", "Thunder Ended", -340971);
                    }
                }
            }
        }
        this.wasRaining = raining;
        this.wasThundering = thundering;
    }

    private void notify(String chatMessage, String toastTitle, int accent) {
        String mode = this.notificationMode.getValue();
        boolean toChat = "Chat".equalsIgnoreCase(mode) || "Both".equalsIgnoreCase(mode);
        boolean toToast = "Toast".equalsIgnoreCase(mode) || "Both".equalsIgnoreCase(mode);
        try {
            mc.field_1705.method_1743().method_1812(class_2561.method_43470("[WeatherNotifier] " + chatMessage));
        }
        catch (Throwable e7) {
            if (toToast) {
                NotificationManager.INSTANCE.push("WeatherNotifier", toastTitle, class_1799.field_8037, accent);
            }
            return;
        }
        if (toToast) {
            NotificationManager.INSTANCE.push("WeatherNotifier", toastTitle, class_1799.field_8037, accent);
        }
    }
}
