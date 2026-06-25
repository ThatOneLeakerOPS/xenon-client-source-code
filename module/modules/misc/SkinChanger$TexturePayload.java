/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import net.minecraft.class_7920;

private static final record SkinChanger.TexturePayload {
    private final String textureUrl;
    private final class_7920 skinType;

    private SkinChanger.TexturePayload(String textureUrl, class_7920 skinType) {
        this.textureUrl = textureUrl;
        this.skinType = skinType;
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

    public String textureUrl() {
        return this.textureUrl;
    }

    public class_7920 skinType() {
        return this.skinType;
    }
}
