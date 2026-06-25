/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

private static final record SpawnerProtect.WebhookSnapshot {
    private final String webhook;
    private final String title;
    private final String description;
    private final int color;
    private final String playerName;
    private final String threatName;
    private final String distance;
    private final int spawnersInInventory;
    private final boolean allMined;
    private final String serverIp;
    private final String time;
    private final String skinRenderUrl;
    private final String disconnectReason;

    private SpawnerProtect.WebhookSnapshot(String webhook, String title, String description, int color, String playerName, String threatName, String distance, int spawnersInInventory, boolean allMined, String serverIp, String time, String skinRenderUrl, String disconnectReason) {
        this.webhook = webhook;
        this.title = title;
        this.description = description;
        this.color = color;
        this.playerName = playerName;
        this.threatName = threatName;
        this.distance = distance;
        this.spawnersInInventory = spawnersInInventory;
        this.allMined = allMined;
        this.serverIp = serverIp;
        this.time = time;
        this.skinRenderUrl = skinRenderUrl;
        this.disconnectReason = disconnectReason;
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

    public String title() {
        return this.title;
    }

    public String description() {
        return this.description;
    }

    public int color() {
        return this.color;
    }

    public String playerName() {
        return this.playerName;
    }

    public String threatName() {
        return this.threatName;
    }

    public String distance() {
        return this.distance;
    }

    public int spawnersInInventory() {
        return this.spawnersInInventory;
    }

    public boolean allMined() {
        return this.allMined;
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

    public String disconnectReason() {
        return this.disconnectReason;
    }
}
