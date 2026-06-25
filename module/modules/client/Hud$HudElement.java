/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.client;

public static enum Hud.HudElement {
    public static final Hud.HudElement WATERMARK = new Hud.HudElement("WATERMARK", 0, "Watermark");
    public static final Hud.HudElement COORDINATES = new Hud.HudElement("COORDINATES", 1, "Coordinates");
    public static final Hud.HudElement INFO = new Hud.HudElement("INFO", 2, "Info");
    public static final Hud.HudElement MODULE_LIST = new Hud.HudElement("MODULE_LIST", 3, "Module List");
    public static final Hud.HudElement POTION_EFFECTS = new Hud.HudElement("POTION_EFFECTS", 4, "Potion Effects");
    public static final Hud.HudElement ARMOR = new Hud.HudElement("ARMOR", 5, "Armor");
    public static final Hud.HudElement KEYBINDS = new Hud.HudElement("KEYBINDS", 6, "Keybinds");
    public static final Hud.HudElement SPOTIFY_HUD = new Hud.HudElement("SPOTIFY_HUD", 7, "Spotify HUD");
    public final String label;
    private static final /* synthetic */ Hud.HudElement[] $VALUES;

    public static Hud.HudElement[] values() {
        return (Hud.HudElement[])$VALUES.clone();
    }

    public static Hud.HudElement valueOf(String name) {
        return (Hud.HudElement)Enum.valueOf(Hud.HudElement.class, name);
    }

    private Hud.HudElement(String label) {
        super(var1, var2);
        this.label = label;
    }

    static {
        $VALUES = Hud.HudElement.$values();
    }
}
