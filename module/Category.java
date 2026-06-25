/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module;

import net.minecraft.class_1799;
import net.minecraft.class_1802;

public enum Category {
    public static final Category MISC = new Category("MISC", 0, "Misc");
    public static final Category RENDER = new Category("RENDER", 1, "Render");
    public static final Category COMBAT = new Category("COMBAT", 2, "Combat");
    public static final Category DONUT = new Category("DONUT", 3, "Donut");
    public static final Category CLIENT = new Category("CLIENT", 4, "Client");
    private final String name;
    private static final /* synthetic */ Category[] $VALUES;

    public static Category[] values() {
        return $VALUES.clone();
    }

    public static Category valueOf(String name) {
        return Enum.valueOf(Category.class, name);
    }

    private Category(String name) {
        super(var1, var2);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public class_1799 getIcon() {
        switch (this.ordinal()) {
            default:
                throw new MatchException(null);
            case 0:
                return new class_1799(class_1802.field_8251);
            case 1:
                return new class_1799(class_1802.field_27070);
            case 2:
                return new class_1799(class_1802.field_22022);
            case 3:
                return new class_1799(class_1802.field_8207);
            case 4:
                return new class_1799(class_1802.field_8137);
        }
    }

    static {
        $VALUES = Category.$values();
    }
}
