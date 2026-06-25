/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.modules.donut.FakeStats;
import java.util.List;
import net.minecraft.class_2561;

private static final record FakeStats.Snapshot {
    private final class_2561 title;
    private final List<FakeStats.SourceLine> lines;
    private final String signature;

    private FakeStats.Snapshot(class_2561 title, List<FakeStats.SourceLine> lines, String signature) {
        this.title = title;
        this.lines = lines;
        this.signature = signature;
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

    public class_2561 title() {
        return this.title;
    }

    public List<FakeStats.SourceLine> lines() {
        return this.lines;
    }

    public String signature() {
        return this.signature;
    }
}
