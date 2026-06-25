/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

private static final class HoleESP.TrackedChunk {
    private final int x;
    private final int z;
    private boolean marked;

    private HoleESP.TrackedChunk(int x, int z) {
        this.x = x;
        this.z = z;
        this.marked = true;
    }
}
