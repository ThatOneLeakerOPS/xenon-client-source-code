/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.xenon.XenonClient;
import com.xenon.module.ActivatableModule;
import com.xenon.module.Category;
import com.xenon.setting.Setting;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_156;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_642;

public final class CoordSnapper
extends ActivatableModule {
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(8L);
    private static final int SUCCESS_COLOR = -11152222;
    private static final int ERROR_COLOR = -1938838;
    private static final int WARNING_COLOR = -1002662;
    private static final class_1799 NOTIFICATION_STACK = new class_1799(class_1802.field_38747);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.ROOT);
    private static final long TRIGGER_COOLDOWN_MS = 250L;
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(HTTP_TIMEOUT).followRedirects(HttpClient.Redirect.NORMAL).build();
    private final Setting<String> webhookUrl = new CoordSnapper.1(this, "Webhook", "");
    private final Setting<Boolean> notification = new Setting("Notification", true);
    private volatile long lastTriggerAt;

    public CoordSnapper() {
        super("CoordSnapper", Category.MISC);
        this.addSetting(this.webhookUrl);
        this.addSetting(this.notification);
    }

    public void onActivationKeyPressed() {
        if (!this.isEnabled() || mc.field_1724 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastTriggerAt < 250L) {
            return;
        }
        this.lastTriggerAt = now;
        String webhook = this.normalizeWebhook((String)this.webhookUrl.getValue());
        if (!this.isValidWebhook(webhook)) {
            this.showNotification("Webhook invalid", "Set a Discord webhook URL.", -1002662);
            return;
        }
        CoordSnapshot snapshot = this.captureSnapshot(webhook);
        CompletableFuture.runAsync(this::lambda$onActivationKeyPressed$0 /* captured: snapshot */, class_156.method_27958().method_64116("coordsnapper-send")).whenComplete(this::lambda$onActivationKeyPressed$2 /* captured: snapshot */);
    }

    private CoordSnapshot captureSnapshot(String webhook) {
        int blockX = mc.field_1724.method_31477();
        int blockY = mc.field_1724.method_31478();
        int blockZ = mc.field_1724.method_31479();
        String playerName = mc.field_1724.method_5477().getString();
        String serverIp = this.resolveServerIp();
        String time = TIME_FORMATTER.format(ZonedDateTime.now());
        String encodedName = this.encodeSkinName(playerName);
        String skinRenderUrl = "https://mc-heads.net/body/" + encodedName;
        return new CoordSnapshot(webhook, playerName, blockX, blockY, blockZ, serverIp, time, skinRenderUrl);
    }

    private void sendWebhook(CoordSnapshot snapshot) {
        JsonObject payload = new JsonObject();
        payload.addProperty("username", "CoordSnapper");
        JsonObject embed = new JsonObject();
        embed.addProperty("title", "CoordSnapper");
        embed.addProperty("color", 5624994);
        JsonArray fields = new JsonArray();
        fields.add(this.createField("Name", snapshot.playerName(), false));
        fields.add(this.createField("Coords", snapshot.coordsField(), false));
        fields.add(this.createField("IP", snapshot.serverIp(), true));
        fields.add(this.createField("Time", snapshot.time(), true));
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
            XenonClient.LOGGER.error("CoordSnapper webhook request failed", exception);
            throw new IllegalStateException("Webhook request failed", exception);
        }
        int status = response.statusCode();
        XenonClient.LOGGER.info("CoordSnapper sent coords for {}", snapshot.playerName());
        if (status >= 200 && status < 300) {
            return;
        }
        String body = response.body();
        XenonClient.LOGGER.warn("CoordSnapper webhook rejected with status {} and body {}", status, this.trimSingleLine(body, 240));
        if (body != null && !body.isBlank()) {
            throw new IllegalStateException("HTTP " + status + ": " + this.trimSingleLine(body, 120));
        }
        XenonClient.LOGGER.warn("CoordSnapper webhook rejected with status {}", status);
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

    private void showNotification(String message, String details, int accentColor) {
        if (mc == null || !((Boolean)this.notification.getValue()).booleanValue()) {
            return;
        }
        mc.execute(CoordSnapper::lambda$showNotification$3 /* captured: message, details, accentColor */);
    }

    private String getRootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.isBlank() ? current.getClass().getSimpleName() : this.trimSingleLine(message, 120);
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
}
