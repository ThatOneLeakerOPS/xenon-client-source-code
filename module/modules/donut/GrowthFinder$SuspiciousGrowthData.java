/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import net.minecraft.class_1923;

private static final record GrowthFinder.SuspiciousGrowthData {
    private final class_1923 chunkPos;
    private final int suspicionLevel;
    private final class_1923 baseChunk;
    private final boolean extreme;
    private final boolean source;
    private final int maxVineLength;

    private GrowthFinder.SuspiciousGrowthData(class_1923 chunkPos, int suspicionLevel, class_1923 baseChunk, boolean extreme, boolean source, int maxVineLength) {
        this.chunkPos = chunkPos;
        this.suspicionLevel = suspicionLevel;
        this.baseChunk = baseChunk;
        this.extreme = extreme;
        this.source = source;
        this.maxVineLength = maxVineLength;
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

    public class_1923 chunkPos() {
        return this.chunkPos;
    }

    public int suspicionLevel() {
        return this.suspicionLevel;
    }

    public class_1923 baseChunk() {
        return this.baseChunk;
    }

    public boolean extreme() {
        return this.extreme;
    }

    public boolean source() {
        return this.source;
    }

    public int maxVineLength() {
        return this.maxVineLength;
    }
}
