/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

private static final record FakeStats.AppliedStats {
    private final String money;
    private final String shards;
    private final String kills;
    private final String deaths;
    private final String playtime;

    private FakeStats.AppliedStats(String money, String shards, String kills, String deaths, String playtime) {
        this.money = money;
        this.shards = shards;
        this.kills = kills;
        this.deaths = deaths;
        this.playtime = playtime;
    }

    private String signature() {
        return this.money + "|" + this.shards + "|" + this.kills + "|" + this.deaths + "|" + this.playtime;
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

    public String money() {
        return this.money;
    }

    public String shards() {
        return this.shards;
    }

    public String kills() {
        return this.kills;
    }

    public String deaths() {
        return this.deaths;
    }

    public String playtime() {
        return this.playtime;
    }
}
