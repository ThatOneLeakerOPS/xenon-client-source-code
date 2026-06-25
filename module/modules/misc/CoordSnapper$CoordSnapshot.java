/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

private static final record CoordSnapper.CoordSnapshot {
    private final String webhook;
    private final String playerName;
    private final int x;
    private final int y;
    private final int z;
    private final String serverIp;
    private final String time;
    private final String skinRenderUrl;

    private CoordSnapper.CoordSnapshot(String webhook, String playerName, int x, int y, int z, String serverIp, String time, String skinRenderUrl) {
        this.webhook = webhook;
        this.playerName = playerName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.serverIp = serverIp;
        this.time = time;
        this.skinRenderUrl = skinRenderUrl;
    }

    private String coordsField() {
        return "X: " + this.x + " Y: " + this.y + " Z: " + this.z;
    }

    private String coordsInline() {
        return this.x + ", " + this.y + ", " + this.z;
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

    public String webhook() {
        return this.webhook;
    }

    public String playerName() {
        return this.playerName;
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int z() {
        return this.z;
    }

    public String serverIp() {
        return this.serverIp;
    }

    public String time() {
        return this.time;
    }

    public String skinRenderUrl() {
        return this.skinRenderUrl;
    }
}
