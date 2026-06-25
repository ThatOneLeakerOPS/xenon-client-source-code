/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xenon.XenonClient;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.Friends;
import com.xenon.setting.Setting;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1268;
import net.minecraft.class_156;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3965;
import net.minecraft.class_642;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_7924;

public final class SpawnerProtect
extends Module {
    private static final String SILK_TOUCH_REQUIRED = "Need a Silk Touch pickaxe in hotbar";
    private static final int SCAN_RADIUS = 32;
    private static final double MAX_BREAK_REACH = 5;
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(8L);
    private static final int SUCCESS_COLOR = 5624994;
    private static final int ERROR_COLOR = 14838378;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT);
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(HTTP_TIMEOUT).followRedirects(HttpClient.Redirect.NORMAL).build();
    private final Setting<Integer> criticalDistance = new Setting("Critical Distance", 5, 1, 20);
    private final Setting<String> webhookUrl = new SpawnerProtect.1(this, "Webhook", "");
    private class_2338 currentTarget;
    private boolean disconnectScheduled;
    private int minedSpawnerCount;

    public SpawnerProtect() {
        super("SpawnerProtect", Category.DONUT);
        this.addSetting(this.criticalDistance);
        this.addSetting(this.webhookUrl);
    }

    public void onEnable() {
        this.resetRuntimeState();
        if (!this.hasSilkTouchPickaxeInHotbar()) {
            this.disconnectAndDisable("Need a Silk Touch pickaxe in hotbar");
        }
    }

    public void onDisable() {
        this.stopMining();
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            return;
        }
        if (this.disconnectScheduled) {
            return;
        }
        if (!this.hasSilkTouchPickaxeInHotbar()) {
            this.disconnectAndDisable("Need a Silk Touch pickaxe in hotbar");
            return;
        }
        EnemyStatus enemyStatus = this.getEnemyStatus();
        if (enemyStatus.hasCriticalThreat()) {
            this.fireWebhookThenDisconnect(this.captureCriticalSnapshot(enemyStatus.threat(), enemyStatus.distance()));
            return;
        }
        if (!enemyStatus.hasAnyEnemy()) {
            this.stopMining();
            return;
        }
        int silkSlot = this.findSilkTouchPickaxeHotbarSlot();
        if (silkSlot == -1) {
            this.disconnectAndDisable("Need a Silk Touch pickaxe in hotbar");
            return;
        }
        this.selectHotbarSlot(silkSlot);
        this.applySneak();
        if (this.currentTarget == null || !this.isSpawner(this.currentTarget) || !mc.field_1724.method_56093(this.currentTarget, 5)) {
            this.currentTarget = this.findClosestSpawner();
            if (this.currentTarget == null) {
                this.fireWebhookThenDisconnect(this.captureSuccessSnapshot());
                return;
            }
        }
        class_2350 side = this.getClosestFace(this.currentTarget);
        this.lookAtSpawner(this.currentTarget, side);
        mc.field_1761.method_2902(this.currentTarget, side);
        mc.field_1687.method_74254(this.currentTarget, side);
        mc.field_1724.method_6104(class_1268.field_5808);
        if (!this.isSpawner(this.currentTarget)) {
            this.minedSpawnerCount = this.minedSpawnerCount + 1;
            this.currentTarget = null;
        }
    }

    private EnemyStatus getEnemyStatus() {
        double criticalDistanceSq = SpawnerProtect.square(((Integer)this.criticalDistance.getValue()).intValue());
        boolean anyEnemy = false;
        boolean criticalThreat = false;
        class_1657 threat = null;
        double threatDistance = -1;
        for (class_1657 other : mc.field_1687.method_18456()) {
            if (other == mc.field_1724) continue;
            if (other.method_7325()) continue;
            while (other.method_5722(mc.field_1724)) {
            }
            while (Friends.isSpawnerProtect()) {
                if (!Friends.isFriend(other.method_5477().getString())) break;
            }
            anyEnemy = true;
            double distanceSq = mc.field_1724.method_5858(other);
            if ((distanceSq > criticalDistanceSq)) continue;
            criticalThreat = true;
            double distance = Math.sqrt(distanceSq);
            if (threat == null) { /* goto L174; */ }
            if (distance >= threatDistance) break;
            threat = other;
            threatDistance = distance;
            break;
        }
        return new EnemyStatus(anyEnemy, criticalThreat, threat, threatDistance);
    }

    private class_2338 findClosestSpawner() {
        List<class_2338> spawners = new ArrayList();
        class_2338 center = mc.field_1724.method_24515();
        int radiusSq = 1024;
        for (int x = -32; x <= 32; x++) {
            for (int y = -32; y <= 32; y++) {
                for (int z = -32; z <= 32; z++) {
                    int distSq = x * x + y * y + z * z;
                    if (!(distSq > radiusSq)) {
                        class_2338 pos = center.method_10069(x, y, z);
                        if (this.isSpawner(pos)) {
                            if (!mc.field_1724.method_56093(pos, 5)) continue;
                            spawners.add(pos.method_10062());
                        }
                    }
                }
            }
        }
        return spawners.stream().min(Comparator.comparingDouble(this::distanceSqTo)).orElse(null);
    }

    private boolean hasSilkTouchPickaxeInHotbar() {
        return this.findSilkTouchPickaxeHotbarSlot() != -1;
    }

    private int findSilkTouchPickaxeHotbarSlot() {
        for (int slot = 0; slot < 9; slot++) {
            if (!this.isSilkTouchPickaxe(mc.field_1724.method_31548().method_5438(slot))) continue;
            return slot;
        }
        return -1;
    }

    private boolean isSilkTouchPickaxe(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return false;
        }
        String itemPath = class_7923.field_41178.method_10221(stack.method_7909()).method_12832();
        if (!itemPath.endsWith("_pickaxe")) {
            return false;
        }
        class_6880<class_1887> silkTouch = mc.field_1687.method_30349().method_30530(class_7924.field_41265).method_47983((class_1887)mc.field_1687.method_30349().method_30530(class_7924.field_41265).method_29107(class_1893.field_9099));
        return silkTouch != null && class_1890.method_8225(silkTouch, stack) > 0;
    }

    private boolean isSpawner(class_2338 pos) {
        return mc.field_1687 != null && mc.field_1687.method_8320(pos).method_27852(class_2246.field_10260);
    }

    private class_2350 getClosestFace(class_2338 pos) {
        class_243 delta = mc.field_1724.method_33571().method_1020(class_243.method_24953(pos));
        return class_2350.method_10142(delta.field_1352, delta.field_1351, delta.field_1350);
    }

    private void lookAtSpawner(class_2338 pos, class_2350 direction) {
        class_243 eyePos = mc.field_1724.method_33571();
        class_243 target = class_243.method_24953(pos);
        double dx = target.field_1352 - eyePos.field_1352;
        double dy = target.field_1351 - eyePos.field_1351;
        double dz = target.field_1350 - eyePos.field_1350;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (Math.toDegrees(Math.atan2(dz, dx)) - 90);
        float pitch = -Math.toDegrees(Math.atan2(dy, horizontalDist));
        mc.field_1724.method_36456(yaw);
        mc.field_1724.method_36457(pitch);
        mc.field_1765 = new class_3965(target, direction, pos, false);
    }

    private void selectHotbarSlot(int slot) {
        if (slot >= 0) {
            if (slot < 9) {
                if (mc.field_1724.method_31548().method_67532() != slot) {
                    mc.field_1724.method_31548().method_61496(slot);
                }
            }
        }
    }

    private void applySneak() {
        mc.field_1690.field_1832.method_23481(true);
        mc.field_1724.method_5660(true);
    }

    private void stopMining() {
        this.currentTarget = null;
        if (mc.field_1761 != null) {
            if (mc.field_1761.method_2923()) {
                mc.field_1761.method_2925();
            }
        }
        if (mc.field_1724 != null) {
            mc.field_1690.field_1832.method_23481(false);
            mc.field_1724.method_5660(false);
        }
    }

    private void fireWebhookThenDisconnect(WebhookSnapshot snapshot) {
        this.stopMining();
        String webhook = this.normalizeWebhook((String)this.webhookUrl.getValue());
        if (!this.isValidWebhook(webhook)) {
            this.disconnectScheduled = false;
            this.disconnectAndDisable(snapshot.disconnectReason());
            return;
        }
        this.disconnectScheduled = true;
        CompletableFuture.runAsync(this::lambda$fireWebhookThenDisconnect$0 /* captured: snapshot */, class_156.method_27958().method_64116("coordsnapper-send")).whenComplete(this::lambda$fireWebhookThenDisconnect$2 /* captured: snapshot */);
    }

    private WebhookSnapshot captureSuccessSnapshot() {
        String webhook = this.normalizeWebhook((String)this.webhookUrl.getValue());
        return new WebhookSnapshot(webhook, "[SpawnerProtect]", "All your spawners have been collected.", 5624994, mc.field_1724.method_5477().getString(), "", "", this.countSpawnersInInventory(), true, this.resolveServerIp(), TIME_FORMATTER.format(LocalTime.now()), "https://mc-heads.net/body/" + this.encodeSkinName(mc.field_1724.method_5477().getString()), "SpawnerProtect finished");
    }

    private WebhookSnapshot captureCriticalSnapshot(class_1657 threat, double distance) {
        String webhook = this.normalizeWebhook((String)this.webhookUrl.getValue());
        String threatName = threat != null ? threat.method_5477().getString() : "Unknown";
        Object[] tmp0 = new Object[1];
        tmp0[0] = distance;
        return new WebhookSnapshot(webhook, "[SpawnerProtect]", threatName + " came too close.", 14838378, mc.field_1724.method_5477().getString(), threatName, String.format(Locale.ROOT, "%.1f", tmp0), this.countSpawnersInInventory(), false, this.resolveServerIp(), TIME_FORMATTER.format(LocalTime.now()), "https://mc-heads.net/body/" + this.encodeSkinName(threatName), "Enemy within critical distance");
    }

    private void sendWebhook(WebhookSnapshot snapshot) {
        JsonObject payload = new JsonObject();
        payload.addProperty("username", "SpawnerProtect");
        JsonObject embed = new JsonObject();
        embed.addProperty("title", snapshot.title());
        embed.addProperty("description", snapshot.description());
        embed.addProperty("color", snapshot.color());
        JsonArray fields = new JsonArray();
        fields.add(this.createField("Player", snapshot.playerName(), false));
        fields.add(this.createField("Time", snapshot.time(), true));
        fields.add(this.createField("Server", snapshot.serverIp(), true));
        fields.add(this.createField("All spawners mined", snapshot.allMined() ? "\u2705 Yes" : "\u274c No", false));
        fields.add(this.createField("Spawners in bag", snapshot.spawnersInInventory() + " spawners", false));
        if (!snapshot.threatName().isBlank()) {
            fields.add(this.createField("Threat", snapshot.threatName() + " (" + snapshot.distance() + " blocks)", false));
        }
        embed.add("fields", fields);
        JsonObject thumbnail = new JsonObject();
        thumbnail.addProperty("url", snapshot.skinRenderUrl());
        embed.add("thumbnail", thumbnail);
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        payload.add("embeds", embeds);
        HttpRequest request = HttpRequest.newBuilder(this.buildWebhookUri(snapshot.webhook())).timeout(HTTP_TIMEOUT).header("Content-Type", "application/json").header("Accept", "application/json").header("User-Agent", "Xenon-CoordSnapper").POST(HttpRequest.BodyPublishers.ofString(payload.toString())).build();
        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception exception) {
            XenonClient.LOGGER.error("SpawnerProtect webhook request failed", exception);
            throw new IllegalStateException("Webhook request failed", exception);
        }
        int status = response.statusCode();
        XenonClient.LOGGER.info("SpawnerProtect webhook sent");
        if (status >= 200 && status < 300) {
            return;
        }
        String body = response.body();
        XenonClient.LOGGER.warn("SpawnerProtect webhook rejected with status {} and body {}", status, this.trimSingleLine(body, 240));
        if (body != null && !body.isBlank()) {
            throw new IllegalStateException("HTTP " + status + ": " + this.trimSingleLine(body, 120));
        }
        XenonClient.LOGGER.warn("SpawnerProtect webhook rejected with status {}", status);
        throw new IllegalStateException("HTTP " + status);
    }

    private JsonObject createField(String name, String value, boolean inline) {
        JsonObject field = new JsonObject();
        field.addProperty("name", name);
        if (value != null) {
            field.addProperty("value", value.isBlank() ? "-" : value);
        }
        field.addProperty("inline", inline);
        return field;
    }

    private String resolveServerIp() {
        class_642 serverInfo = mc.method_1558();
        if (serverInfo == null || serverInfo.field_3761 == null || serverInfo.field_3761.isBlank()) {
            return "Singleplayer";
        }
        String host = this.normalizeHost(serverInfo.field_3761);
        return host.isEmpty() ? "Singleplayer" : host;
    }

    private String normalizeHost(String address) {
        String normalized = address == null ? "" : address.trim().toLowerCase(Locale.ROOT);
        int slashIndex = normalized.indexOf(47);
        if (slashIndex >= 0) {
            normalized = normalized.substring(0, slashIndex);
        }
        int colonIndex = normalized.indexOf(58);
        if (colonIndex >= 0) {
            normalized = normalized.substring(0, colonIndex);
        }
        return normalized;
    }

    private String normalizeWebhook(String value) {
        return value == null ? "" : value.trim();
    }

    private String encodeSkinName(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return "Steve";
        }
        return playerName.trim();
    }

    private URI buildWebhookUri(String webhook) {
        URI uri = URI.create(webhook);
        String query = uri.getQuery();
        if (query == null || query.isBlank()) {
            return URI.create(webhook + "?wait=true");
        }
        if (query.contains("wait=")) {
            return uri;
        }
        return URI.create(webhook + "&wait=true");
    }

    private boolean isValidWebhook(String webhook) {
        if (webhook.isEmpty()) {
            return false;
        }
        try {
            URI uri = URI.create(webhook);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            String path = uri.getPath();
            if (host != null && !(host.isBlank()) && path != null && path.contains("/api/webhooks/")) {
                return "https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme);
            }
        }
        catch (Exception ignored) {
            return false;
        }
    }

    private String trimSingleLine(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String trimmed = value.replace('\n', ' ').replace('\r', ' ').trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private int countSpawnersInInventory() {
        int count = 0;
        for (int i = 0; i < mc.field_1724.method_31548().method_5439(); i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (!stack.method_7960()) {
                if (!stack.method_31574(class_2246.field_10260.method_8389())) continue;
                count = count + stack.method_7947();
            }
        }
        return count;
    }

    private void disconnectAndDisable(String reason) {
        if (mc.method_1562() != null) {
            if (mc.method_1562().method_48296() != null) {
                mc.method_1562().method_48296().method_10747(class_2561.method_43470(reason));
            }
        }
        this.setEnabled(false);
    }

    private double distanceSqTo(class_2338 pos) {
        return mc.field_1724.method_5707(class_243.method_24953(pos));
    }

    private static double square(int value) {
        return (double)value * (double)value;
    }

    private void resetRuntimeState() {
        this.currentTarget = null;
        this.disconnectScheduled = false;
        this.minedSpawnerCount = 0;
    }
}
