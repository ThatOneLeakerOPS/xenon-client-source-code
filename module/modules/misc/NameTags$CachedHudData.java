/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.modules.misc.NameTags;
import com.xenon.utils.NametagRenderState;
import java.util.List;
import net.minecraft.class_2561;

private static final record NameTags.CachedHudData {
    private final long expiresAtMs;
    private final class_2561 nameLabel;
    private final int nameWidth;
    private final NameTags.HealthRenderData healthData;
    private final List<NametagRenderState.ItemEntry> items;
    private final int itemRowWidth;

    private NameTags.CachedHudData(long expiresAtMs, class_2561 nameLabel, int nameWidth, NameTags.HealthRenderData healthData, List<NametagRenderState.ItemEntry> items, int itemRowWidth) {
        this.expiresAtMs = expiresAtMs;
        this.nameLabel = nameLabel;
        this.nameWidth = nameWidth;
        this.healthData = healthData;
        this.items = items;
        this.itemRowWidth = itemRowWidth;
    }

    private boolean isEmpty() {
        return this.nameLabel == null && this.healthData == null && this.items.isEmpty();
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

    public long expiresAtMs() {
        return this.expiresAtMs;
    }

    public class_2561 nameLabel() {
        return this.nameLabel;
    }

    public int nameWidth() {
        return this.nameWidth;
    }

    public NameTags.HealthRenderData healthData() {
        return this.healthData;
    }

    public List<NametagRenderState.ItemEntry> items() {
        return this.items;
    }

    public int itemRowWidth() {
        return this.itemRowWidth;
    }
}
