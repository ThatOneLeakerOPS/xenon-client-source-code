/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.mixin.HandledScreenAccessor;
import com.xenon.module.Category;
import com.xenon.module.Module;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1802;
import net.minecraft.class_465;

public final class HoverTotem
extends Module {
    private int lastSwapAttemptHandlerSlotId = -1;

    public HoverTotem() {
        super("Hover Totem", Category.COMBAT);
    }

    public void onDisable() {
        this.lastSwapAttemptHandlerSlotId = -1;
        super.onDisable();
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1761 == null) {
            return;
        }
        class_1735 focused = mc.field_1755;
        if (focused instanceof class_465) {
            class_465<?> handled = focused;
        } else {
            this.lastSwapAttemptHandlerSlotId = -1;
            return;
        }
        focused = ((HandledScreenAccessor)handled).xenon$getFocusedSlot();
        if (focused == null || focused.method_7677().method_7960()) {
            this.lastSwapAttemptHandlerSlotId = -1;
            return;
        }
        if (!focused.method_7677().method_31574(class_1802.field_8288)) {
            this.lastSwapAttemptHandlerSlotId = -1;
            return;
        }
        if (mc.field_1724.method_6079().method_31574(class_1802.field_8288)) {
            return;
        }
        if (focused.field_7874 == this.lastSwapAttemptHandlerSlotId) {
            return;
        }
        int syncId = mc.field_1724.field_7512.field_7763;
        mc.field_1761.method_2906(syncId, focused.field_7874, 40, class_1713.field_7791, mc.field_1724);
        this.lastSwapAttemptHandlerSlotId = focused.field_7874;
    }
}
