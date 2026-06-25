/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

private static final class LightDebug.ChunkRenderData {
    private final int minX;
    private final int minZ;
    private final int minY;
    private final int[] blockData;
    private final int blockCount;
    private final short[] faceVertices;
    private final int[] faceColors;
    private final int faceCount;

    private LightDebug.ChunkRenderData(int minX, int minZ, int minY, int[] blockData, int blockCount, short[] faceVertices, int[] faceColors, int faceCount) {
        this.minX = minX;
        this.minZ = minZ;
        this.minY = minY;
        this.blockData = blockData;
        this.blockCount = blockCount;
        this.faceVertices = faceVertices;
        this.faceColors = faceColors;
        this.faceCount = faceCount;
    }
}
