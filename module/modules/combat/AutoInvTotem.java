/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import java.util.Random;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_490;

public final class AutoInvTotem
extends Module {
    private final Setting<Float> delay = new Setting("Delay", 2f, 0f, 20f);
    private final Setting<Boolean> hotbar = new Setting("Hotbar", false);
    private final Setting<Float> totemSlot = new Setting("Totem Slot", 1f, 1f, 9f);
    private final Setting<Boolean> forceTotem = new Setting("Force Totem", false);
    private final Setting<Boolean> autoOpen = new Setting("Auto Open", false);
    private final Setting<Float> closeDelay = new Setting("Close Delay", 3f, 0f, 20f);
    private static final int STATE_IDLE = 0;
    private static final int STATE_WAIT_OPEN = 1;
    private static final int STATE_INV_OPEN = 2;
    private static final int STATE_SWAPPED = 3;
    private int state = 0;
    private int tickCounter = 0;
    private boolean wasTotemInOffhand = true;
    private final Random random = new Random();

    public AutoInvTotem() {
        super("Auto Inv Totem", Category.COMBAT);
        this.addSetting(this.delay);
        this.addSetting(this.hotbar);
        this.addSetting(this.totemSlot);
        this.addSetting(this.forceTotem);
        this.addSetting(this.autoOpen);
        this.addSetting(this.closeDelay);
    }

    public void onEnable() {
        this.state = 0;
        this.tickCounter = 0;
        this.wasTotemInOffhand = true;
        super.onEnable();
    }

    public void onDisable() {
        this.state = 0;
        this.tickCounter = 0;
        super.onDisable();
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1761 == null) {
            return;
        }
        class_1661 inv = mc.field_1724.method_31548();
        boolean totemInOffhand = mc.field_1724.method_6079().method_7909() == class_1802.field_8288;
        if (((Boolean)this.autoOpen.getValue()).booleanValue()) {
            this.handleAutoOpen(inv, totemInOffhand);
            this.wasTotemInOffhand = totemInOffhand;
            return;
        }
        this.wasTotemInOffhand = totemInOffhand;
        if (!(mc.field_1755 instanceof class_490)) {
            this.tickCounter = 0;
            return;
        }
        if (this.tickCounter < ((Float)this.delay.getValue()).intValue() + this.randomJitter()) {
            this.tickCounter = this.tickCounter + 1;
            return;
        }
        this.tickCounter = 0;
        if (!totemInOffhand && this.swapTotemToOffhand(inv)) {
            return;
        }
        if (((Boolean)this.hotbar.getValue()).booleanValue()) {
            this.tryHotbarTotem(inv);
        }
        this.tickCounter = 0;
    }

    private void handleAutoOpen(class_1661 inv, boolean totemInOffhand) {
        switch (this.state) {
            case 0:
                if (this.wasTotemInOffhand && !(totemInOffhand) && this.findTotemSlot(inv) != -1) {
                    this.state = 1;
                }
                this.tickCounter = ((Float)this.delay.getValue()).intValue() <= 0 ? 1 + this.random.nextInt(2) : 1 + this.random.nextInt(3);
                if (!(totemInOffhand) && this.state == 0 && !(mc.field_1755 instanceof class_490) && this.findTotemSlot(inv) != -1) {
                    this.state = 1;
                }
                this.tickCounter = ((Float)this.delay.getValue()).intValue() <= 0 ? 1 + this.random.nextInt(2) : 1 + this.random.nextInt(3);
            case 1:
                if (this.tickCounter > 0) {
                    this.tickCounter = this.tickCounter - 1;
                    return;
                }
                if (!(mc.field_1755 instanceof class_490)) {
                    mc.method_1507(new class_490(mc.field_1724));
                }
                this.state = 2;
                this.tickCounter = ((Float)this.delay.getValue()).intValue() + this.randomJitter();
            case 2:
                if (!(mc.field_1755 instanceof class_490)) {
                    this.state = 0;
                    return;
                }
                if (this.tickCounter > 0) {
                    this.tickCounter = this.tickCounter - 1;
                    return;
                }
                boolean didSwapOffhand = false;
                if (!totemInOffhand) {
                    didSwapOffhand = this.swapTotemToOffhand(inv);
                }
                boolean didSwapHotbar = false;
                if (((Boolean)this.hotbar.getValue()).booleanValue()) {
                    didSwapHotbar = this.tryHotbarTotem(inv);
                }
                if (didSwapOffhand || !(didSwapHotbar)) {
                    this.state = 3;
                }
                this.tickCounter = ((Float)this.closeDelay.getValue()).intValue() + this.randomJitter();
            case 3:
                if (this.tickCounter > 0) {
                    this.tickCounter = this.tickCounter - 1;
                    return;
                }
                if (mc.field_1755 instanceof class_490) {
                    mc.field_1724.method_7346();
                    mc.method_1507(null);
                }
                this.state = 0;
            default:
                return;
        }
    }

    private boolean swapTotemToOffhand(class_1661 inv) {
        int invSlot = this.findTotemSlot(inv);
        if (invSlot == -1) {
            return false;
        }
        int handlerSlot = AutoInvTotem.convertToHandlerSlot(invSlot);
        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, handlerSlot, 40, class_1713.field_7791, mc.field_1724);
        return true;
    }

    private boolean tryHotbarTotem(class_1661 inv) {
        int preferredSlot = ((Float)this.totemSlot.getValue()).intValue() - 1;
        if (inv.method_5438(preferredSlot).method_7909() == class_1802.field_8288) {
            return false;
        }
        if (!inv.method_5438(preferredSlot).method_7960() && !((Boolean)this.forceTotem.getValue()).booleanValue()) {
            return false;
        }
        int totemSlotIdx = this.findTotemSlotMainOnly(inv);
        if (totemSlotIdx == -1) {
            return false;
        }
        int handlerSlot = AutoInvTotem.convertToHandlerSlot(totemSlotIdx);
        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, handlerSlot, preferredSlot, class_1713.field_7791, mc.field_1724);
        return true;
    }

    private int findTotemSlot(class_1661 inv) {
        for (int i = 9; i < 36; i++) {
            if (inv.method_5438(i).method_7909() != class_1802.field_8288) continue;
            return i;
        }
        for (int i = 0; i < 9; i++) {
            if (inv.method_5438(i).method_7909() != class_1802.field_8288) continue;
            return i;
        }
        return -1;
    }

    private int findTotemSlotMainOnly(class_1661 inv) {
        for (int i = 9; i < 36; i++) {
            if (inv.method_5438(i).method_7909() != class_1802.field_8288) continue;
            return i;
        }
        return -1;
    }

    private static int convertToHandlerSlot(int invSlot) {
        if (invSlot < 9) {
            return 36 + invSlot;
        }
        return invSlot;
    }

    private int randomJitter() {
        if (((Float)this.delay.getValue()).intValue() <= 0) {
            return 0;
        }
        return this.random.nextInt(2);
    }
}
