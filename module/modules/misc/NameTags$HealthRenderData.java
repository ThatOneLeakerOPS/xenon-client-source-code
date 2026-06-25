/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

private static final record NameTags.HealthRenderData {
    private final int baseHeartCount;
    private final int fullHearts;
    private final boolean halfHeart;
    private final int emptyHearts;
    private final int absorptionFullHearts;
    private final boolean absorptionHalfHeart;
    private final int totalWidth;

    private NameTags.HealthRenderData(int baseHeartCount, int fullHearts, boolean halfHeart, int emptyHearts, int absorptionFullHearts, boolean absorptionHalfHeart, int totalWidth) {
        this.baseHeartCount = baseHeartCount;
        this.fullHearts = fullHearts;
        this.halfHeart = halfHeart;
        this.emptyHearts = emptyHearts;
        this.absorptionFullHearts = absorptionFullHearts;
        this.absorptionHalfHeart = absorptionHalfHeart;
        this.totalWidth = totalWidth;
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

    public int baseHeartCount() {
        return this.baseHeartCount;
    }

    public int fullHearts() {
        return this.fullHearts;
    }

    public boolean halfHeart() {
        return this.halfHeart;
    }

    public int emptyHearts() {
        return this.emptyHearts;
    }

    public int absorptionFullHearts() {
        return this.absorptionFullHearts;
    }

    public boolean absorptionHalfHeart() {
        return this.absorptionHalfHeart;
    }

    public int totalWidth() {
        return this.totalWidth;
    }
}
