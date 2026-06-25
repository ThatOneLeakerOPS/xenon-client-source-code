/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_10538;
import net.minecraft.class_12079;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_7920;
import net.minecraft.class_8685;

public final class SkinChanger
extends Module {
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(10L);
    private static final long INPUT_DEBOUNCE_MS = 600L;
    private static final String PRIMARY_LOOKUP_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String FALLBACK_LOOKUP_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/name/";
    private static final String PROFILE_LOOKUP_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(HTTP_TIMEOUT).followRedirects(HttpClient.Redirect.NORMAL).build();
    private static volatile class_8685 overrideSkin;
    private static volatile class_12079.class_12081 overrideTextureAsset;
    private final Setting<String> playerName = new Setting("Player Name", "");
    private final AtomicInteger requestGeneration = new AtomicInteger();
    private class_10538 skinDownloader;
    private String lastObservedName = "";
    private String lastRequestedName = "";
    private long lastNameEditAt;

    public SkinChanger() {
        super("SkinChanger", Category.MISC);
        this.addSetting(this.playerName);
    }

    public void onEnable() {
        super.onEnable();
        this.lastObservedName = SkinChanger.normalizeName((String)this.playerName.getValue());
        this.lastRequestedName = "";
        this.lastNameEditAt = System.currentTimeMillis();
        if (this.lastObservedName.isEmpty()) {
            SkinChanger.clearOverride();
            return;
        }
        this.requestSkin(this.lastObservedName);
    }

    public void onDisable() {
        super.onDisable();
        this.requestGeneration.incrementAndGet();
        this.lastRequestedName = "";
        SkinChanger.clearOverride();
    }

    public void onTick() {
        String currentName = SkinChanger.normalizeName((String)this.playerName.getValue());
        if (!Objects.equals(currentName, this.lastObservedName)) {
            this.lastObservedName = currentName;
            this.lastNameEditAt = System.currentTimeMillis();
            return;
        }
        if (currentName.isEmpty()) {
            if (overrideSkin != null || overrideTextureAsset != null) {
                this.lastRequestedName = "";
                SkinChanger.clearOverride();
            }
            return;
        }
        if (!Objects.equals(currentName, this.lastRequestedName)) {
            if (System.currentTimeMillis() - this.lastNameEditAt >= 600L) {
                this.requestSkin(currentName);
            }
        }
    }

    public static class_8685 getOverrideSkin(UUID playerUuid) {
        if (overrideSkin == null || playerUuid == null) {
            return null;
        }
        UUID localUuid = SkinChanger.getLocalPlayerUuid();
        return localUuid != null && localUuid.equals(playerUuid) ? overrideSkin : null;
    }

    private void requestSkin(String name) {
        this.lastRequestedName = name;
        int generation = this.requestGeneration.incrementAndGet();
        CompletableFuture.supplyAsync(this::lambda$requestSkin$0 /* captured: name */, class_156.method_27958().method_64116("skinchanger-lookup")).thenCompose(this::lambda$requestSkin$2).whenComplete(this::lambda$requestSkin$4 /* captured: generation, name */);
    }

    private class_10538 getSkinDownloader() {
        if (this.skinDownloader == null) {
            Objects.requireNonNull(mc);
            this.skinDownloader = new class_10538(mc.method_1487(), mc.method_1531(), mc::execute);
        }
        return this.skinDownloader;
    }

    private SkinLookup lookupSkin(String playerName) {
        try {
            UUID uuid = this.lookupUuid(playerName);
            TexturePayload payload = this.lookupTexturePayload(uuid);
            return new SkinLookup(playerName, uuid, payload.textureUrl(), payload.skinType());
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Request interrupted", exception);
        }
        catch (IOException exception) {
            throw new IllegalStateException(exception.getMessage(), exception);
        }
    }

    private UUID lookupUuid(String playerName) throws IOException, InterruptedException {
        JsonObject lookup = this.requestJson("https://api.mojang.com/users/profiles/minecraft/" + this.encodeName(playerName));
        if (lookup == null) {
            lookup = this.requestJson("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + this.encodeName(playerName));
        }
        if (lookup == null || !lookup.has("id")) {
            throw new IOException("Player not found");
        }
        return SkinChanger.parseUuid(lookup.get("id").getAsString());
    }

    private TexturePayload lookupTexturePayload(UUID uuid) throws IOException, InterruptedException {
        JsonObject profile = this.requestJson("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", ""));
        if (profile == null || !profile.has("properties")) {
            throw new IOException("Skin profile not found");
        }
        JsonArray properties = profile.getAsJsonArray("properties");
        Iterator var4 = properties.iterator();
        if (!var4.hasNext()) { /* goto L304; */ }
        JsonElement element = var4.next();
        while (!element.isJsonObject()) {
        }
        JsonObject property = element.getAsJsonObject();
        if ("textures".equalsIgnoreCase(SkinChanger.getString(property, "name"))) {
            while (!property.has("value")) {
            }
        }
        String decoded = new String(Base64.getDecoder().decode(property.get("value").getAsString()), StandardCharsets.UTF_8);
        JsonObject textureRoot = JsonParser.parseString(decoded).getAsJsonObject();
        JsonObject textures = textureRoot.getAsJsonObject("textures");
        JsonObject skin = textures != null ? textures.getAsJsonObject("SKIN") : null;
        if (skin != null) {
            if (skin.has("url")) {
                String modelName = null;
                JsonObject metadata = skin.getAsJsonObject("metadata");
                if (metadata != null) {
                    if (metadata.has("model")) {
                        modelName = metadata.get("model").getAsString();
                    }
                }
                class_7920 skinType = "slim".equalsIgnoreCase(modelName) ? class_7920.field_41122 : class_7920.field_41123;
                return new TexturePayload(skin.get("url").getAsString(), skinType);
            }
        }
        throw new IOException("No usable skin texture found");
    }

    private JsonObject requestJson(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(HTTP_TIMEOUT).header("Accept", "application/json").header("User-Agent", "Xenon-SkinChanger").GET().build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        if (status == 404 || status == 204) {
            return null;
        }
        if (status < 200 || status >= 300) {
            throw new IOException("HTTP " + status);
        }
        String body = response.body();
        if (body == null || body.isBlank()) {
            return null;
        }
        return JsonParser.parseString(body).getAsJsonObject();
    }

    private static synchronized void applyOverride(class_12079.class_12081 textureAsset, class_7920 skinType) {
        SkinChanger.destroyTexture(overrideTextureAsset);
        overrideTextureAsset = textureAsset;
        overrideSkin = class_8685.method_74884(textureAsset, null, null, skinType);
    }

    private static synchronized void clearOverride() {
        SkinChanger.destroyTexture(overrideTextureAsset);
        overrideTextureAsset = null;
        overrideSkin = null;
    }

    private static void destroyTexture(class_12079.class_12081 textureAsset) {
        if (textureAsset == null || mc == null) {
            return;
        }
        try {
            mc.method_1531().method_4615(textureAsset.comp_3627());
        }
        catch (Throwable e1) {
            if (!textureAsset.comp_3626().equals(textureAsset.comp_3627())) {
                mc.method_1531().method_4615(textureAsset.comp_3626());
            }
            return;
        }
        try {
            if (!textureAsset.comp_3626().equals(textureAsset.comp_3627())) {
                mc.method_1531().method_4615(textureAsset.comp_3626());
            }
        }
        catch (Throwable e1) {
            return;
        }
    }

    private void sendFeedback(String message) {
        if (mc == null || mc.field_1705 == null) {
            return;
        }
        try {
            mc.field_1705.method_1743().method_1812(class_2561.method_43470("[SkinChanger] " + message));
        }
        catch (Throwable e2) {
            return;
        }
    }

    private class_2960 createTextureId(SkinLookup lookup) {
        String safeName = lookup.playerName().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "");
        if (safeName.isEmpty()) {
            safeName = "player";
        }
        return class_2960.method_60655("xenon", "skins/" + safeName + "_" + lookup.uuid().toString().replace("-", ""));
    }

    private Path getCacheFile(UUID uuid) {
        return mc.field_1697.toPath().resolve("xenon-cache").resolve("skins").resolve(uuid.toString().replace("-", "") + ".png");
    }

    private String encodeName(String playerName) {
        return URLEncoder.encode(playerName, StandardCharsets.UTF_8);
    }

    private static UUID parseUuid(String rawUuid) {
        String normalized = rawUuid.replace("-", "");
        return UUID.fromString(normalized.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    private static String normalizeName(String value) {
        return value == null ? "" : value.trim();
    }

    private static String getString(JsonObject object, String key) {
        return object.has(key) ? object.get(key).getAsString() : "";
    }

    private static String getRootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.isBlank() ? current.getClass().getSimpleName() : message;
    }

    private static UUID getLocalPlayerUuid() {
        if (mc == null) {
            return null;
        }
        if (mc.field_1724 != null) {
            return mc.field_1724.method_5667();
        }
        return mc.method_1548() != null ? mc.method_1548().method_44717() : null;
    }
}
