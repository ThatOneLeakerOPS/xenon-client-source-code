/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_274;
import net.minecraft.class_5250;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import net.minecraft.class_9014;
import net.minecraft.class_9015;
import net.minecraft.class_9020;

public final class FakeStats
extends Module {
    private static final String OBJECTIVE_NAME = "xenon_fake_stats";
    private static final int MAX_SIDEBAR_LINES = 15;
    private final Setting<String> money = new Setting("Money", "0");
    private final Setting<String> shards = new Setting("Shards", "0");
    private final Setting<String> kills = new Setting("Kills", "0");
    private final Setting<String> deaths = new Setting("Deaths", "0");
    private final Setting<String> playtime = new Setting("Playtime", "0m");
    private final Random randomSource = new Random();
    private class_266 originalObjective;
    private String originalObjectiveName;
    private class_266 customObjective;
    private AppliedStats appliedStats;
    private Object lastWorld;
    private String lastSnapshotSignature = "";
    private boolean needsRefresh;

    public FakeStats() {
        super("FakeStats", Category.DONUT);
        this.addSetting(this.money);
        this.addSetting(this.shards);
        this.addSetting(this.kills);
        this.addSetting(this.deaths);
        this.addSetting(this.playtime);
        this.randomizeSettings();
        this.applyCurrentValues();
    }

    public void onEnable() {
        this.lastWorld = mc.field_1687;
        this.originalObjective = null;
        this.originalObjectiveName = null;
        this.customObjective = null;
        this.lastSnapshotSignature = "";
        this.randomizeSettings();
        this.applyCurrentValues();
        this.needsRefresh = true;
    }

    public void onDisable() {
        this.restoreOriginalScoreboard();
        this.lastWorld = null;
        this.originalObjective = null;
        this.originalObjectiveName = null;
        this.customObjective = null;
        this.lastSnapshotSignature = "";
        this.needsRefresh = false;
    }

    public void onTick() {
        if (mc.field_1687 == null) {
            this.originalObjective = null;
            this.originalObjectiveName = null;
            this.customObjective = null;
            this.lastWorld = null;
            this.lastSnapshotSignature = "";
            return;
        }
        if (mc.field_1687 != this.lastWorld) {
            this.originalObjective = null;
            this.originalObjectiveName = null;
            this.customObjective = null;
            this.lastSnapshotSignature = "";
            this.lastWorld = mc.field_1687;
            this.needsRefresh = true;
        }
        this.captureOriginalObjective();
        if (this.originalObjective == null) {
            return;
        }
        class_269 scoreboard = mc.field_1687.method_8428();
        if (!scoreboard.method_1151().contains(this.originalObjective)) {
            this.originalObjective = null;
            this.captureOriginalObjective();
            if (this.originalObjective == null) {
                return;
            }
        }
        Snapshot snapshot = this.createSnapshot(scoreboard, this.originalObjective);
        if (snapshot == null) {
            return;
        }
        this.syncAppliedStatsWithSettings();
        if (!snapshot.signature().equals(this.lastSnapshotSignature) || this.customObjective == null) {
            this.needsRefresh = true;
        }
        this.rebuildIfPossible(scoreboard, snapshot);
        if (this.customObjective != null) {
            if (scoreboard.method_1189(class_8646.field_45157) != this.customObjective) {
                scoreboard.method_1158(class_8646.field_45157, this.customObjective);
            }
        }
    }

    private void randomizeSettings() {
        this.money.setValue(this.formatCompactNumber(this.randomBetweenLong(10000L, 5000000000L)));
        this.shards.setValue(this.formatCompactNumber(this.randomBetweenLong(0L, 2500000L)));
        this.kills.setValue(String.valueOf(this.randomBetweenLong(0L, 2000L)));
        this.deaths.setValue(String.valueOf(this.randomBetweenLong(0L, 1000L)));
        this.playtime.setValue(this.formatCompactPlaytime(this.randomBetweenLong(0L, 15552000L)));
    }

    private void applyCurrentValues() {
        this.appliedStats = new AppliedStats(this.sanitize((String)this.money.getValue(), "0"), this.sanitize((String)this.shards.getValue(), "0"), this.sanitize((String)this.kills.getValue(), "0"), this.sanitize((String)this.deaths.getValue(), "0"), this.sanitize((String)this.playtime.getValue(), "0m"));
        this.needsRefresh = true;
    }

    private void syncAppliedStatsWithSettings() {
        AppliedStats current = new AppliedStats(this.sanitize((String)this.money.getValue(), "0"), this.sanitize((String)this.shards.getValue(), "0"), this.sanitize((String)this.kills.getValue(), "0"), this.sanitize((String)this.deaths.getValue(), "0"), this.sanitize((String)this.playtime.getValue(), "0m"));
        if (this.appliedStats == null || !this.appliedStats.signature().equals(current.signature())) {
            this.appliedStats = current;
            this.needsRefresh = true;
        }
    }

    private void captureOriginalObjective() {
        if (mc.field_1687 == null) {
            return;
        }
        class_269 scoreboard = mc.field_1687.method_8428();
        class_266 current = scoreboard.method_1189(class_8646.field_45157);
        this.originalObjective = current;
        if (current != null && !"xenon_fake_stats".equals(current.method_1113())) {
            this.originalObjectiveName = current.method_1113();
            return;
        }
        if (this.originalObjective != null && scoreboard.method_1151().contains(this.originalObjective)) {
            return;
        }
        if (this.originalObjectiveName != null) {
            class_266 namedObjective = scoreboard.method_1170(this.originalObjectiveName);
            if (namedObjective != null) {
                if (!"xenon_fake_stats".equals(namedObjective.method_1113())) {
                    this.originalObjective = namedObjective;
                    return;
                }
            }
        }
        for (class_266 objective : scoreboard.method_1151()) {
            if (!"xenon_fake_stats".equals(objective.method_1113())) {
                this.originalObjective = objective;
                this.originalObjectiveName = objective.method_1113();
                return;
            }
        }
    }

    private Snapshot createSnapshot(class_269 scoreboard, class_266 objective) {
        List<SourceLine> lines = new ArrayList();
        List<class_9011> entries = new ArrayList(scoreboard.method_1184(objective));
        entries.removeIf(class_9011::method_55385);
        entries.sort(Comparator.comparingInt(class_9011::comp_2128).reversed());
        if (entries.size() > 15) {
            entries = new ArrayList(entries.subList(0, 15));
        }
        for (class_9011 entry : entries) {
            lines.add(new SourceLine(entry.comp_2128(), this.getVisibleLine(scoreboard, entry)));
        }
        title = objective.method_1114() != null ? objective.method_1114().method_27661() : class_2561.method_43470("Donut SMP");
        StringBuilder signature = new StringBuilder(title.getString());
        for (SourceLine line : lines) {
            signature.append('\n').append(line.score()).append(':').append(line.text().getString());
        }
        signature.append('\n').append(this.appliedStats != null ? this.appliedStats.signature() : "");
        return new Snapshot(title, lines, signature.toString());
    }

    private class_2561 getVisibleLine(class_269 scoreboard, class_9011 entry) {
        if (entry.comp_2129() != null) {
            return entry.comp_2129().method_27661();
        }
        class_2561 baseText = entry.method_55387() != null ? entry.method_55387().method_27661() : class_2561.method_43470(entry.comp_2127());
        class_268 team = scoreboard.method_1164(entry.comp_2127());
        return class_268.method_1142(team, baseText).method_27661();
    }

    private void rebuildIfPossible(class_269 scoreboard, Snapshot snapshot) {
        if (!this.needsRefresh || this.appliedStats == null) {
            return;
        }
        class_266 existing = scoreboard.method_1170("xenon_fake_stats");
        if (existing != null) {
            scoreboard.method_1194(existing);
        }
        this.customObjective = scoreboard.method_1168("xenon_fake_stats", class_274.field_1468, snapshot.title().method_27661(), class_274.class_275.field_1472, true, class_9020.field_47557);
        scoreboard.method_1158(class_8646.field_45157, this.customObjective);
        List<SourceLine> lines = snapshot.lines();
        for (int i = 0; i < lines.size(); i++) {
            SourceLine line = lines.get(i);
            class_9015 holder = class_9015.method_55422("fake_stats_line_" + i);
            class_9014 score = scoreboard.method_1180(holder, this.customObjective);
            score.method_55410(lines.size() - i);
            score.method_55411(this.replaceTrackedStat(line.text()));
            score.method_55412(class_9020.field_47557);
        }
        this.lastSnapshotSignature = snapshot.signature();
        this.needsRefresh = false;
    }

    private class_2561 replaceTrackedStat(class_2561 originalText) {
        class_2561 replaced = this.tryReplaceValue(originalText, "money", this.appliedStats.money());
        if (replaced != null) {
            return replaced;
        }
        replaced = this.tryReplaceValue(originalText, "shards", this.appliedStats.shards());
        if (replaced != null) {
            return replaced;
        }
        replaced = this.tryReplaceValue(originalText, "kills", this.appliedStats.kills());
        if (replaced != null) {
            return replaced;
        }
        replaced = this.tryReplaceValue(originalText, "deaths", this.appliedStats.deaths());
        if (replaced != null) {
            return replaced;
        }
        replaced = this.tryReplaceValue(originalText, "playtime", this.appliedStats.playtime());
        if (replaced != null) {
            return replaced;
        }
        return originalText.method_27661();
    }

    private class_2561 tryReplaceValue(class_2561 originalText, String label, String newValue) {
        String rendered = originalText.getString();
        int valueStart = this.findValueStart(rendered, label);
        if (valueStart < 0) {
            return null;
        }
        List<TextSegment> segments = this.collectSegments(originalText);
        class_5250 result = class_2561.method_43473();
        this.appendTextRange(result, segments, valueStart);
        result.method_10852(class_2561.method_43470(newValue).method_10862(this.findStyleAt(segments, valueStart)));
        return result;
    }

    private int findValueStart(String rendered, String label) {
        String lower = rendered.toLowerCase(Locale.ROOT);
        int labelIndex = lower.indexOf(label);
        if (labelIndex < 0) {
            return -1;
        }
        for (int index = labelIndex + label.length(); index < rendered.length(); index++) {
            if (!Character.isWhitespace(rendered.charAt(index))) break;
        }
        return index < rendered.length() ? index : -1;
    }

    private List<TextSegment> collectSegments(class_2561 text) {
        List<TextSegment> segments = new ArrayList();
        text.method_27658(FakeStats::lambda$collectSegments$0 /* captured: segments */, class_2583.field_24360);
        return segments;
    }

    private void appendTextRange(class_5250 result, List<TextSegment> segments, int endExclusive) {
        int remaining = Math.max(0, endExclusive);
        for (TextSegment segment : segments) {
            if (remaining > 0) continue;
            return;
            String value = segment.value();
            int length = Math.min(value.length(), remaining);
            result.method_10852(class_2561.method_43470(value.substring(0, length)).method_10862(segment.style()));
            remaining = remaining - length;
        }
    }

    private class_2583 findStyleAt(List<TextSegment> segments, int charIndex) {
        int index = Math.max(0, charIndex);
        class_2583 fallback = class_2583.field_24360;
        for (TextSegment segment : segments) {
            if (segment.value().isEmpty()) continue;
            fallback = segment.style();
            if (index < segment.value().length()) return segment.style();
            index = index - segment.value().length();
        }
        return fallback;
    }

    private void restoreOriginalScoreboard() {
        if (mc.field_1687 == null) {
            return;
        }
        class_269 scoreboard = mc.field_1687.method_8428();
        class_266 existing = scoreboard.method_1170("xenon_fake_stats");
        if (existing != null) {
            scoreboard.method_1194(existing);
        }
        if (this.originalObjective != null) {
            if (scoreboard.method_1151().contains(this.originalObjective)) {
                scoreboard.method_1158(class_8646.field_45157, this.originalObjective);
            }
        }
    }

    private long randomBetweenLong(long min, long max) {
        if (min >= max) {
            return min;
        }
        return min + (long)Math.floor(this.randomSource.nextDouble() * (double)(max - min + 1L));
    }

    private String formatCompactNumber(long value) {
        long absolute = Math.abs(value);
        if (absolute < 1000L) {
            return Long.toString(value);
        }
        if (absolute < 1000000L) {
            return this.formatCompactValue((double)value / 1000, "K");
        }
        if (absolute < 1000000000L) {
            return this.formatCompactValue((double)value / 1000000, "M");
        }
        return this.formatCompactValue((double)value / 1000000000, "B");
    }

    private String formatCompactValue(double value, String suffix) {
        String pattern = value >= 100 ? "%.0f%s" : value >= 10 ? "%.1f%s" : "%.2f%s";
        Object[] tmp0 = new Object[2];
        tmp0[0] = value;
        tmp0[1] = suffix;
        return String.format(Locale.US, pattern, tmp0);
    }

    private String formatCompactPlaytime(long totalSeconds) {
        long totalHours = totalSeconds / 3600L;
        long totalDays = totalHours / 24L;
        long hours = totalHours % 24L;
        long minutes = totalSeconds % 3600L / 60L;
        if (totalDays > 0L) {
            Object[] tmp0 = new Object[2];
            tmp0[0] = totalDays;
            tmp0[1] = hours;
            return String.format(Locale.US, "%dd %dh", tmp0);
        }
        if (totalHours > 0L) {
            Object[] tmp1 = new Object[2];
            tmp1[0] = totalHours;
            tmp1[1] = minutes;
            return String.format(Locale.US, "%dh %dm", tmp1);
        }
        Object[] tmp2 = new Object[1];
        tmp2[0] = minutes;
        return String.format(Locale.US, "%dm", tmp2);
    }

    private String sanitize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }
}
