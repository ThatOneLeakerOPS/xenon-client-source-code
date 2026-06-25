/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import java.util.List;
import net.minecraft.class_2338;

private static class GrowthFinder.VineCluster {
    final int length;
    final List<class_2338> positions;

    GrowthFinder.VineCluster(List<class_2338> p) {
        this.positions = p;
        this.length = p.size();
    }

    class_2338 centroid() {
        long x = 0L;
        long y = 0L;
        long z = 0L;
        for (class_2338 p : this.positions) {
            x = x + (long)p.method_10263();
            y = y + (long)p.method_10264();
            z = z + (long)p.method_10260();
        }
        return new class_2338((int)(x / (long)this.positions.size()), (int)(y / (long)this.positions.size()), (int)(z / (long)this.positions.size()));
    }
}
