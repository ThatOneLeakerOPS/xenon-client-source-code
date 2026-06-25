/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.mixin.MinecraftClientAccessor;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1811;
import net.minecraft.class_9334;

public final class FastPlace
extends Module {
    private final Setting<Boolean> onlyXP = new Setting("Only XP", false);
    private final Setting<Boolean> allowBlocks = new Setting("Blocks", true);
    private final Setting<Boolean> allowItems = new Setting("Items", true);
    private final Setting<Float> useDelay = new Setting("Delay", 0f, 0f, 10f);

    public FastPlace() {
        super("Fast Place", Category.MISC);
        this.addSetting(this.onlyXP);
        this.addSetting(this.allowBlocks);
        this.addSetting(this.allowItems);
        this.addSetting(this.useDelay);
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1755 != null) {
            return;
        }
        if (!mc.field_1690.field_1904.method_1434()) {
            return;
        }
        class_1799 mainHand = mc.field_1724.method_6047();
        class_1799 offHand = mc.field_1724.method_6079();
        if (!this.shouldAffectCooldown(mainHand, offHand)) {
            return;
        }
        MinecraftClientAccessor accessor = mc;
        int targetCooldown = Math.max(0, ((Float)this.useDelay.getValue()).intValue());
        if (accessor.xenon$getItemUseCooldown() != targetCooldown) {
            accessor.xenon$setItemUseCooldown(targetCooldown);
        }
    }

    private boolean shouldAffectCooldown(class_1799 mainHand, class_1799 offHand) {
        boolean mainXp = mainHand.method_31574(class_1802.field_8287);
        boolean offXp = offHand.method_31574(class_1802.field_8287);
        if ((Boolean)this.onlyXP.getValue()).booleanValue() {
            return mainXp || offXp;
        }
        class_1792 mainItem = mainHand.method_7909();
        class_1792 offItem = offHand.method_7909();
        if (this.isFood(mainHand) || this.isFood(offHand)) {
            return false;
        }
        if (mainHand.method_31574(class_1802.field_23141) || mainHand.method_31574(class_1802.field_8801) || offHand.method_31574(class_1802.field_23141) || offHand.method_31574(class_1802.field_8801)) {
            return false;
        }
        if (mainItem instanceof class_1811 || offItem instanceof class_1811) {
            return false;
        }
        boolean hasBlockItem = mainItem instanceof class_1747 || offItem instanceof class_1747;
        if (hasBlockItem) {
            return ((Boolean)this.allowBlocks.getValue()).booleanValue();
        }
        return ((Boolean)this.allowItems.getValue()).booleanValue();
    }

    private boolean isFood(class_1799 stack) {
        return stack.method_57353().method_57832(class_9334.field_50075);
    }
}
