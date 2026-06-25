/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.modules.misc.SkinChanger;
import net.minecraft.class_12079;

private static final record SkinChanger.ResolvedSkin {
    private final SkinChanger.SkinLookup lookup;
    private final class_12079.class_12081 textureAsset;

    private SkinChanger.ResolvedSkin(SkinChanger.SkinLookup lookup, class_12079.class_12081 textureAsset) {
        this.lookup = lookup;
        this.textureAsset = textureAsset;
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

    public SkinChanger.SkinLookup lookup() {
        return this.lookup;
    }

    public class_12079.class_12081 textureAsset() {
        return this.textureAsset;
    }
}
