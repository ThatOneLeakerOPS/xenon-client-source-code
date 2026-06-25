/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import java.util.Objects;
import net.minecraft.class_238;

private static final class HoleESP.HoleData {
    private final class_238 box;
    private final int depth;
    private final boolean is1x1;
    private final long createdAt;

    private HoleESP.HoleData(class_238 box, int depth, boolean is1x1) {
        this.box = box;
        this.depth = depth;
        this.is1x1 = is1x1;
        this.createdAt = System.currentTimeMillis();
    }

    private boolean isReadyToRender() {
        return true;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HoleESP.HoleData) {
            HoleData holeData = obj;
        } else {
            return false;
        }
        return Objects.equals(this.box, holeData.box);
    }

    public int hashCode() {
        Object[] tmp0 = new Object[1];
        tmp0[0] = this.box;
        return Objects.hash(tmp0);
    }
}
