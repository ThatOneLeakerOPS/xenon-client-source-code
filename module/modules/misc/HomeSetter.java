/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import net.minecraft.class_634;

public final class HomeSetter
extends Module {
    private final Setting<Boolean> chatFeedback = new Setting("Chat Feedback", true);
    private final Setting<Float> homeSlot = new Setting("Home Slot", 1f, 1f, 5f);
    private volatile boolean running = false;

    public HomeSetter() {
        super("HomeSetter", Category.MISC);
        this.addSetting(this.chatFeedback);
        this.addSetting(this.homeSlot);
    }

    public void onEnable() {
        super.onEnable();
        if (this.running) {
            return;
        }
        if (mc == null || mc.field_1724 == null || mc.field_1687 == null) {
            this.toggle();
            return;
        }
        this.running = true;
        int slot = Math.round(((Float)this.homeSlot.getValue()).floatValue());
        int wait = 750;
        mc.execute(this::lambda$onEnable$2 /* captured: slot */);
    }

    public void onDisable() {
        super.onDisable();
        this.running = false;
    }

    private void sendServerCommand(String command) {
        if (mc == null) {
            return;
        }
        class_634 nh = null;
        try {
            if (mc.field_1724 != null) {
                nh = mc.field_1724.field_3944;
            }
        }
        catch (Throwable cmdNoSlash) {
            if (nh == null) {
                nh = mc.method_1562();
            }
        }
        try {
            nh = mc.method_1562();
        }
        catch (Throwable cmdNoSlash) {
            if (nh == null) {
                return;
            }
        }
        if (nh == null) {
            return;
        }
        cmdNoSlash = command.startsWith("/") ? command.substring(1) : command;
        try {
            nh.method_45730(cmdNoSlash);
            return;
        }
        catch (Throwable e4) {
            nh.method_45729(command);
            return;
        }
        try {
            nh.method_45729(command);
        }
        catch (Throwable e4) {
            return;
        }
    }
}
