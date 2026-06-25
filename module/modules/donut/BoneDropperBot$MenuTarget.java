/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

private static enum BoneDropperBot.MenuTarget {
    public static final BoneDropperBot.MenuTarget BONE = new BoneDropperBot.MenuTarget("BONE", 0);
    public static final BoneDropperBot.MenuTarget CHEST = new BoneDropperBot.MenuTarget("CHEST", 1);
    public static final BoneDropperBot.MenuTarget DROPPER = new BoneDropperBot.MenuTarget("DROPPER", 2);
    public static final BoneDropperBot.MenuTarget ARROW = new BoneDropperBot.MenuTarget("ARROW", 3);
    private static final /* synthetic */ BoneDropperBot.MenuTarget[] $VALUES;

    public static BoneDropperBot.MenuTarget[] values() {
        return (BoneDropperBot.MenuTarget[])$VALUES.clone();
    }

    public static BoneDropperBot.MenuTarget valueOf(String name) {
        return (BoneDropperBot.MenuTarget)Enum.valueOf(BoneDropperBot.MenuTarget.class, name);
    }

    private BoneDropperBot.MenuTarget() {
        super(var1, var2);
    }

    static {
        $VALUES = BoneDropperBot.MenuTarget.$values();
    }
}
