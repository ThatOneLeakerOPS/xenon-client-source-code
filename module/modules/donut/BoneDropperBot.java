/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.ModeSetting;
import com.xenon.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1268;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_465;
import net.minecraft.class_634;

public final class BoneDropperBot
extends Module {
    private static final int MIN_DELAY_MS = 100;
    private static final int MAX_DELAY_MS = 2000;
    private static final int DEFAULT_DELAY_MS = 300;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 36;
    private static final int PRIMARY_CLICK_BUTTON = 0;
    private static final long SPAWNER_DROP_TIMEOUT_MS = 4000L;
    private final ModeSetting mode;
    private final Setting<Integer> delayMs;
    private BotState state;
    private String lastMode;
    private long nextActionAtMs;
    private int spawnerBoneCountBeforeDrop;
    private long spawnerDropRequestedAtMs;
    private boolean spawnerGridWasFullBeforeDrop;

    public BoneDropperBot() {
        super("BoneDropper", Category.DONUT);
        this.mode = new ModeSetting("Mode", "Spawner", "Spawner", "Orders");
        this.delayMs = new Setting("Delay", 300, 100, 2000);
        this.state = BotState.SPAWNER_OPEN_MENU;
        this.lastMode = this.mode.getValue();
        this.addSetting(this.mode);
        this.addSetting(this.delayMs);
    }

    public void onEnable() {
        this.resetState();
    }

    public void onDisable() {
        this.state = BotState.SPAWNER_OPEN_MENU;
        this.nextActionAtMs = 0L;
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            return;
        }
        if (mc.field_1755 != null && !(mc.field_1755 instanceof class_465)) {
            return;
        }
        if (!((String)this.mode.getValue()).equalsIgnoreCase(this.lastMode)) {
            this.resetState();
        }
        long now = System.currentTimeMillis();
        if (now < this.nextActionAtMs) {
            return;
        }
        if (this.mode.is("Spawner")) {
            this.tickSpawnerMode();
        } else {
            this.tickOrdersMode();
        }
    }

    private void tickSpawnerMode() {
        switch (this.state.ordinal()) {
            case 0:
                if (this.isHandledMenuOpen()) {
                    this.state = BotState.SPAWNER_WAIT_MENU;
                    this.scheduleNextAction();
                    return;
                }
                this.interactWithTargetBlock();
                this.state = BotState.SPAWNER_WAIT_MENU;
                this.scheduleNextAction();
                return;
            case 1:
                if (!this.isHandledMenuOpen()) {
                    this.state = BotState.SPAWNER_OPEN_MENU;
                    this.scheduleNextAction();
                    return;
                }
                this.state = BotState.SPAWNER_SCAN_GRID;
                this.scheduleNextAction();
                return;
            case 2:
                class_1703 handler = this.getMenuHandler();
                if (handler == null) {
                    this.state = BotState.SPAWNER_OPEN_MENU;
                    this.scheduleNextAction();
                    return;
                }
                if (!this.isSpawnerGridFull(handler)) {
                    this.scheduleNextAction();
                    return;
                }
                this.state = BotState.SPAWNER_CLICK_DROPPER;
                this.scheduleNextAction();
                return;
            case 3:
                class_1703 handler = this.getMenuHandler();
                if (handler == null) {
                    this.state = BotState.SPAWNER_OPEN_MENU;
                    this.scheduleNextAction();
                    return;
                }
                class_1735 dropperSlot = this.findSpawnerDropperSlot(handler);
                if (dropperSlot == null) {
                    this.scheduleNextAction();
                    return;
                }
                this.spawnerGridWasFullBeforeDrop = this.isSpawnerGridFull(handler);
                if (!this.spawnerGridWasFullBeforeDrop) {
                    this.state = BotState.SPAWNER_SCAN_GRID;
                    this.scheduleNextAction();
                    return;
                }
                this.spawnerBoneCountBeforeDrop = this.countInventoryItem(class_1802.field_8606);
                this.spawnerDropRequestedAtMs = System.currentTimeMillis();
                this.clickSlot(dropperSlot);
                this.state = BotState.SPAWNER_WAIT_DROP_CONFIRM;
                this.scheduleNextAction();
                return;
            case 4:
                long now = System.currentTimeMillis();
                class_1703 handler = this.getMenuHandler();
                boolean gridCleared = this.spawnerGridWasFullBeforeDrop && handler != null && !this.isSpawnerGridFull(handler);
                boolean gainedBones = this.countInventoryItem(class_1802.field_8606) > this.spawnerBoneCountBeforeDrop;
                if (gridCleared || gainedBones) {
                    if (handler != null) {
                        this.closeMenu();
                    }
                    this.state = BotState.SPAWNER_DONE;
                    this.nextActionAtMs = 9223372036854775807L;
                    return;
                }
                if (!(this.spawnerDropRequestedAtMs <= 0L) && !(now - this.spawnerDropRequestedAtMs <= 4000L)) {
                    this.state = handler == null ? BotState.SPAWNER_OPEN_MENU : BotState.SPAWNER_SCAN_GRID;
                }
                this.scheduleNextAction();
                return;
                this.scheduleNextAction();
                return;
            case 5:
                return;
            default:
                this.state = BotState.SPAWNER_OPEN_MENU;
                this.scheduleNextAction();
                return;
        }
    }

    private void tickOrdersMode() {
        switch (this.state.ordinal()) {
            case 6:
                if (!this.isHandledMenuOpen()) {
                    this.sendServerCommand("/order");
                    this.state = BotState.ORDERS_WAIT_MENU;
                    this.scheduleNextAction();
                    return;
                }
                this.state = BotState.ORDERS_CLICK_CHEST_ONE;
                this.scheduleNextAction();
                return;
            case 7:
                if (!this.isHandledMenuOpen()) {
                    this.sendServerCommand("/order");
                    this.scheduleNextAction();
                    return;
                }
                this.state = BotState.ORDERS_CLICK_CHEST_ONE;
                this.scheduleNextAction();
                return;
            case 8:
                this.handleOrdersClickStep(MenuTarget.CHEST, BotState.ORDERS_CLICK_BONE);
                return;
            case 9:
                this.handleOrdersClickStep(MenuTarget.BONE, BotState.ORDERS_CLICK_CHEST_TWO);
                return;
            case 10:
                this.handleOrdersClickStep(MenuTarget.CHEST, BotState.ORDERS_CLICK_DROPPER_ONE);
                return;
            case 11:
                this.handleOrdersClickStep(MenuTarget.DROPPER, BotState.ORDERS_CLICK_ARROW);
                return;
            case 12:
                this.handleOrdersClickStep(MenuTarget.ARROW, BotState.ORDERS_CLICK_DROPPER_TWO);
                return;
            case 13:
                this.handleOrdersClickStep(MenuTarget.DROPPER, BotState.ORDERS_CLICK_CHEST_ONE);
                return;
            default:
                this.state = BotState.ORDERS_SEND_COMMAND;
                this.scheduleNextAction();
                return;
        }
    }

    private void handleOrdersClickStep(MenuTarget target, BotState nextState) {
        class_1703 handler = this.getMenuHandler();
        if (handler == null) {
            this.sendServerCommand("/order");
            this.scheduleNextAction();
            return;
        }
        if (target == MenuTarget.DROPPER) { /* goto L37; */ }
        class_1735 slot = this.findUpperMenuSlot(handler, target, target == MenuTarget.ARROW);
        if (slot == null) {
            this.scheduleNextAction();
            return;
        }
        this.clickSlot(slot);
        this.state = nextState;
        this.scheduleNextAction();
    }

    private boolean isSpawnerGridFull(class_1703 handler) {
        List<class_1735> gridSlots = this.getSpawnerGridSlots(handler);
        if (gridSlots.isEmpty()) {
            return false;
        }
        for (class_1735 slot : gridSlots) {
            while (!slot.method_7682()) {
            }
            class_1799 stack = slot.method_7677();
            if (!stack.method_7960() || !this.matchesTarget(stack, MenuTarget.BONE)) continue;
            return false;
        }
        return true;
    }

    private List<class_1735> getSpawnerGridSlots(class_1703 handler) {
        List<class_1735> upperSlots = this.getUpperMenuSlots(handler);
        if (upperSlots.isEmpty()) {
            return List.of();
        }
        int maxY = upperSlots.stream().mapToInt(BoneDropperBot::lambda$getSpawnerGridSlots$0).max().orElse(-2147483648);
        List<class_1735> gridSlots = new ArrayList();
        for (class_1735 slot : upperSlots) {
            if (slot.field_7872 >= maxY) continue;
            gridSlots.add(slot);
        }
        return gridSlots.isEmpty() ? upperSlots : gridSlots;
    }

    private class_1735 findSpawnerDropperSlot(class_1703 handler) {
        List<class_1735> upperSlots = this.getUpperMenuSlots(handler);
        if (upperSlots.isEmpty()) {
            return null;
        }
        int maxY = upperSlots.stream().mapToInt(BoneDropperBot::lambda$findSpawnerDropperSlot$1).max().orElse(-2147483648);
        class_1735 controlDropper = this.chooseBestMatchingSlot(upperSlots, MenuTarget.DROPPER, true, maxY);
        if (controlDropper != null) {
            return controlDropper;
        }
        class_1735 anyDropper = this.chooseBestMatchingSlot(upperSlots, MenuTarget.DROPPER, true, -2147483648);
        if (anyDropper != null) {
            return anyDropper;
        }
        for (int i = upperSlots.size() - 1; i >= 0; i--) {
            class_1735 slot = upperSlots.get(i);
            if (!slot.method_7682()) continue;
            return slot;
        }
        return null;
    }

    private class_1735 findUpperMenuSlot(class_1703 handler, MenuTarget target, boolean preferBottomRight) {
        return this.chooseBestMatchingSlot(this.getUpperMenuSlots(handler), target, preferBottomRight, -2147483648);
    }

    private class_1735 chooseBestMatchingSlot(List<class_1735> slots, MenuTarget target, boolean preferBottomRight, int yFilter) {
        class_1735 best = null;
        for (class_1735 slot : slots) {
            while (!slot.method_7682()) {
            }
            while (yFilter != -2147483648) {
                if (slot.field_7872 == yFilter) break;
            }
            while (!this.matchesTarget(slot.method_7677(), target)) {
            }
            while (best == null) {
                best = slot;
            }
            if (preferBottomRight) {
                if ((slot.field_7872 <= best.field_7872) || slot.field_7872 == best.field_7872) continue;
                best = slot;
            } else {
                if ((slot.field_7872 >= best.field_7872) || slot.field_7872 == best.field_7872) continue;
                best = slot;
            }
        }
        return best;
    }

    private boolean matchesTarget(class_1799 stack, MenuTarget target) {
        if (stack == null || stack.method_7960()) {
            return false;
        }
        class_1792 item = stack.method_7909();
        String lowerName = stack.method_7964().getString().toLowerCase(Locale.ROOT);
        switch (target.ordinal()) {
            default:
                throw new MatchException(null);
            case 0:
                if (item != class_1802.field_8606) {
                    return lowerName.contains("bone");
                }
            case 1:
                if (item != class_1802.field_8106) {
                    if (item != class_1802.field_8247) {
                        if (item != class_1802.field_8466) {
                            if (lowerName.contains("chest")) { /* goto @147; */ }
                        }
                    }
                }
                return true;
            case 2:
                if (item != class_1802.field_8878) {
                    return lowerName.contains("dropper");
                }
            case 3:
                return item == class_1802.field_8107 || lowerName.contains("arrow") || lowerName.contains("pfeil");
        }
    }

    private List<class_1735> getUpperMenuSlots(class_1703 handler) {
        if (handler == null || handler.field_7761 == null || handler.field_7761.isEmpty()) {
            return List.of();
        }
        int upperSlotCount = Math.max(0, handler.field_7761.size() - 36);
        if (upperSlotCount == 0) {
            upperSlotCount = handler.field_7761.size();
        }
        List<class_1735> upperSlots = new ArrayList(upperSlotCount);
        for (int i = 0; i < upperSlotCount; i++) {
            upperSlots.add((class_1735)handler.field_7761.get(i));
        }
        return upperSlots;
    }

    private int countInventoryItem(class_1792 item) {
        if (mc.field_1724 == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < mc.field_1724.method_31548().method_5439(); i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (!stack.method_7960()) {
                if (!stack.method_31574(item)) continue;
                count = count + stack.method_7947();
            }
        }
        return count;
    }

    private void clickSlot(class_1735 slot) {
        if (slot == null || mc.field_1724 == null || mc.field_1761 == null) {
            return;
        }
        mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slot.field_7874, 0, class_1713.field_7790, mc.field_1724);
    }

    private void interactWithTargetBlock() {
        var var2 = mc.field_1765;
        class_3965 hit = var2;
        if (!(var2 instanceof class_3965)|| mc.field_1765.method_17783() != class_239.class_240.field_1332) {
            return;
        }
        mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
        mc.field_1724.method_6104(class_1268.field_5808);
    }

    private void closeMenu() {
        if (mc.field_1724 == null) {
            return;
        }
        if (mc.field_1755 instanceof class_465) {
            mc.field_1724.method_7346();
            mc.method_1507(null);
        }
    }

    private class_1703 getMenuHandler() {
        if (!(mc.field_1755 instanceof class_465)|| mc.field_1724 == null) {
            return null;
        }
        return mc.field_1724.field_7512;
    }

    private boolean isHandledMenuOpen() {
        return this.getMenuHandler() != null;
    }

    private void sendServerCommand(String command) {
        class_634 networkHandler = mc.field_1724 != null ? mc.field_1724.field_3944 : mc.method_1562();
        if (networkHandler == null) {
            return;
        }
        String commandWithoutSlash = command.startsWith("/") ? command.substring(1) : command;
        try {
            networkHandler.method_45730(commandWithoutSlash);
        }
        catch (Throwable ignored) {
            networkHandler.method_45729(command);
            return;
        }
    }

    private void resetState() {
        this.lastMode = this.mode.getValue();
        this.state = this.mode.is("Spawner") ? BotState.SPAWNER_OPEN_MENU : BotState.ORDERS_SEND_COMMAND;
        this.nextActionAtMs = 0L;
        this.spawnerBoneCountBeforeDrop = 0;
        this.spawnerDropRequestedAtMs = 0L;
        this.spawnerGridWasFullBeforeDrop = false;
    }

    private void scheduleNextAction() {
        this.nextActionAtMs = System.currentTimeMillis() + (long)((Integer)this.delayMs.getValue()).intValue();
    }
}
