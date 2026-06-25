/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import java.awt.Color;

private static final class PlayerESP.RenderData {
    final double dx;
    final double dy;
    final double dz;
    final double tracerTargetY;
    final double halfWidth;
    final double height;
    final Color outline;
    final Color fill;
    final Color tracer;
    final boolean boxVisible;

    private PlayerESP.RenderData(double dx, double dy, double dz, double tracerTargetY, double halfWidth, double height, Color outline, Color fill, Color tracer, boolean boxVisible) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.tracerTargetY = tracerTargetY;
        this.halfWidth = halfWidth;
        this.height = height;
        this.outline = outline;
        this.fill = fill;
        this.tracer = tracer;
        this.boxVisible = boxVisible;
    }
}
