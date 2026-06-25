/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public final class AutoTotem
extends Module {
    private final Setting<Float> delay = new Setting("Delay", 1f, 0f, 5f);
    private int delayCounter;

    public AutoTotem() {
        super("Auto Totem", Category.COMBAT);
        this.addSetting(this.delay);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    public void onTick() {
        if (mc.field_1724 == null) {
            return;
        }
        if (mc.field_1724.method_6079().method_7909() == class_1802.field_8288) {
            this.delayCounter = ((Float)this.delay.getValue()).intValue();
            return;
        }
        if (this.delayCounter > 0) {
            this.delayCounter = this.delayCounter - 1;
            return;
        }
        int slot = this.findItemSlot(class_1802.field_8288);
        if (slot == -1) {
            return;
        }
        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, AutoTotem.convertSlotIndex(slot), 40, class_1713.field_7791, mc.field_1724);
        this.delayCounter = ((Float)this.delay.getValue()).intValue();
    }

    public int findItemSlot(class_1792 item) {
        if (mc.field_1724 == null) {
            return -1;
        }
        for (int i = 0; i < 36; i++) {
            if (!mc.field_1724.method_31548().method_5438(i).method_31574(item)) continue;
            return i;
        }
        return -1;
    }

    private static int convertSlotIndex(int slotIndex) {
        if (slotIndex < 9) {
            return 36 + slotIndex;
        }
        return slotIndex;
    }
}
