/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import net.minecraft.class_1657;

private static final record SpawnerProtect.EnemyStatus {
    private final boolean hasAnyEnemy;
    private final boolean hasCriticalThreat;
    private final class_1657 threat;
    private final double distance;

    private SpawnerProtect.EnemyStatus(boolean hasAnyEnemy, boolean hasCriticalThreat, class_1657 threat, double distance) {
        this.hasAnyEnemy = hasAnyEnemy;
        this.hasCriticalThreat = hasCriticalThreat;
        this.threat = threat;
        this.distance = distance;
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

    public boolean hasAnyEnemy() {
        return this.hasAnyEnemy;
    }

    public boolean hasCriticalThreat() {
        return this.hasCriticalThreat;
    }

    public class_1657 threat() {
        return this.threat;
    }

    public double distance() {
        return this.distance;
    }
}
