/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import net.minecraft.class_1661;
import net.minecraft.class_1802;

public final class AutoDoubleHand
extends Module {
    private final Setting<Boolean> onTotemPop = new Setting("On Totem Pop", true);
    private final Setting<Boolean> onHealth = new Setting("On Health", true);
    private final Setting<Float> healthThreshold = new Setting("Health Threshold", 6f, 1f, 20f);
    private final Setting<Float> cooldown = new Setting("Cooldown", 5f, 0f, 40f);
    private boolean hadTotemHeldLastTick = false;
    private int cooldownTicks = 0;
    private int previousSlot = -1;

    public AutoDoubleHand() {
        super("AutoDoubleHand", Category.COMBAT);
        this.addSetting(this.onTotemPop);
        this.addSetting(this.onHealth);
        this.addSetting(this.healthThreshold);
        this.addSetting(this.cooldown);
    }

    public void onEnable() {
        this.hadTotemHeldLastTick = false;
        this.cooldownTicks = 0;
        this.previousSlot = -1;
    }

    public void onDisable() {
        this.hadTotemHeldLastTick = false;
        this.cooldownTicks = 0;
        this.previousSlot = -1;
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1761 == null) {
            return;
        }
        if (this.cooldownTicks > 0) {
            this.cooldownTicks = this.cooldownTicks - 1;
        }
        class_1661 inv = mc.field_1724.method_31548();
        boolean totemMain = mc.field_1724.method_6047().method_7909() == class_1802.field_8288;
        boolean totemOff = mc.field_1724.method_6079().method_7909() == class_1802.field_8288;
        boolean totemHeld = totemMain || totemOff;
        boolean popped = this.hadTotemHeldLastTick && !totemHeld;
        this.hadTotemHeldLastTick = totemHeld;
        if (this.cooldownTicks > 0) {
            return;
        }
        boolean shouldSwitch = false;
        if (((Boolean)this.onTotemPop.getValue()).booleanValue()) {
            if (popped) {
                shouldSwitch = true;
            }
        }
        if (((Boolean)this.onHealth.getValue()).booleanValue()) {
            if (mc.field_1724.method_6032() <= ((Float)this.healthThreshold.getValue()).floatValue()) {
                shouldSwitch = true;
            }
        }
        if (!shouldSwitch) {
            return;
        }
        if (totemMain) {
            return;
        }
        int hotbarSlot = this.findHotbarTotemSlot();
        if (hotbarSlot < 0) {
            return;
        }
        if (inv.method_67532() == hotbarSlot) {
            return;
        }
        this.previousSlot = inv.method_67532();
        inv.method_61496(hotbarSlot);
        this.cooldownTicks = ((Float)this.cooldown.getValue()).intValue();
    }

    private int findHotbarTotemSlot() {
        class_1661 inv = mc.field_1724.method_31548();
        for (int i = 0; i < 9; i++) {
            if (!inv.method_5438(i).method_31574(class_1802.field_8288)) continue;
            return i;
        }
        return -1;
    }
}
