/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import java.util.UUID;
import net.minecraft.class_7920;

private static final record SkinChanger.SkinLookup {
    private final String playerName;
    private final UUID uuid;
    private final String textureUrl;
    private final class_7920 skinType;

    private SkinChanger.SkinLookup(String playerName, UUID uuid, String textureUrl, class_7920 skinType) {
        this.playerName = playerName;
        this.uuid = uuid;
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

    public String playerName() {
        return this.playerName;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String textureUrl() {
        return this.textureUrl;
    }

    public class_7920 skinType() {
        return this.skinType;
    }
}
