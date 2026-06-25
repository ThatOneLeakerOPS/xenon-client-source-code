/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.gui.notification.NotificationManager;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.ModeSetting;
import com.xenon.setting.Setting;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_640;

public final class TabDetector
extends Module {
    private final ModeSetting detectMode;
    private final Setting<String> targetPlayers;
    private final ModeSetting notificationMode;
    private final Setting<Boolean> logOffline;
    private final Set<String> currentTargetsOnline;
    private final Set<String> previousTargetsOnline;

    public TabDetector() {
        super("TabDetector", Category.MISC);
        this.detectMode = new ModeSetting("Detect", "List", "Any", "List");
        this.targetPlayers = new Setting("Target Players", "");
        this.notificationMode = new ModeSetting("Notification Mode", "Both", "Chat", "Toast", "Both");
        this.logOffline = new Setting("Log Offline", true);
        this.currentTargetsOnline = new HashSet();
        this.previousTargetsOnline = new HashSet();
        this.targetPlayers.visibleWhen(this::lambda$new$0);
        this.addSetting(this.detectMode);
        this.addSetting(this.targetPlayers);
        this.addSetting(this.notificationMode);
        this.addSetting(this.logOffline);
    }

    public void onEnable() {
        this.currentTargetsOnline.clear();
        this.previousTargetsOnline.clear();
        this.snapshotOnlineTargetsInto(this.previousTargetsOnline);
    }

    public void onDisable() {
        this.currentTargetsOnline.clear();
        this.previousTargetsOnline.clear();
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.method_1562() == null) {
            return;
        }
        boolean any = this.detectMode.is("Any");
        Set<String> wanted = any ? Set.of() : this.parseTargets((String)this.targetPlayers.getValue());
        this.currentTargetsOnline.clear();
        if (!any && wanted.isEmpty()) {
            this.previousTargetsOnline.clear();
            return;
        }
        this.currentTargetsOnline.clear();
        for (class_640 entry : mc.method_1562().method_2880()) {
            String name = this.safeProfileName(entry);
            while (name.isEmpty()) {
            }
            while (mc.field_1724 != null) {
                if (!name.equalsIgnoreCase(mc.field_1724.method_5477().getString())) break;
            }
            if (!any || this.containsIgnoreCase(wanted, name)) continue;
            this.currentTargetsOnline.add(name);
        }
        joined = new HashSet(this.currentTargetsOnline);
        joined.removeAll(this.previousTargetsOnline);
        if (!joined.isEmpty()) {
            this.handleJoin(joined);
        }
        if (((Boolean)this.logOffline.getValue()).booleanValue()) {
            Set<String> left = new HashSet(this.previousTargetsOnline);
            left.removeAll(this.currentTargetsOnline);
            if (!left.isEmpty()) {
                this.handleLeave(left);
            }
        }
        this.previousTargetsOnline.clear();
        this.previousTargetsOnline.addAll(this.currentTargetsOnline);
    }

    private void handleJoin(Set<String> players) {
        String list = String.join(", ", players);
        String msg = players.size() == 1 ? "Target player joined: " + list : "Target players joined: " + list;
        this.notify(msg, players.size() == 1 ? "Target Player Joined!" : "Target Players Joined!", -1938838);
    }

    private void handleLeave(Set<String> players) {
        String list = String.join(", ", players);
        String msg = players.size() == 1 ? "Target player left: " + list : "Target players left: " + list;
        this.notify(msg, players.size() == 1 ? "Target Player Left!" : "Target Players Left!", -11152222);
    }

    private void notify(String chatMessage, String toastTitle, int accent) {
        String mode = this.notificationMode.getValue();
        boolean toChat = "Chat".equalsIgnoreCase(mode) || "Both".equalsIgnoreCase(mode);
        boolean toToast = "Toast".equalsIgnoreCase(mode) || "Both".equalsIgnoreCase(mode);
        try {
            mc.field_1705.method_1743().method_1812(class_2561.method_43470("[TabDetector] " + chatMessage));
        }
        catch (Throwable e7) {
            if (toToast) {
                NotificationManager.INSTANCE.push("TabDetector", toastTitle, class_1799.field_8037, accent);
            }
            return;
        }
        if (toToast) {
            NotificationManager.INSTANCE.push("TabDetector", toastTitle, class_1799.field_8037, accent);
        }
    }

    private void snapshotOnlineTargetsInto(Set<String> out) {
        out.clear();
        if (mc.method_1562() == null) {
            return;
        }
        boolean any = this.detectMode.is("Any");
        Set<String> wanted = any ? Set.of() : this.parseTargets((String)this.targetPlayers.getValue());
        if (!any && wanted.isEmpty()) {
            return;
        }
        for (class_640 entry : mc.method_1562().method_2880()) {
            String name = this.safeProfileName(entry);
            while (name.isEmpty()) {
            }
            while (mc.field_1724 != null) {
                if (!name.equalsIgnoreCase(mc.field_1724.method_5477().getString())) break;
            }
            if (!any || this.containsIgnoreCase(wanted, name)) continue;
            out.add(name);
        }
    }

    private String safeProfileName(class_640 entry) {
        try {
            if (entry == null || entry.method_2966() == null) {
                return "";
            }
        }
        catch (Throwable ignored) {
            return "";
        }
        try {
            n = entry.method_2966().name();
            return n == null ? "" : n;
        }
        catch (Throwable ignored) {
            return "";
        }
        catch (Throwable ignored) {
            return "";
        }
        try {
            ignored = null;
            return "";
        }
        catch (Throwable ignored) {
            return "";
        }
    }

    private boolean containsIgnoreCase(Set<String> loweredSet, String value) {
        return loweredSet.contains(value.toLowerCase(Locale.ROOT));
    }

    private Set<String> parseTargets(String raw) {
        if (raw == null || raw.isBlank()) {
            return Set.of();
        }
        String normalized = raw.replace('\n', ',').replace('\r', ',');
        LinkedHashSet<String> out = new LinkedHashSet();
        for (String part : normalized.split(",")) {
            String t = part == null ? "" : part.trim();
            if ((t.isEmpty())) continue;
            out.add(t.toLowerCase(Locale.ROOT));
        }
        return out;
    }
}
