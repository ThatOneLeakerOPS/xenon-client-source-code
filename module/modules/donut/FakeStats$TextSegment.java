/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import net.minecraft.class_2583;

private static final record FakeStats.TextSegment {
    private final String value;
    private final class_2583 style;

    private FakeStats.TextSegment(String value, class_2583 style) {
        this.value = value;
        this.style = style;
    }

    public final String toString() {
        return /* lambda: toString */ this;
    }

    public final int hashCode() {
        return /* lambda: hashCode */ this;
    }

    public final boolean equals(Object o) {
        return /* lambda: equals */ this, o;
    }

    public String value() {
        return this.value;
    }

    public class_2583 style() {
        return this.style;
    }
}
