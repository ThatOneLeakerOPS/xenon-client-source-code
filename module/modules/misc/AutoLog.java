/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.Friends;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_1657;
import net.minecraft.class_239;
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_3966;
import net.minecraft.class_8646;
import net.minecraft.class_9011;

public final class AutoLog
extends Module {
    private static final long COMBAT_COOLDOWN_MS = 20000L;
    private static final int COMBAT_COOLDOWN_TICKS = 400;
    private static final double COMBAT_GRACE_RANGE = 8;
    private static final long SERVER_COMBAT_GRACE_MS = 1500L;
    private static final int MAX_TEXT_SCAN_DEPTH = 4;
    private long lastHitTime;
    private long lastAttackTime;
    private long lastServerCombatTagTime;
    private float lastCombinedHealth = -1f;
    private int lastObservedAttackedTick = -1;
    private int lastObservedAttackTick = -1;

    public AutoLog() {
        super("AutoLog", Category.MISC);
    }

    public void onEnable() {
        this.lastHitTime = 0L;
        this.lastAttackTime = 0L;
        this.lastServerCombatTagTime = 0L;
        this.lastCombinedHealth = this.getCurrentCombinedHealth();
        this.lastObservedAttackedTick = this.getCurrentAttackedTick();
        this.lastObservedAttackTick = this.getCurrentAttackTick();
        if (mc.field_1724 != null) {
            if (mc.field_1687 != null) {
                if (this.isCombatContextPresent() || this.isRecentPlayerCombat() || this.isServerCombatTagged()) {
                    this.startCombatCooldown(System.currentTimeMillis());
                }
            }
        }
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        float currentCombinedHealth = this.getCurrentCombinedHealth();
        boolean tookDamage = this.lastCombinedHealth >= 0f && currentCombinedHealth + 0.001f < this.lastCombinedHealth;
        this.lastCombinedHealth = currentCombinedHealth;
        if (mc.field_1724.field_6235 > 0 || tookDamage || this.hasNewPlayerHit()) {
            this.lastHitTime = now;
        }
        boolean serverCombatTagged = this.isServerCombatTagged();
        if (serverCombatTagged) {
            this.lastServerCombatTagTime = now;
        }
        if (mc.field_1690.field_1886.method_1434()) {
            if (mc.field_1765 != null) {
                if (mc.field_1765.method_17783() == class_239.class_240.field_1331) {
                    var var8 = mc.field_1765;
                    if (var8 instanceof class_3966) {
                        class_3966 entityHitResult = var8;
                        var8 = entityHitResult.method_17782();
                        if (var8 instanceof class_1657) {
                            class_1657 target = var8;
                            if (target != mc.field_1724) {
                                this.lastAttackTime = now;
                            }
                        }
                    }
                }
            }
        }
        if (this.hasNewPlayerAttack()) {
            this.lastAttackTime = now;
        }
        if (this.isCombatCooldownActive(now, serverCombatTagged)) {
            return;
        }
        Iterator entityHitResult = mc.field_1687.method_18456().iterator();
        if (!entityHitResult.hasNext()) { /* goto L370; */ }
        class_1657 player = entityHitResult.next();
        if (player != mc.field_1724) {
            while (player.method_7325()) {
            }
        }
        while (Friends.isAutoLog()) {
            if (!Friends.isFriend(player.method_5477().getString())) break;
        }
        mc.method_1562().method_48296().method_10747(class_2561.method_43470("[AutoLog] Player detected: " + player.method_5477().getString()));
        if (mc.method_1562() != null && mc.method_1562().method_48296() != null) {
            this.toggle();
        }
        return;
    }

    private boolean isCombatCooldownActive(long now, boolean serverCombatTagged) {
        if (serverCombatTagged || this.isRecentPlayerCombat() || now - this.lastServerCombatTagTime < 1500L) {
            return true;
        }
        if (this.lastHitTime <= 0L && this.lastAttackTime <= 0L) {
            return false;
        }
        long lastCombatTime = Math.max(this.lastHitTime, this.lastAttackTime);
        return now - lastCombatTime < 20000L;
    }

    private boolean isCombatContextPresent() {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return false;
        }
        if (mc.field_1724.field_6235 > 0 || this.isRecentPlayerCombat()) {
            return true;
        }
        if (mc.field_1765 != null) {
            if (mc.field_1765.method_17783() == class_239.class_240.field_1331) {
                var var3 = mc.field_1765;
                if (var3 instanceof class_3966) {
                    class_3966 entityHitResult = var3;
                    var3 = entityHitResult.method_17782();
                    if (var3 instanceof class_1657) {
                        class_1657 target = var3;
                        if (target != mc.field_1724) {
                            if (!target.method_7325()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        double maxDistanceSq = 64;
        for (class_1657 player : mc.field_1687.method_18456()) {
            if (player == mc.field_1724) continue;
            while (player.method_7325()) {
            }
            if (mc.field_1724.method_5858(player) > maxDistanceSq) continue;
            return true;
        }
        return false;
    }

    private void startCombatCooldown(long now) {
        this.lastHitTime = now;
        this.lastAttackTime = now;
        this.lastServerCombatTagTime = now;
    }

    private boolean isRecentPlayerCombat() {
        return this.isRecentPlayerHit() || this.isRecentPlayerAttack();
    }

    private boolean hasNewPlayerHit() {
        int currentAttackedTick = this.getCurrentAttackedTick();
        if (currentAttackedTick <= 0 || currentAttackedTick == this.lastObservedAttackedTick) {
            return false;
        }
        this.lastObservedAttackedTick = currentAttackedTick;
        return this.isRecentPlayerHit();
    }

    private boolean hasNewPlayerAttack() {
        int currentAttackTick = this.getCurrentAttackTick();
        if (currentAttackTick <= 0 || currentAttackTick == this.lastObservedAttackTick) {
            return false;
        }
        this.lastObservedAttackTick = currentAttackTick;
        return this.isRecentPlayerAttack();
    }

    private boolean isRecentPlayerHit() {
        if (mc.field_1724 == null) {
            return false;
        }
        class_1309 var2 = mc.field_1724.method_49107();
        class_1657 attacker = var2;
        if (!(var2 instanceof class_1657)|| attacker == mc.field_1724 || attacker.method_7325()) {
            return false;
        }
        return this.isRecentCombatTick(mc.field_1724.method_6117());
    }

    private boolean isRecentPlayerAttack() {
        if (mc.field_1724 == null) {
            return false;
        }
        class_1309 var2 = mc.field_1724.method_6052();
        class_1657 target = var2;
        if (!(var2 instanceof class_1657)|| target == mc.field_1724 || target.method_7325()) {
            return false;
        }
        return this.isRecentCombatTick(mc.field_1724.method_6083());
    }

    private boolean isRecentCombatTick(int tickTimestamp) {
        if (mc.field_1724 == null || tickTimestamp <= 0) {
            return false;
        }
        int elapsedTicks = mc.field_1724.field_6012 - tickTimestamp;
        return elapsedTicks >= 0 && elapsedTicks < 400;
    }

    private int getCurrentAttackedTick() {
        return mc.field_1724 != null ? mc.field_1724.method_6117() : -1;
    }

    private int getCurrentAttackTick() {
        return mc.field_1724 != null ? mc.field_1724.method_6083() : -1;
    }

    private boolean isServerCombatTagged() {
        if (mc.field_1687 != null && this.containsCombatScoreboardText(mc.field_1687.method_8428())) {
            return true;
        }
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap());
        return mc.field_1705 != null && this.containsCombatText(mc.field_1705, 0, visited);
    }

    private boolean containsCombatScoreboardText(class_269 scoreboard) {
        if (scoreboard == null) {
            return false;
        }
        for (class_266 objective : scoreboard.method_1151()) {
            if (!this.containsCombatKeyword(objective.method_1113()) || this.containsCombatText(objective.method_1114())) continue;
            return true;
        }
        var2 = class_8646.values();
        class_268 team = var2.length;
        for (int var4 = 0; var4 < team; var4++) {
            class_8646 slot = var2[var4];
            class_266 objective = scoreboard.method_1189(slot);
            if (objective != null) {
                if (!this.containsCombatKeyword(objective.method_1113()) || this.containsCombatText(objective.method_1114())) continue;
                return true;
                for (class_9011 entry : scoreboard.method_1184(objective)) {
                    if (this.containsCombatKeyword(entry.comp_2127()) || this.containsCombatText(entry.method_55387()) || this.containsCombatText(entry.comp_2129())) return true;
                    class_268 team = scoreboard.method_1164(entry.comp_2127());
                    if (!this.containsCombatTeamText(team)) continue;
                    return true;
                }
            }
        }
        for (team : scoreboard.method_1159()) {
            if (!this.containsCombatTeamText(team)) continue;
            return true;
        }
        return false;
    }

    private boolean containsCombatTeamText(class_268 team) {
        if (!this.containsCombatKeyword(team.method_1197())) {
            if (!this.containsCombatText(team.method_1140())) {
                if (this.containsCombatText(team.method_1144())) { /* goto @48; */ }
            }
        }
        return team != null;
    }

    private boolean containsCombatText(Object value, int depth, Set<Object> visited) {
        if (value == null || depth > 4) {
            return false;
        }
        if (value instanceof class_2561) {
            class_2561 text = value;
            return this.containsCombatKeyword(text.getString());
        }
        if (value instanceof String) {
            String string = value;
            return this.containsCombatKeyword(string);
        }
        if (!visited.add(value)) {
            return false;
        }
        if (value instanceof Map) {
            Map<?, ?> map = value;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!this.containsCombatText(entry.getKey(), depth + 1, visited) || this.containsCombatText(entry.getValue(), depth + 1, visited)) continue;
                return true;
            }
            return false;
        }
        if (value instanceof Collection) {
            Collection<?> collection = value;
            for (Object element : collection) {
                if (!this.containsCombatText(element, depth + 1, visited)) continue;
                return true;
            }
            return false;
        }
        Class<?> clazz = value.getClass();
        if (!this.isInspectableClass(clazz)) {
            return false;
        }
        current = clazz;
        while (current != null) {
            if (current == Object.class) { /* goto @359; */ }
            Field[] element = current.getDeclaredFields();
            var var7 = element.length;
            int var8 = 0;
            if (var8 >= var7) { /* goto @349; */ }
            Field field = element[var8];
            if (!(Modifier.isStatic(field.getModifiers()))) {
                if (!(field.getType().isPrimitive())) {
                    field.setAccessible(true);
                }
            }
            try {
                Object fieldValue = field.get(value);
                if (this.containsCombatText(fieldValue, depth + 1, visited)) {
                    return true;
                }
            }
            catch (IllegalAccessException fieldValue) {
                var8++;
                /* goto @270; */
                current = current.getSuperclass();
            }
        }
        return false;
    }

    private boolean isInspectableClass(Class<?> clazz) {
        String name = clazz.getName();
        return name.startsWith("net.minecraft.scoreboard.") || name.startsWith("net.minecraft.text.") || name.startsWith("net.minecraft.client.gui.hud.") || name.startsWith("net.minecraft.client.network.") || name.startsWith("java.util.");
    }

    private boolean containsCombatKeyword(String value) {
        if (value == null) {
            return false;
        }
        String lower = value.toLowerCase();
        return lower.contains("combat");
    }

    private boolean containsCombatText(class_2561 text) {
        return text != null && this.containsCombatKeyword(text.getString());
    }

    private float getCurrentCombinedHealth() {
        if (mc.field_1724 == null) {
            return -1f;
        }
        return mc.field_1724.method_6032() + mc.field_1724.method_6067();
    }
}
