/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.Category;
import com.xenon.module.Module;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1820;
import net.minecraft.class_2211;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2397;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_7923;
import net.minecraft.class_9285;
import net.minecraft.class_9334;

public final class AutoTool
extends Module {
    public AutoTool() {
        super("Auto Tool", Category.MISC);
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            return;
        }
        if (!mc.field_1690.field_1886.method_1434() || mc.field_1765 == null) {
            return;
        }
        class_239 crosshairTarget = mc.field_1765;
        this.switchToBestWeapon();
        if (crosshairTarget.method_17783() == class_239.class_240.field_1331 && crosshairTarget instanceof class_3966) {
            return;
        }
        class_3965 blockHitResult = crosshairTarget;
        if (crosshairTarget.method_17783() == class_239.class_240.field_1332 && crosshairTarget instanceof class_3965) {
            this.switchToBestTool(blockHitResult.method_17777());
        }
    }

    private void switchToBestTool(class_2338 blockPos) {
        class_2680 blockState = mc.field_1687.method_8320(blockPos);
        class_1799 currentStack = mc.field_1724.method_6047();
        int bestSlot = -1;
        double bestEfficiency = -1;
        for (int slot = 0; slot < 9; slot++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(slot);
            double efficiency = AutoTool.calculateToolEfficiency(stack, blockState, AutoTool::lambda$switchToBestTool$0);
            if (efficiency > bestEfficiency) {
                bestEfficiency = efficiency;
                bestSlot = slot;
            }
        }
        if (bestSlot == -1) {
            return;
        }
        double currentEfficiency = AutoTool.calculateToolEfficiency(currentStack, blockState, AutoTool::lambda$switchToBestTool$1);
        if (bestEfficiency > currentEfficiency || !AutoTool.isToolItemStack(currentStack)) {
            this.swapToSlot(bestSlot);
        }
    }

    private void switchToBestWeapon() {
        int bestSlot = -1;
        double bestDamage = -inf;
        for (int slot = 0; slot < 9; slot++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(slot);
            if (!(stack.method_7960())) {
                double damage = this.getAttackDamage(stack);
                if (damage > bestDamage) {
                    bestDamage = damage;
                    bestSlot = slot;
                }
            }
        }
        if (bestSlot != -1) {
            this.swapToSlot(bestSlot);
        }
    }

    private double getAttackDamage(class_1799 stack) {
        class_9285 modifiers = stack.method_58694(class_9334.field_49636);
        double damage = 0;
        if (modifiers == null) return 0;
        for (class_9287 entry : modifiers.comp_2393()) {
            if (!entry.comp_2395().toString().contains("attack_damage")) continue;
            damage = damage + entry.comp_2396().comp_2449();
        }
        if (damage > 0) {
            return damage;
        }
        itemPath = class_7923.field_41178.method_10221(stack.method_7909()).method_12832();
        if (itemPath.endsWith("_sword")) {
            return 10 + this.getMaterialRank(itemPath);
        }
        if (itemPath.endsWith("_axe")) {
            return 5 + this.getMaterialRank(itemPath);
        }
        return 0;
    }

    private void swapToSlot(int slot) {
        if (slot < 0 || slot > 8) {
            return;
        }
        if (mc.field_1724.method_31548().method_67532() != slot) {
            mc.field_1724.method_31548().method_61496(slot);
        }
    }

    public static double calculateToolEfficiency(class_1799 itemStack, class_2680 blockState, Predicate<class_1799> predicate) {
        if (!predicate.test(itemStack) || !AutoTool.isToolItemStack(itemStack)) {
            return -1;
        }
        String itemPath = class_7923.field_41178.method_10221(itemStack.method_7909()).method_12832();
        boolean isSword = itemPath.endsWith("_sword");
        if (!itemStack.method_7951(blockState)) {
            if (!(isSword) || !(blockState.method_26204() instanceof class_2211)) {
                if (!itemStack.method_7909() instanceof class_1820 || !(blockState.method_26204() instanceof class_2397)) {
                    if (!blockState.method_26164(class_3481.field_15481)) {
                        return -1;
                    }
                }
            }
        }
        return itemStack.method_7924(blockState) * 1000f;
    }

    public static boolean isToolItemStack(class_1799 itemStack) {
        return AutoTool.isToolItem(itemStack.method_7909());
    }

    public static boolean isToolItem(class_1792 item) {
        if (item instanceof class_1820) {
            return true;
        }
        String itemPath = class_7923.field_41178.method_10221(item).method_12832();
        return itemPath.endsWith("_pickaxe") || itemPath.endsWith("_axe") || itemPath.endsWith("_shovel") || itemPath.endsWith("_hoe") || itemPath.endsWith("_sword");
    }

    private double getMaterialRank(String itemPath) {
        if (itemPath.startsWith("netherite_")) {
            return 6;
        }
        if (itemPath.startsWith("diamond_")) {
            return 5;
        }
        if (itemPath.startsWith("iron_")) {
            return 4;
        }
        if (itemPath.startsWith("golden_")) {
            return 3;
        }
        if (itemPath.startsWith("stone_")) {
            return 2;
        }
        if (itemPath.startsWith("wooden_")) {
            return 1;
        }
        return 0;
    }
}
