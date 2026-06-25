/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import java.util.List;
import net.minecraft.class_10799;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_5481;

private static final class ChunkFinder.ChunkFinderToast
implements class_368 {
    private static final class_2960 TEXTURE = class_2960.method_60656("toast/advancement");
    private final class_2561 title;
    private final class_2561 description;
    private final class_1799 icon;
    private class_368.class_369 visibility;

    private ChunkFinder.ChunkFinderToast(class_2561 title, class_2561 description, class_1799 icon) {
        this.visibility = class_368.class_369.field_2209;
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public class_368.class_369 method_61988() {
        return this.visibility;
    }

    public void method_61989(class_374 manager, long time) {
        this.visibility = (double)time >= 5000 * manager.method_48221() ? class_368.class_369.field_2209 : class_368.class_369.field_2210;
    }

    public void method_1986(class_332 context, class_327 textRenderer, long startTime) {
        context.method_52706(class_10799.field_56883, TEXTURE, 0, 0, this.method_29049(), this.method_29050());
        context.method_51439(textRenderer, this.title, 30, 7, -256, false);
        List<class_5481> lines = textRenderer.method_1728(this.description, 125);
        if (!lines.isEmpty()) {
            context.method_51430(textRenderer, (class_5481)lines.getFirst(), 30, 18, -1, false);
        }
        context.method_51445(this.icon, 8, 8);
    }
}
