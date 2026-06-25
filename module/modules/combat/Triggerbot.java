/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.Friends;
import com.xenon.setting.Setting;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_239;
import net.minecraft.class_3966;

public final class Triggerbot
extends Module {
    private static final int FIXED_DELAY_TICKS = 9;
    private final Setting<Boolean> onlyCrit = new Setting("Only Crit", false);
    private final Setting<Boolean> checkShield = new Setting("Check Shield", false);
    private int delayCounter = 0;

    public Triggerbot() {
        super("Triggerbot", Category.COMBAT);
        this.addSetting(this.onlyCrit);
        this.addSetting(this.checkShield);
    }

    public void onEnable() {
        this.delayCounter = 0;
    }

    public void onDisable() {
        this.delayCounter = 0;
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        if (mc.field_1755 != null) {
            return;
        }
        if (this.delayCounter > 0) {
            this.delayCounter = this.delayCounter - 1;
            return;
        }
        if (mc.field_1765 == null || mc.field_1765.method_17783() != class_239.class_240.field_1331) {
            return;
        }
        class_1297 entity = mc.field_1765;
        if (entity instanceof class_3966) {
            class_3966 entityHitResult = entity;
        } else {
            return;
        }
        entity = entityHitResult.method_17782();
        if (!(entity instanceof class_1309)) {
            return;
        }
        if (entity == mc.field_1724) {
            return;
        }
        if (entity instanceof class_1657) {
            class_1657 p = entity;
            if (Friends.isAntiTriggerbot()) {
                if (Friends.isFriend(p.method_5477().getString())) {
                    return;
                }
            }
        }
        if (mc.field_1690.field_1886.method_1434()) {
            return;
        }
        if (!this.passesFilters((class_1309)entity)) {
            return;
        }
        this.performAttack(entity);
        this.delayCounter = 9;
    }

    private boolean passesFilters(class_1309 entity) {
        if (((Boolean)this.onlyCrit.getValue()).booleanValue() && !this.isValidCrit(mc.field_1724)) {
            return false;
        }
        if (((Boolean)this.checkShield.getValue()).booleanValue() && this.isHoldingShield(entity)) {
            return false;
        }
        return true;
    }

    private boolean isHoldingShield(class_1309 entity) {
        class_1799 main = entity.method_6047();
        class_1799 off = entity.method_6079();
        return main.method_7909() == class_1802.field_8255 || off.method_7909() == class_1802.field_8255;
    }

    private boolean isValidCrit(class_1657 p) {
        if (p.field_6017 <= 0.05000000074505806) {
            return false;
        }
        if (p.method_24828()) {
            return false;
        }
        if (p.method_5799() || p.method_5771() || p.method_6101() || p.method_5765()) {
            return false;
        }
        return !p.method_5624();
    }

    private void performAttack(class_1297 target) {
        mc.field_1761.method_2918(mc.field_1724, target);
        mc.field_1724.method_6104(class_1268.field_5808);
    }
}
