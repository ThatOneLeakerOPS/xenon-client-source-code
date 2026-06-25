/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

private static enum BoneDropperBot.BotState {
    public static final BoneDropperBot.BotState SPAWNER_OPEN_MENU = new BoneDropperBot.BotState("SPAWNER_OPEN_MENU", 0);
    public static final BoneDropperBot.BotState SPAWNER_WAIT_MENU = new BoneDropperBot.BotState("SPAWNER_WAIT_MENU", 1);
    public static final BoneDropperBot.BotState SPAWNER_SCAN_GRID = new BoneDropperBot.BotState("SPAWNER_SCAN_GRID", 2);
    public static final BoneDropperBot.BotState SPAWNER_CLICK_DROPPER = new BoneDropperBot.BotState("SPAWNER_CLICK_DROPPER", 3);
    public static final BoneDropperBot.BotState SPAWNER_WAIT_DROP_CONFIRM = new BoneDropperBot.BotState("SPAWNER_WAIT_DROP_CONFIRM", 4);
    public static final BoneDropperBot.BotState SPAWNER_DONE = new BoneDropperBot.BotState("SPAWNER_DONE", 5);
    public static final BoneDropperBot.BotState ORDERS_SEND_COMMAND = new BoneDropperBot.BotState("ORDERS_SEND_COMMAND", 6);
    public static final BoneDropperBot.BotState ORDERS_WAIT_MENU = new BoneDropperBot.BotState("ORDERS_WAIT_MENU", 7);
    public static final BoneDropperBot.BotState ORDERS_CLICK_CHEST_ONE = new BoneDropperBot.BotState("ORDERS_CLICK_CHEST_ONE", 8);
    public static final BoneDropperBot.BotState ORDERS_CLICK_BONE = new BoneDropperBot.BotState("ORDERS_CLICK_BONE", 9);
    public static final BoneDropperBot.BotState ORDERS_CLICK_CHEST_TWO = new BoneDropperBot.BotState("ORDERS_CLICK_CHEST_TWO", 10);
    public static final BoneDropperBot.BotState ORDERS_CLICK_DROPPER_ONE = new BoneDropperBot.BotState("ORDERS_CLICK_DROPPER_ONE", 11);
    public static final BoneDropperBot.BotState ORDERS_CLICK_ARROW = new BoneDropperBot.BotState("ORDERS_CLICK_ARROW", 12);
    public static final BoneDropperBot.BotState ORDERS_CLICK_DROPPER_TWO = new BoneDropperBot.BotState("ORDERS_CLICK_DROPPER_TWO", 13);
    private static final /* synthetic */ BoneDropperBot.BotState[] $VALUES;

    public static BoneDropperBot.BotState[] values() {
        return (BoneDropperBot.BotState[])$VALUES.clone();
    }

    public static BoneDropperBot.BotState valueOf(String name) {
        return (BoneDropperBot.BotState)Enum.valueOf(BoneDropperBot.BotState.class, name);
    }

    private BoneDropperBot.BotState() {
        super(var1, var2);
    }

    static {
        $VALUES = BoneDropperBot.BotState.$values();
    }
}
