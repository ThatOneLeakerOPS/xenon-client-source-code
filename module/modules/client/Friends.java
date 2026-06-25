/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.client;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class Friends
extends Module {
    private static Friends INSTANCE;
    private final Setting<String> nameList = new Setting("Names", "");
    private final Setting<Boolean> antiTriggerbot = new Setting("Anti Triggerbot", true);
    private final Setting<Boolean> espColor = new Setting("ESP Color", true);
    private final Setting<Boolean> autoLog = new Setting("Auto Log", true);
    private final Setting<Boolean> spawnerProtect = new Setting("Spawner Protect", true);
    private final Setting<Color> friendColor = new Setting("Friend Color", new Color(0, 200, 255));

    public Friends() {
        super("Friends", Category.CLIENT);
        this.addSetting(this.nameList);
        this.addSetting(this.antiTriggerbot);
        this.addSetting(this.espColor);
        this.addSetting(this.autoLog);
        this.addSetting(this.spawnerProtect);
        this.addSetting(this.friendColor);
        INSTANCE = this;
    }

    public static boolean isFriend(String name) {
        if (INSTANCE == null || !INSTANCE.isEnabled() || name == null || name.isEmpty()) {
            return false;
        }
        String lower = name.trim().toLowerCase(Locale.ROOT);
        for (String n : Friends.parseNames((String)INSTANCE.nameList.getValue())) {
            if (!n.equalsIgnoreCase(lower)) continue;
            return true;
        }
        return false;
    }

    public static boolean isAntiTriggerbot() {
        return INSTANCE != null && ((Boolean)INSTANCE.antiTriggerbot.getValue()).booleanValue();
    }

    public static boolean isEspColor() {
        return INSTANCE != null && ((Boolean)INSTANCE.espColor.getValue()).booleanValue();
    }

    public static boolean isAutoLog() {
        return INSTANCE != null && ((Boolean)INSTANCE.autoLog.getValue()).booleanValue();
    }

    public static boolean isSpawnerProtect() {
        return INSTANCE != null && ((Boolean)INSTANCE.spawnerProtect.getValue()).booleanValue();
    }

    public static Color getColor() {
        if (INSTANCE == null) {
            return new Color(0, 200, 255);
        }
        Color c = INSTANCE.friendColor.getValue();
        if (c == null) {
            return new Color(0, 200, 255);
        }
        if (c.getAlpha() == 0) {
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
        }
        return c;
    }

    public static void refreshCache() {
    }

    public static List<String> getNames() {
        if (INSTANCE == null) {
            return List.of();
        }
        return Friends.parseNames((String)INSTANCE.nameList.getValue());
    }

    public static void setNames(List<String> names) {
        if (INSTANCE == null) {
            return;
        }
        INSTANCE.nameList.setValue(Friends.serializeNames(names));
    }

    private static List<String> parseNames(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String normalized = raw.replace('\n', ',').replace('\r', ',');
        LinkedHashSet<String> out = new LinkedHashSet();
        for (String part : normalized.split(",")) {
            String t = part == null ? "" : part.trim();
            if ((t.isEmpty())) continue;
            out.add(t.toLowerCase(Locale.ROOT));
        }
        return new ArrayList(out);
    }

    private static String serializeNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            String t = name == null ? "" : name.trim();
            while (t.isEmpty()) {
            }
            if (sb.isEmpty()) continue;
            sb.append(", ");
            sb.append(t);
        }
        return sb.toString();
    }
}
