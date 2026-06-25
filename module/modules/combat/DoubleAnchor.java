/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.ActivatableModule;
import com.xenon.module.Category;
import com.xenon.setting.Setting;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2596;
import net.minecraft.class_2749;
import net.minecraft.class_3965;

public final class DoubleAnchor
extends ActivatableModule {
    private final Setting<Float> switchDelay = new Setting("Delay", 0f, 0f, 20f);
    private final Setting<Float> totemSlot = new Setting("Totem Slot", 1f, 1f, 9f);
    private final Setting<Boolean> switchBack = new Setting("Switch Back", false);
    private int delayCounter = 0;
    private int step = 0;
    private boolean isAnchoring = false;
    private class_2338 lastAnchorPos = null;
    private boolean waitingForPop = false;

    public DoubleAnchor() {
        super("Double Anchor", Category.COMBAT);
        this.addSetting(this.switchDelay);
        this.addSetting(this.totemSlot);
        this.addSetting(this.switchBack);
    }

    public void onEnable() {
        this.resetState();
        this.isAnchoring = false;
        this.waitingForPop = false;
        this.lastAnchorPos = null;
        super.onEnable();
    }

    public void onDisable() {
        this.resetState();
        this.isAnchoring = false;
        this.waitingForPop = false;
        this.lastAnchorPos = null;
        super.onDisable();
    }

    public void onBindPressed() {
        super.onBindPressed();
    }

    public void onActivationKeyPressed() {
        if (!this.isEnabled()) {
            return;
        }
        this.resetState();
        this.isAnchoring = true;
        this.waitingForPop = false;
        this.lastAnchorPos = null;
    }

    public void onPacketReceive(class_2596<?> packet) {
        if (((Boolean)this.switchBack.getValue()).booleanValue()) {
            if (this.waitingForPop) {
                if (packet instanceof class_2749) {
                    this.waitingForPop = false;
                    if (mc.field_1724 != null) {
                        int anchorSlot = this.findItemInHotbar(class_1802.field_23141);
                        if (anchorSlot != -1) {
                            mc.field_1724.method_31548().method_61496(anchorSlot);
                        }
                    }
                }
            }
        }
    }

    public void onTick() {
        if (!this.isAnchoring) {
            return;
        }
        if (mc.field_1755 != null) {
            return;
        }
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        if (!this.hasRequiredItems()) {
            this.isAnchoring = false;
            this.resetState();
            return;
        }
        int switchDelayTicks = mc.field_1765;
        if (switchDelayTicks instanceof class_3965) {
            class_3965 blockHitResult = switchDelayTicks;
        } else {
            this.isAnchoring = false;
            this.resetState();
            return;
        }
        if (mc.field_1687.method_8320(blockHitResult.method_17777()).method_27852(class_2246.field_10124)) {
            this.isAnchoring = false;
            this.resetState();
            return;
        }
        switchDelayTicks = Math.max(0, ((Float)this.switchDelay.getValue()).intValue());
        if (this.delayCounter < switchDelayTicks) {
            this.delayCounter = this.delayCounter + 1;
            return;
        }
        if (this.step == 0) {
            this.swapToItem(class_1802.field_23141);
        } else {
            if (this.step == 1) {
                this.interactWithBlock(blockHitResult);
            } else {
                if (this.step == 2) {
                    this.swapToItem(class_1802.field_8801);
                } else {
                    if (this.step == 3) {
                        this.interactWithBlock(blockHitResult);
                    } else {
                        if (this.step == 4) {
                            this.swapToItem(class_1802.field_23141);
                        } else {
                            if (this.step == 5) {
                                this.interactWithBlock(blockHitResult);
                                this.interactWithBlock(blockHitResult);
                            } else {
                                if (this.step == 6) {
                                    this.swapToItem(class_1802.field_8801);
                                } else {
                                    if (this.step == 7) {
                                        this.interactWithBlock(blockHitResult);
                                    } else {
                                        if (this.step == 8) {
                                            int desiredSlot = ((Float)this.totemSlot.getValue()).intValue() - 1;
                                            this.swapToHotbarSlot(desiredSlot);
                                            this.lastAnchorPos = blockHitResult.method_17777();
                                        } else {
                                            if (this.step == 9) {
                                                this.interactWithBlock(blockHitResult);
                                                if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                                                    this.waitingForPop = true;
                                                }
                                            } else {
                                                if (this.step == 10) {
                                                    this.isAnchoring = false;
                                                    this.resetState();
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.step = this.step + 1;
    }

    private void resetState() {
        this.delayCounter = 0;
        this.step = 0;
    }

    private boolean hasRequiredItems() {
        boolean hasAnchor = false;
        boolean hasGlowstone = false;
        for (int i = 0; i < 9; i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (!stack.method_31574(class_1802.field_23141)) continue;
            hasAnchor = true;
            if (!stack.method_31574(class_1802.field_8801)) continue;
            hasGlowstone = true;
        }
        return hasAnchor && hasGlowstone;
    }

    private int findItemInHotbar(class_1792 item) {
        for (int i = 0; i < 9; i++) {
            if (!mc.field_1724.method_31548().method_5438(i).method_31574(item)) continue;
            return i;
        }
        return -1;
    }

    private void swapToItem(class_1792 item) {
        int slot = this.findItemInHotbar(item);
        if (slot != -1) {
            mc.field_1724.method_31548().method_61496(slot);
        }
    }

    private void swapToHotbarSlot(int slot) {
        if (slot < 0 || slot > 8) {
            return;
        }
        mc.field_1724.method_31548().method_61496(slot);
    }

    private void interactWithBlock(class_3965 hit) {
        mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
        mc.field_1724.method_6104(class_1268.field_5808);
    }
}
