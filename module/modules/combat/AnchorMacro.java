/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.ActivatableModule;
import com.xenon.module.Category;
import com.xenon.setting.Setting;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1819;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_2749;
import net.minecraft.class_3965;
import net.minecraft.class_4969;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;

public final class AnchorMacro
extends ActivatableModule {
    private final Setting<Float> switchDelay = new Setting("Switch Delay", 0f, 0f, 20f);
    private final Setting<Float> glowstoneDelay = new Setting("Glowstone Delay", 0f, 0f, 20f);
    private final Setting<Float> explodeDelay = new Setting("Explode Delay", 0f, 0f, 20f);
    private final Setting<Float> totemSlot = new Setting("Totem Slot", 1f, 1f, 9f);
    private final Setting<Boolean> switchBack = new Setting("Switch Back", false);
    private int switchCounter;
    private int glowstoneDelayCounter;
    private int explodeDelayCounter;
    private boolean waitingForPop = false;

    public AnchorMacro() {
        super("Anchor Macro", Category.COMBAT);
        this.addSetting(this.switchDelay);
        this.addSetting(this.glowstoneDelay);
        this.addSetting(this.explodeDelay);
        this.addSetting(this.totemSlot);
        this.addSetting(this.switchBack);
    }

    public void onEnable() {
        this.resetCounters();
        this.waitingForPop = false;
        super.onEnable();
    }

    public void onDisable() {
        this.resetCounters();
        this.waitingForPop = false;
        super.onDisable();
    }

    public void onPacketReceive(class_2596<?> packet) {
        if (((Boolean)this.switchBack.getValue()).booleanValue()) {
            if (this.waitingForPop) {
                if (packet instanceof class_2749) {
                    this.waitingForPop = false;
                    if (mc.field_1724 != null) {
                        int anchorSlot = this.findItemSlot(class_1802.field_23141);
                        if (anchorSlot != -1) {
                            mc.field_1724.method_31548().method_61496(anchorSlot);
                        }
                    }
                }
            }
        }
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            return;
        }
        if (mc.field_1755 != null) {
            return;
        }
        if (this.isShieldOrFoodActive()) {
            return;
        }
        if (!this.isRightClickHeld()) {
            this.resetCounters();
            return;
        }
        this.handleAnchorInteraction();
    }

    private boolean isShieldOrFoodActive() {
        boolean isFood = mc.field_1724.method_6047().method_7909().method_57347().method_57832(class_9334.field_50075) || mc.field_1724.method_6079().method_7909().method_57347().method_57832(class_9334.field_50075);
        boolean isShield = mc.field_1724.method_6047().method_7909() instanceof class_1819 || mc.field_1724.method_6079().method_7909() instanceof class_1819;
        boolean rightClickPressed = this.isRightClickHeld();
        if (rightClickPressed) {
            return isFood || isShield;
        }
    }

    private boolean isRightClickHeld() {
        return mc.method_22683() != null && GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 1) == 1;
    }

    private void handleAnchorInteraction() {
        class_2338 pos = mc.field_1765;
        if (pos instanceof class_3965) {
            class_3965 blockHitResult = pos;
        } else {
            return;
        }
        if (blockHitResult.method_17783() != class_239.class_240.field_1332) {
            return;
        }
        pos = blockHitResult.method_17777();
        class_2680 state = mc.field_1687.method_8320(pos);
        if (!state.method_27852(class_2246.field_23152)) {
            return;
        }
        mc.field_1690.field_1904.method_23481(false);
        int charges = ((Integer)state.method_11654(class_4969.field_23153)).intValue();
        if (charges == 0) {
            this.placeGlowstone(blockHitResult);
        } else {
            this.explodeAnchor(blockHitResult);
        }
    }

    private void placeGlowstone(class_3965 blockHitResult) {
        if (!mc.field_1724.method_6047().method_31574(class_1802.field_8801)) {
            if (this.switchCounter < ((Float)this.switchDelay.getValue()).intValue()) {
                this.switchCounter = this.switchCounter + 1;
                return;
            }
            this.switchCounter = 0;
            if (!this.swapToItem(class_1802.field_8801)) {
                return;
            }
        }
        if (mc.field_1724.method_6047().method_31574(class_1802.field_8801)) {
            if (this.glowstoneDelayCounter < ((Float)this.glowstoneDelay.getValue()).intValue()) {
                this.glowstoneDelayCounter = this.glowstoneDelayCounter + 1;
                return;
            }
            this.glowstoneDelayCounter = 0;
            this.interactWith(blockHitResult);
        }
    }

    private void explodeAnchor(class_3965 blockHitResult) {
        int selectedSlot = Math.max(0, Math.min(8, ((Float)this.totemSlot.getValue()).intValue() - 1));
        if (mc.field_1724.method_31548().method_67532() != selectedSlot) {
            if (this.switchCounter < ((Float)this.switchDelay.getValue()).intValue()) {
                this.switchCounter = this.switchCounter + 1;
                return;
            }
            this.switchCounter = 0;
            mc.field_1724.method_31548().method_61496(selectedSlot);
        }
        if (mc.field_1724.method_31548().method_67532() == selectedSlot) {
            if (this.explodeDelayCounter < ((Float)this.explodeDelay.getValue()).intValue()) {
                this.explodeDelayCounter = this.explodeDelayCounter + 1;
                return;
            }
            this.explodeDelayCounter = 0;
            this.interactWith(blockHitResult);
            if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                this.waitingForPop = true;
            }
        }
    }

    private int findItemSlot(class_1792 item) {
        for (int i = 0; i < 9; i++) {
            if (!mc.field_1724.method_31548().method_5438(i).method_31574(item)) continue;
            return i;
        }
        return -1;
    }

    private boolean swapToItem(class_1792 item) {
        int slot = this.findItemSlot(item);
        if (slot != -1) {
            mc.field_1724.method_31548().method_61496(slot);
            return true;
        }
        return false;
    }

    private void interactWith(class_3965 hit) {
        mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
        mc.field_1724.method_6104(class_1268.field_5808);
    }

    private void resetCounters() {
        this.switchCounter = 0;
        this.glowstoneDelayCounter = 0;
        this.explodeDelayCounter = 0;
    }
}
