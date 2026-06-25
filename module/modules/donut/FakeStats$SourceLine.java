/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import net.minecraft.class_2561;

private static final record FakeStats.SourceLine {
    private final int score;
    private final class_2561 text;

    private FakeStats.SourceLine(int score, class_2561 text) {
        this.score = score;
        this.text = text;
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

    public int score() {
        return this.score;
    }

    public class_2561 text() {
        return this.text;
    }
}
