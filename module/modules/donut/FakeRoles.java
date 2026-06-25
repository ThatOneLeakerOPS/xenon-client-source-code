/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.ModeSetting;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_5251;

public final class FakeRoles
extends Module {
    public static FakeRoles instance;
    private static final String MODE_NONE = "None";
    private static final String MODE_SRMOD = "SRMOD";
    private static final String MODE_MEDIA = "MEDIA";
    private static final String MODE_SRADMIN = "SRADMIN";
    private static final int TAG_BRACKET = 8355711;
    private static final int TAG_SRMOD = 5635925;
    private static final int TAG_MEDIA = 16733695;
    private static final int TAG_SRADMIN = 16733525;
    private static final int TAG_WHITE = 16777215;
    private final ModeSetting role;

    public FakeRoles() {
        super("FakeRoles", Category.DONUT);
        this.role = new ModeSetting("Role", "None", "None", "SRMOD", "MEDIA", "SRADMIN");
        instance = this;
        this.addSetting(this.role);
    }

    public static boolean isActive() {
        return instance != null && instance.isEnabled() && !instance.role.is("None") && mc != null && mc.field_1724 != null;
    }

    public static String getActiveRole() {
        return !FakeRoles.isActive() ? null : (String)instance.role.getValue();
    }

    public static String getPlayerName() {
        return mc != null && mc.method_1548() != null ? mc.method_1548().method_1676() : null;
    }

    public static class_2561 modifyChatText(class_2561 original) {
        if (FakeRoles.isActive()) {
            if (original != null) {
                String playerName = FakeRoles.getPlayerName();
                if (playerName != null) {
                    if (!playerName.isBlank()) {
                        String plain = original.getString();
                        if (!plain.contains(playerName)) {
                            return original;
                        }
                        class_2561 prefixed = FakeRoles.buildPrefixedName(playerName);
                        int idx = plain.indexOf(playerName);
                        class_5250 result = class_2561.method_43473();
                        if (idx > 0) {
                            result.method_10852(class_2561.method_43470(plain.substring(0, idx)));
                        }
                        result.method_10852(prefixed);
                        int afterIdx = idx + playerName.length();
                        if (afterIdx < plain.length()) {
                            result.method_10852(class_2561.method_43470(plain.substring(afterIdx)));
                        }
                        return result;
                    }
                }
                return original;
            }
        }
        return original;
    }

    public static class_2561 buildPrefixedDisplayName(String playerName) {
        return FakeRoles.isActive() && playerName != null ? FakeRoles.buildPrefixedName(playerName) : null;
    }

    public static String getPrefixedNameString() {
        if (!FakeRoles.isActive()) {
            return null;
        }
        String playerName = FakeRoles.getPlayerName();
        if (playerName == null) {
            return null;
        }
        class_2561 prefixed = FakeRoles.buildPrefixedName(playerName);
        return prefixed.getString();
    }

    public static class_2583 getRolePrefixStyle() {
        if (!FakeRoles.isActive()) {
            return class_2583.field_24360;
        }
        String activeRole = instance.role.getValue();
        var var1 = activeRole;
        int var2 = -1;
        switch (var1.hashCode()) {
            case 79171619:
                if (var1.equals("SRMOD")) {
                    var2 = 0;
                }
            case 73234372:
                if (var1.equals("MEDIA")) {
                    var2 = 1;
                }
            case -1236884432:
                if (var1.equals("SRADMIN")) {
                    var2 = 2;
                }
            default:
                switch (var2) {
                    case 0:
                        return FakeRoles.roleStyle(5635925);
                    case 1:
                        return FakeRoles.roleStyle(16733695);
                    case 2:
                        return FakeRoles.roleStyle(16733525);
                    default:
                        return class_2583.field_24360;
                }
        }
    }

    public static class_2583 getRoleBracketStyle() {
        return FakeRoles.isActive() ? class_2583.field_24360.method_27703(class_5251.method_27717(8355711)).method_10982(false) : class_2583.field_24360;
    }

    public static class_2583 getRolePrefixStyleForChar(int codePoint) {
        return codePoint != 91 && codePoint != 93 && !Character.isWhitespace(codePoint) ? FakeRoles.getRolePrefixStyle() : FakeRoles.getRoleBracketStyle();
    }

    public static class_2583 getRoleNameStyle() {
        if (!FakeRoles.isActive()) {
            return class_2583.field_24360;
        }
        String activeRole = instance.role.getValue();
        var var1 = activeRole;
        int var2 = -1;
        switch (var1.hashCode()) {
            case 79171619:
                if (var1.equals("SRMOD")) {
                    var2 = 0;
                }
            case 73234372:
                if (var1.equals("MEDIA")) {
                    var2 = 1;
                }
            case -1236884432:
                if (var1.equals("SRADMIN")) {
                    var2 = 2;
                }
            default:
                switch (var2) {
                    case 0:
                        return FakeRoles.roleStyle(5635925);
                    case 1:
                        return class_2583.field_24360.method_27703(class_5251.method_27717(16777215)).method_10982(false);
                    case 2:
                        return FakeRoles.roleStyle(16733525);
                    default:
                        return class_2583.field_24360;
                }
        }
    }

    public static String getRolePrefixString() {
        if (!FakeRoles.isActive()) {
            return null;
        }
        String activeRole = instance.role.getValue();
        var var1 = activeRole;
        int var2 = -1;
        switch (var1.hashCode()) {
            case 79171619:
                if (var1.equals("SRMOD")) {
                    var2 = 0;
                }
            case 73234372:
                if (var1.equals("MEDIA")) {
                    var2 = 1;
                }
            case -1236884432:
                if (var1.equals("SRADMIN")) {
                    var2 = 2;
                }
            default:
                switch (var2) {
                    case 0:
                        return "[SR.MOD] ";
                    case 1:
                        return "[MEDIA] ";
                    case 2:
                        return "[SR.ADMIN] ";
                    default:
                        return null;
                }
        }
    }

    private static class_2561 buildPrefixedName(String playerName) {
        String activeRole = instance.role.getValue();
        var var2 = activeRole;
        int var3 = -1;
        switch (var2.hashCode()) {
            case 79171619:
                if (var2.equals("SRMOD")) {
                    var3 = 0;
                }
            case 73234372:
                if (var2.equals("MEDIA")) {
                    var3 = 1;
                }
            case -1236884432:
                if (var2.equals("SRADMIN")) {
                    var3 = 2;
                }
            default:
                switch (var3) {
                    case 0:
                        return FakeRoles.buildSrmodName(playerName);
                    case 1:
                        return FakeRoles.buildMediaName(playerName);
                    case 2:
                        return FakeRoles.buildSradminName(playerName);
                    default:
                        return class_2561.method_43470(playerName);
                }
        }
    }

    private static class_2561 buildSrmodName(String playerName) {
        class_5250 result = class_2561.method_43473();
        FakeRoles.appendTag(result, "SR.MOD", FakeRoles.roleStyle(5635925));
        result.method_10852(class_2561.method_43470(playerName).method_10862(FakeRoles.getRoleNameStyle()));
        return result;
    }

    private static class_2561 buildMediaName(String playerName) {
        class_5250 result = class_2561.method_43473();
        FakeRoles.appendTag(result, "MEDIA", FakeRoles.roleStyle(16733695));
        result.method_10852(class_2561.method_43470(playerName).method_10862(FakeRoles.getRoleNameStyle()));
        return result;
    }

    private static class_2561 buildSradminName(String playerName) {
        class_5250 result = class_2561.method_43473();
        FakeRoles.appendTag(result, "SR.ADMIN", FakeRoles.roleStyle(16733525));
        result.method_10852(class_2561.method_43470(playerName).method_10862(FakeRoles.getRoleNameStyle()));
        return result;
    }

    private static void appendTag(class_5250 result, String roleText, class_2583 roleStyle) {
        class_2583 bracketStyle = class_2583.field_24360.method_27703(class_5251.method_27717(8355711)).method_10982(false);
        result.method_10852(class_2561.method_43470("[").method_10862(bracketStyle));
        result.method_10852(class_2561.method_43470(roleText).method_10862(roleStyle));
        result.method_10852(class_2561.method_43470("] ").method_10862(bracketStyle));
    }

    private static class_2583 roleStyle(int rgb) {
        return class_2583.field_24360.method_27703(class_5251.method_27717(rgb)).method_10982(true);
    }
}
