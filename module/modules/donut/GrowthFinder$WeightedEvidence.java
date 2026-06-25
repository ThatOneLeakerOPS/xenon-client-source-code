/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import net.minecraft.class_2338;

private static final record GrowthFinder.WeightedEvidence {
    private final class_2338 pos;
    private final double weight;

    private GrowthFinder.WeightedEvidence(class_2338 pos, double weight) {
        this.pos = pos;
        this.weight = weight;
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

    public class_2338 pos() {
        return this.pos;
    }

    public double weight() {
        return this.weight;
    }
}
