/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.ActivatableModule;
import com.xenon.module.Category;
import com.xenon.setting.Setting;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import org.lwjgl.glfw.GLFW;

public final class AutoHitCrystal
extends ActivatableModule {
    private final Setting<Integer> delay = new Setting("Delay (ticks)", 1, 0, 10);
    private int cooldown = 0;

    public AutoHitCrystal() {
        super("AutoHitCrystal", Category.COMBAT);
        this.addSetting(this.delay);
    }

    public void onEnable() {
        this.cooldown = 0;
    }

    public void onActivationKeyPressed() {
    }

    private boolean isActivationHeld() {
        int key = this.getActivationKey();
        if (key == 0) {
            return true;
        }
        if (mc.method_22683() == null) {
            return false;
        }
        try {
            return GLFW.glfwGetKey(mc.method_22683().method_4490(), key) == 1;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void onTick() {
        if (this.cooldown > 0) {
            this.cooldown = this.cooldown - 1;
            return;
        }
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            return;
        }
        if (mc.field_1755 != null) {
            return;
        }
        if (!this.isActivationHeld()) {
            return;
        }
        if (mc.field_1765 == null || mc.field_1765.method_17783() != class_239.class_240.field_1332) {
            return;
        }
        class_3965 lookHit = mc.field_1765;
        class_2338 surface = lookHit.method_17777();
        class_2338 obsidianPos = surface.method_10084();
        class_2338 crystalPos = obsidianPos.method_10084();
        class_1511 existing = this.findCrystalAt(crystalPos);
        if (existing != null) {
            mc.field_1761.method_2918(mc.field_1724, existing);
            mc.field_1724.method_6104(class_1268.field_5808);
            this.cooldown = ((Integer)this.delay.getValue()).intValue();
            return;
        }
        if (!this.isObsidianLike(obsidianPos)) {
            int obsSlot = this.findHotbarSlot(class_1802.field_8281);
            if (obsSlot < 0) {
                return;
            }
            if (mc.field_1724.method_31548().method_67532() != obsSlot) {
                mc.field_1724.method_31548().method_61496(obsSlot);
            }
            class_3965 placeObs = new class_3965(class_243.method_24953(surface).method_1031(0, 0.5, 0), class_2350.field_11036, surface, false);
            mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, placeObs);
            mc.field_1724.method_6104(class_1268.field_5808);
            this.cooldown = ((Integer)this.delay.getValue()).intValue();
            return;
        }
        if (!this.canPlaceCrystal(crystalPos)) {
            return;
        }
        int crystalSlot = this.findHotbarSlot(class_1802.field_8301);
        if (crystalSlot < 0) {
            return;
        }
        if (mc.field_1724.method_31548().method_67532() != crystalSlot) {
            mc.field_1724.method_31548().method_61496(crystalSlot);
        }
        class_3965 placeCrystal = new class_3965(class_243.method_24953(obsidianPos).method_1031(0, 0.5, 0), class_2350.field_11036, obsidianPos, false);
        mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, placeCrystal);
        mc.field_1724.method_6104(class_1268.field_5808);
        this.cooldown = ((Integer)this.delay.getValue()).intValue();
    }

    private boolean isObsidianLike(class_2338 pos) {
        class_2680 s = mc.field_1687.method_8320(pos);
        return s.method_27852(class_2246.field_10540) || s.method_27852(class_2246.field_9987);
    }

    private boolean canPlaceCrystal(class_2338 crystalPos) {
        if (!mc.field_1687.method_22347(crystalPos)) {
            return false;
        }
        class_238 box = new class_238((double)crystalPos.method_10263(), (double)crystalPos.method_10264(), (double)crystalPos.method_10260(), (double)crystalPos.method_10263() + 1, (double)crystalPos.method_10264() + 2, (double)crystalPos.method_10260() + 1);
        return mc.field_1687.method_8335(null, box).isEmpty();
    }

    private class_1511 findCrystalAt(class_2338 pos) {
        class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 1, (double)pos.method_10264() + 2, (double)pos.method_10260() + 1);
        for (class_1297 e : mc.field_1687.method_8335(null, box)) {
            if (e instanceof class_1511) {
                class_1511 crystal = e;
                if (!crystal.method_5805()) continue;
                return crystal;
            }
        }
        return null;
    }

    private int findHotbarSlot(class_1792 item) {
        for (int i = 0; i < 9; i++) {
            if (!mc.field_1724.method_31548().method_5438(i).method_31574(item)) continue;
            return i;
        }
        return -1;
    }
}
