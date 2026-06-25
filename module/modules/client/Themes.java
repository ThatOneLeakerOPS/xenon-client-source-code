/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.client;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.ModeSetting;
import java.awt.Color;

public final class Themes
extends Module {
    public static Themes INSTANCE;
    private final ModeSetting theme;

    public Themes() {
        super("Themes", Category.CLIENT);
        this.theme = new ModeSetting("Theme", "Xenon Green", "Xenon Green", "Sunset Orange", "Crimson Red", "Ocean Blue", "Royal Purple", "Hot Pink", "Cyber Cyan", "Sunshine Yellow", "Mint Fresh", "Midnight Gray");
        this.addSetting(this.theme);
        INSTANCE = this;
    }

    public static Color getAccent() {
        if (INSTANCE == null || !INSTANCE.isEnabled()) {
            return null;
        }
        String var0 = INSTANCE.theme.getValue();
        int var1 = -1;
        switch (var0.hashCode()) {
            case 1258351352:
                if (var0.equals("Sunset Orange")) {
                    var1 = 0;
                }
            case 1383414672:
                if (var0.equals("Crimson Red")) {
                    var1 = 1;
                }
            case 2110498428:
                if (var0.equals("Ocean Blue")) {
                    var1 = 2;
                }
            case -1448785099:
                if (var0.equals("Royal Purple")) {
                    var1 = 3;
                }
            case -284992087:
                if (var0.equals("Hot Pink")) {
                    var1 = 4;
                }
            case 1795509962:
                if (var0.equals("Cyber Cyan")) {
                    var1 = 5;
                }
            case -397815531:
                if (var0.equals("Sunshine Yellow")) {
                    var1 = 6;
                }
            case 535268336:
                if (var0.equals("Mint Fresh")) {
                    var1 = 7;
                }
            case -416025165:
                if (var0.equals("Midnight Gray")) {
                    var1 = 8;
                }
            case 1162322787:
                if (var0.equals("Xenon Green")) {
                    var1 = 9;
                }
            default:
                switch (var1) {
                    case 0:
                        return new Color(255, 140, 50);
                    case 1:
                        return new Color(229, 75, 75);
                    case 2:
                        return new Color(72, 160, 255);
                    case 3:
                        return new Color(170, 110, 240);
                    case 4:
                        return new Color(255, 100, 180);
                    case 5:
                        return new Color(80, 220, 230);
                    case 6:
                        return new Color(250, 210, 80);
                    case 7:
                        return new Color(120, 230, 180);
                    case 8:
                        return new Color(160, 170, 185);
                    case 9:
                        return new Color(107, 211, 165);
                    default:
                        return null;
                }
        }
    }
}
