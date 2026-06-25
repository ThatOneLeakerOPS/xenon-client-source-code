/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_239;
import net.minecraft.class_3966;

public final class ShieldBreaker
extends Module {
    private final Setting<Boolean> switchBack = new Setting("Switch Back", true);
    private final Setting<Float> switchDelayMs = new Setting("Switch Delay", 0f, 0f, 500f);
    private boolean isBlockingState = false;
    private long firstDetectedTime = -1L;
    private boolean hasAttacked = false;
    private boolean needsSwitchBack = false;
    private int previousSlot = -1;

    public ShieldBreaker() {
        super("Shield Breaker", Category.COMBAT);
        this.addSetting(this.switchBack);
        this.addSetting(this.switchDelayMs);
    }

    public void onEnable() {
        this.resetState();
        super.onEnable();
    }

    public void onDisable() {
        if (this.hasAttacked) {
            if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                if (this.needsSwitchBack) {
                    if (mc.field_1724 != null) {
                        this.swapToSlot(this.previousSlot);
                    }
                }
            }
        }
        this.resetState();
        super.onDisable();
    }

    private void resetState() {
        this.isBlockingState = false;
        this.firstDetectedTime = -1L;
        this.hasAttacked = false;
        this.needsSwitchBack = false;
        this.previousSlot = -1;
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        class_1657 target = this.getTargetPlayer();
        if (target != null) {
            if (target.method_6039()) {
                if (!this.isBlockingState) {
                    this.isBlockingState = true;
                    this.firstDetectedTime = System.currentTimeMillis();
                }
                long delay = ((Float)this.switchDelayMs.getValue()).longValue();
                if (!this.hasAttacked) {
                    if (this.firstDetectedTime >= 0L) {
                        if (System.currentTimeMillis() - this.firstDetectedTime >= delay) {
                            int bestAxeSlot = this.findAxeSlot();
                            if (bestAxeSlot != -1) {
                                this.previousSlot = mc.field_1724.method_31548().method_67532();
                                if (this.previousSlot != bestAxeSlot) {
                                    this.swapToSlot(bestAxeSlot);
                                    this.needsSwitchBack = true;
                                }
                                mc.field_1761.method_2918(mc.field_1724, target);
                                mc.field_1724.method_6104(class_1268.field_5808);
                                this.hasAttacked = true;
                            }
                        }
                    }
                }
            }
        } else {
            if (this.isBlockingState) {
                this.isBlockingState = false;
                this.firstDetectedTime = -1L;
            }
            if (this.hasAttacked) {
                if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                    if (this.needsSwitchBack) {
                        if (this.previousSlot != -1) {
                            this.swapToSlot(this.previousSlot);
                        }
                    }
                }
                this.hasAttacked = false;
                this.needsSwitchBack = false;
                this.previousSlot = -1;
            }
        }
    }

    private class_1657 getTargetPlayer() {
        if (mc.field_1765 != null) {
            if (mc.field_1765.method_17783() == class_239.class_240.field_1331) {
                class_1297 entity = ((class_3966)mc.field_1765).method_17782();
                if (entity instanceof class_1657) {
                    class_1657 player = entity;
                    if (player != mc.field_1724) {
                        return player;
                    }
                }
            }
        }
        return null;
    }

    private int findAxeSlot() {
        int bestScore = -1;
        int bestSlot = -1;
        for (int i = 0; i < 9; i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (stack.method_7909() instanceof class_1743) {
                int score = this.getAxeStrengthScore(stack);
                if (score > bestScore) {
                    bestScore = score;
                    bestSlot = i;
                }
            }
        }
        return bestSlot;
    }

    private int getAxeStrengthScore(class_1799 stack) {
        if (stack.method_31574(class_1802.field_22025)) {
            return 6;
        }
        if (stack.method_31574(class_1802.field_8556)) {
            return 5;
        }
        if (stack.method_31574(class_1802.field_8475)) {
            return 4;
        }
        if (stack.method_31574(class_1802.field_8825)) {
            return 3;
        }
        if (stack.method_31574(class_1802.field_8062)) {
            return 2;
        }
        if (stack.method_31574(class_1802.field_8406)) {
            return 1;
        }
        return 0;
    }

    private void swapToSlot(int slot) {
        if (mc.field_1724 == null) {
            return;
        }
        if (slot < 0 || slot > 8) {
            return;
        }
        mc.field_1724.method_31548().method_61496(slot);
    }
}
