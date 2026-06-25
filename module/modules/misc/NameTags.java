/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.gui.ClickGUI;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.render.Freecam;
import com.xenon.setting.Setting;
import com.xenon.utils.NametagRenderState;
import com.xenon.utils.RenderUtils;
import com.xenon.utils.renderer.ProjectionUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import net.minecraft.class_10799;
import net.minecraft.class_124;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_5250;
import net.minecraft.class_640;
import net.minecraft.class_9064;
import org.joml.Matrix3x2fStack;

public final class NameTags
extends Module {
    private static final float MAX_RENDER_DISTANCE = 64f;
    private static final boolean SHOW_ABSORPTION = true;
    private static final int HUD_TEXT_COLOR = -1;
    private static final int HUD_OUTLINE_COLOR = -16777216;
    private static final float HUD_WORLD_SCALE = 0.025f;
    private static final int HUD_OUTLINE_RADIUS = 1;
    private static final int HUD_NAME_OFFSET = 0;
    private static final int HUD_HEALTH_OFFSET_WITH_NAME = 10;
    private static final int HUD_HEALTH_OFFSET_NO_NAME = 0;
    private static final int HUD_ITEM_SIZE = 16;
    private static final int HUD_ITEM_GAP = 2;
    private static final int HUD_ITEM_ROW_OFFSET_WITH_HEALTH = 34;
    private static final int HUD_ITEM_ROW_OFFSET_WITH_NAME = 16;
    private static final int HUD_ITEM_ROW_OFFSET_NO_TEXT = 0;
    private static final double HUD_ANCHOR_Y_ADJUST = 0.62;
    private static final double HUD_FRUSTUM_Y_PADDING = 1.25;
    private static final long HUD_CACHE_DURATION_MS = 125L;
    private static final int HEART_ICON_SIZE = 9;
    private static final int HEART_ICON_SPACING = 8;
    private static final class_2960 HEART_CONTAINER_TEXTURE = class_2960.method_60656("hud/heart/container");
    private static final class_2960 HEART_FULL_TEXTURE = class_2960.method_60656("hud/heart/full");
    private static final class_2960 HEART_HALF_TEXTURE = class_2960.method_60656("hud/heart/half");
    private static final class_2960 HEART_ABS_FULL_TEXTURE = class_2960.method_60656("hud/heart/absorbing_full");
    private static final class_2960 HEART_ABS_HALF_TEXTURE = class_2960.method_60656("hud/heart/absorbing_half");
    private static final Pattern MINECRAFT_COLOR_CODE_PATTERN = Pattern.compile("\u00a7.");
    public static NameTags instance;
    private final Setting<Boolean> self = new Setting("Self", true);
    private final Setting<Boolean> name = new Setting("Name", true);
    private final Setting<Boolean> ping = new Setting("Ping", true);
    private final Setting<Boolean> health = new Setting("Health", true);
    private final Setting<Boolean> mainHand = new Setting("MainHand", true);
    private final Setting<Boolean> offHand = new Setting("OffHand", true);
    private final Setting<Boolean> armor = new Setting("Armor", true);
    private final Map<UUID, CachedHudData> hudCache = new HashMap();
    private final ProjectionUtil.ScreenProjection screenProjection = new ProjectionUtil.ScreenProjection();
    private int hudConfigSignature = -2147483648;

    public NameTags() {
        super("NameTags", Category.MISC);
        instance = this;
        this.addSetting(this.self);
        this.addSetting(this.name);
        this.addSetting(this.ping);
        this.addSetting(this.health);
        this.addSetting(this.mainHand);
        this.addSetting(this.offHand);
        this.addSetting(this.armor);
    }

    public static boolean isActive() {
        return instance != null && instance.isEnabled() && mc != null && mc.field_1724 != null;
    }

    public static void renderHud(class_332 context, float tickDelta) {
        if (!NameTags.isActive() || mc.field_1687 == null || mc.field_1690.field_1842 || NameTags.isMenuOpen()) {
            return;
        }
        NameTags module = instance;
        if (module == null) {
            return;
        }
        module.ensureHudCacheConfig();
        module.pruneHudCacheIfNeeded();
        long now = System.currentTimeMillis();
        class_4184 camera = RenderUtils.getCamera();
        if (camera == null) {
            return;
        }
        class_243 cameraPos = RenderUtils.getCameraPos(camera);
        double cameraX = cameraPos.field_1352;
        double cameraY = cameraPos.field_1351;
        double cameraZ = cameraPos.field_1350;
        double maxDistanceSq = 4096;
        double scaleBaseX = mc.method_22683().method_4486() * 0.5 * (double)Math.abs(ProjectionUtil.projectionMatrix.m00()) * 0.02500000037252903;
        double scaleBaseY = mc.method_22683().method_4502() * 0.5 * (double)Math.abs(ProjectionUtil.projectionMatrix.m11()) * 0.02500000037252903;
        Matrix3x2fStack matrices = context.method_51448();
        for (class_1657 player : mc.field_1687.method_18456()) {
            while (!module.shouldRenderFor(player)) {
            }
            double worldX = class_3532.method_16436((double)tickDelta, player.field_6038, player.method_23317());
            double worldY = class_3532.method_16436((double)tickDelta, player.field_5971, player.method_23318());
            double worldZ = class_3532.method_16436((double)tickDelta, player.field_5989, player.method_23321());
            double deltaX = worldX - cameraX;
            double deltaY = worldY - cameraY;
            double deltaZ = worldZ - cameraZ;
            double squaredDistance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            while (squaredDistance > maxDistanceSq) {
            }
            CachedHudData hudData = module.getCachedHudData(player, now);
            while (hudData.isEmpty()) {
            }
            double halfWidth = Math.max(0.35, (double)player.method_17681() * 0.5);
            while (!RenderUtils.isWorldBoxVisible(worldX - halfWidth, worldY, worldZ - halfWidth, worldX + halfWidth, worldY + (double)player.method_17682() + 1.25, worldZ + halfWidth)) {
            }
            float hudScale = module.projectHudAnchor(player, tickDelta, worldX, worldY, worldZ, scaleBaseX, scaleBaseY);
            while (hudScale <= 0f) {
            }
            matrices.pushMatrix();
            NameTags.applyHudTransform(matrices, (float)module.screenProjection.x, (float)module.screenProjection.y, hudScale);
            NameTags.renderHudLabel(context, hudData.nameLabel(), hudData.nameWidth(), 0, false);
            NameTags.renderHudHealth(context, hudData.healthData(), hudData.nameLabel() != null ? 10 : 0);
            NameTags.renderHudItems(context, hudData.items(), hudData.itemRowWidth(), hudData.nameLabel() != null, hudData.healthData() != null);
            matrices.popMatrix();
        }
    }

    public boolean shouldRenderFor(class_1309 entity) {
        if (!entity.method_5805() || entity instanceof class_1531 || !(entity instanceof class_1657)) {
            return false;
        }
        if (entity != mc.field_1724 && entity.method_5756(mc.field_1724)) {
            return false;
        }
        if (entity != mc.field_1724) return true;
        if (mc.field_1690.method_31044().method_31034()) {
            if (Freecam.instance != null) {
                return ((Boolean)this.self.getValue()).booleanValue();
            }
        }
        return true;
    }

    public boolean shouldRenderForState(class_1309 entity, double squaredDistanceToCamera) {
        if (!this.shouldRenderFor(entity) || NameTags.isMenuOpen()) {
            return false;
        }
        return squaredDistanceToCamera <= 4096;
    }

    public void onEnable() {
        this.hudCache.clear();
        this.hudConfigSignature = -2147483648;
    }

    public void onDisable() {
        this.hudCache.clear();
    }

    public void onTick() {
    }

    public class_2561 buildNameLabel(class_1309 entity) {
        class_5250 text = class_2561.method_43473();
        boolean hasContent = false;
        if (((Boolean)this.name.getValue()).booleanValue()) {
            text.method_10852(class_2561.method_43470("| ").method_27692(class_124.field_1062));
            text.method_10852(entity.method_5476().method_27661().method_27692(class_124.field_1068));
            hasContent = true;
        }
        if (((Boolean)this.ping.getValue()).booleanValue()) {
            if (entity instanceof class_1657) {
                class_1657 player = entity;
                int latency = this.getPing(player);
                if (latency >= 0) {
                    if (hasContent) {
                        text.method_10852(class_2561.method_43470(" ").method_27692(class_124.field_1080));
                    }
                    text.method_10852(class_2561.method_43470("[").method_27692(class_124.field_1063));
                    text.method_10852(class_2561.method_43470(latency + " ms").method_27692(this.getPingFormatting(latency)));
                    text.method_10852(class_2561.method_43470("]").method_27692(class_124.field_1063));
                    hasContent = true;
                }
            }
        }
        if (((Boolean)this.health.getValue()).booleanValue()) {
            float absorption = Math.max(0f, entity.method_6067());
            if (absorption > 0f) {
                int abs = Math.max(1, class_3532.method_15386(absorption));
                if (hasContent) {
                    text.method_10852(class_2561.method_43470(" ").method_27692(class_124.field_1080));
                }
                text.method_10852(class_2561.method_43470("+" + abs).method_27692(class_124.field_1065));
                hasContent = true;
            }
        }
        return hasContent ? text : null;
    }

    private HealthRenderData getHealthRenderData(class_1309 entity) {
        if (!((Boolean)this.health.getValue()).booleanValue()) {
            return null;
        }
        float maxHealth = Math.max(1f, entity.method_6063());
        float currentHealth = class_3532.method_15363(entity.method_6032(), 0f, maxHealth);
        float absorption = Math.max(0f, entity.method_6067());
        int maxHearts = Math.max(1, class_3532.method_15386(maxHealth / 2f));
        if (maxHearts > 10) {
            float scale = 10f / (float)maxHearts;
            currentHealth = currentHealth * scale;
            absorption = absorption * scale;
            maxHearts = 10;
        }
        int filledHalfHearts = class_3532.method_15340(Math.round(currentHealth), 0, maxHearts * 2);
        int fullHearts = filledHalfHearts / 2;
        boolean halfHeart = filledHalfHearts & 1 != 0;
        int emptyHearts = Math.max(0, maxHearts - fullHearts - (halfHeart ? 1 : 0));
        int absorptionHalfHearts = Math.max(0, Math.round(absorption));
        int absorptionFullHearts = absorptionHalfHearts / 2;
        boolean absorptionHalfHeart = absorptionHalfHearts & 1 != 0;
        int iconCount = maxHearts + absorptionFullHearts + (absorptionHalfHeart ? 1 : 0);
        if (fullHearts <= 0 && !halfHeart && absorptionFullHearts <= 0 && !absorptionHalfHeart && emptyHearts <= 0) {
            return null;
        }
        int totalWidth = (iconCount - 1) * 8 + 9;
        return new HealthRenderData(maxHearts, fullHearts, halfHeart, emptyHearts, absorptionFullHearts, absorptionHalfHeart, totalWidth);
    }

    public List<NametagRenderState.ItemEntry> buildItemEntries(class_1309 entity) {
        List<NametagRenderState.ItemEntry> items = new ArrayList(6);
        if (((Boolean)this.offHand.getValue()).booleanValue()) {
            this.addItem(items, entity.method_6079());
        }
        if (((Boolean)this.armor.getValue()).booleanValue()) {
            this.addItem(items, entity.method_6118(class_1304.field_6166));
            this.addItem(items, entity.method_6118(class_1304.field_6172));
            this.addItem(items, entity.method_6118(class_1304.field_6174));
            this.addItem(items, entity.method_6118(class_1304.field_6169));
        }
        if (((Boolean)this.mainHand.getValue()).booleanValue()) {
            this.addItem(items, entity.method_6047());
        }
        return items;
    }

    private void addItem(List<NametagRenderState.ItemEntry> items, class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return;
        }
        items.add(new NametagRenderState.ItemEntry(stack.method_7972()));
    }

    private static void renderHudItems(class_332 context, List<NametagRenderState.ItemEntry> items, int rowWidth, boolean hasNameLabel, boolean hasHealthLabel) {
        if (items.isEmpty()) {
            return;
        }
        int startX = -(rowWidth / 2);
        int offset = hasHealthLabel ? 34 : hasNameLabel ? 16 : 0;
        int y = -offset;
        for (int index = 0; index < items.size(); index++) {
            class_1799 stack = ((NametagRenderState.ItemEntry)items.get(index)).stack();
            int x = startX + index * 18;
            context.method_51427(stack, x, y);
            context.method_51432(mc.field_1772, stack, x, y, null);
        }
    }

    private static void renderHudLabel(class_332 context, class_2561 text, int textWidth, int yOffset, boolean outlined) {
        if (text == null) {
            return;
        }
        int x = -(textWidth / 2);
        int y = -yOffset;
        if (!outlined) { /* goto L96; */ }
        String plain = text.getString();
        for (int offsetX = -1; offsetX <= 1; offsetX++) {
            for (int offsetY = -1; offsetY <= 1; offsetY++) {
                if (!offsetX != 0 && offsetY == 0) continue;
                context.method_51433(mc.field_1772, plain, x + offsetX, y + offsetY, -16777216, false);
            }
        }
        context.method_51439(mc.field_1772, text, x, y, -1, false);
    }

    private static void renderHudHealth(class_332 context, HealthRenderData healthData, int yOffset) {
        if (healthData == null) {
            return;
        }
        int x = -(healthData.totalWidth() / 2);
        int y = -yOffset;
        for (int index = 0; index < healthData.baseHeartCount(); index++) {
            int heartX = x + index * 8;
            NameTags.drawHeart(context, HEART_CONTAINER_TEXTURE, heartX, y);
        }
        for (int index = 0; index < healthData.fullHearts(); index++) {
            int heartX = x + index * 8;
            NameTags.drawHeart(context, HEART_FULL_TEXTURE, heartX, y);
        }
        if (healthData.halfHeart()) {
            int heartX = x + healthData.fullHearts() * 8;
            NameTags.drawHeart(context, HEART_HALF_TEXTURE, heartX, y);
        }
        int absorptionStart = x + healthData.baseHeartCount() * 8;
        int absorptionIcons = healthData.absorptionFullHearts() + (healthData.absorptionHalfHeart() ? 1 : 0);
        for (int index = 0; index < absorptionIcons; index++) {
            int heartX = absorptionStart + index * 8;
            NameTags.drawHeart(context, HEART_CONTAINER_TEXTURE, heartX, y);
        }
        for (int index = 0; index < healthData.absorptionFullHearts(); index++) {
            int heartX = absorptionStart + index * 8;
            NameTags.drawHeart(context, HEART_ABS_FULL_TEXTURE, heartX, y);
        }
        if (healthData.absorptionHalfHeart()) {
            int heartX = absorptionStart + healthData.absorptionFullHearts() * 8;
            NameTags.drawHeart(context, HEART_ABS_HALF_TEXTURE, heartX, y);
        }
    }

    private static void drawHeart(class_332 context, class_2960 texture, int x, int y) {
        context.method_52706(class_10799.field_56883, texture, x, y, 9, 9);
    }

    private void ensureHudCacheConfig() {
        int currentSignature = this.getHudConfigSignature();
        if (currentSignature != this.hudConfigSignature) {
            this.hudConfigSignature = currentSignature;
            this.hudCache.clear();
        }
    }

    private void pruneHudCacheIfNeeded() {
        if (mc.field_1687 == null || this.hudCache.size() <= mc.field_1687.method_18456().size() + 8) {
            return;
        }
        this.hudCache.keySet().removeIf(NameTags::lambda$pruneHudCacheIfNeeded$0);
    }

    private int getHudConfigSignature() {
        int signature = 0;
        if (((Boolean)this.self.getValue()).booleanValue()) {
            signature = signature | 1;
        }
        if (((Boolean)this.name.getValue()).booleanValue()) {
            signature = signature | 2;
        }
        if (((Boolean)this.ping.getValue()).booleanValue()) {
            signature = signature | 4;
        }
        if (((Boolean)this.health.getValue()).booleanValue()) {
            signature = signature | 8;
        }
        if (((Boolean)this.mainHand.getValue()).booleanValue()) {
            signature = signature | 16;
        }
        if (((Boolean)this.offHand.getValue()).booleanValue()) {
            signature = signature | 32;
        }
        if (((Boolean)this.armor.getValue()).booleanValue()) {
            signature = signature | 64;
        }
        return signature;
    }

    private CachedHudData getCachedHudData(class_1657 player, long now) {
        CachedHudData cached = this.hudCache.get(player.method_5667());
        if (cached != null && cached.expiresAtMs() > now) {
            return cached;
        }
        class_2561 nameLabel = this.buildNameLabel(player);
        List<NametagRenderState.ItemEntry> items = this.buildItemEntries(player);
        CachedHudData rebuilt = new CachedHudData(now + 125L, nameLabel, nameLabel != null ? mc.field_1772.method_27525(nameLabel) : 0, this.getHealthRenderData(player), items, items.isEmpty() ? 0 : items.size() * 16 + (items.size() - 1) * 2);
        this.hudCache.put(player.method_5667(), rebuilt);
        return rebuilt;
    }

    private static void applyHudTransform(Matrix3x2fStack matrices, float screenX, float screenY, float scale) {
        matrices.translate(screenX, screenY);
        matrices.scale(scale, scale);
    }

    private float projectHudAnchor(class_1657 player, float tickDelta, double worldX, double worldY, double worldZ, double scaleBaseX, double scaleBaseY) {
        class_243 localAnchor = player.method_56072().method_55675(class_9064.field_47745, 0, player.method_61415(tickDelta));
        if (localAnchor == null) {
            double anchorX = worldX;
            double anchorY = worldY + (double)player.method_17682() + 0.5 + 0.62;
            double anchorZ = worldZ;
        } else {
            double anchorX = worldX + localAnchor.field_1352;
            double anchorY = worldY + localAnchor.field_1351 + 0.62;
            double anchorZ = worldZ + localAnchor.field_1350;
        }
        if (!ProjectionUtil.projectToScreen(ProjectionUtil.modelViewMatrix, ProjectionUtil.projectionMatrix, anchorX, anchorY, anchorZ, this.screenProjection)) {
            return 0f;
        }
        if (!this.screenProjection.visible || this.screenProjection.z < 0 || this.screenProjection.z > 1 || this.screenProjection.w <= 0) {
            return 0f;
        }
        double scaleX = scaleBaseX / this.screenProjection.w;
        double scaleY = scaleBaseY / this.screenProjection.w;
        float screenScale = ((scaleX + scaleY) * 0.5);
        return Float.isFinite(screenScale) && screenScale > 0f ? screenScale : 0f;
    }

    private static boolean isMenuOpen() {
        return mc.field_1755 instanceof ClickGUI;
    }

    private int getPing(class_1657 player) {
        if (mc.method_1562() == null) {
            return -1;
        }
        class_640 entry = mc.method_1562().method_2871(player.method_5667());
        return entry != null ? entry.method_2959() : -1;
    }

    private class_124 getPingFormatting(int latency) {
        if (latency < 75) {
            return class_124.field_1060;
        }
        if (latency < 150) {
            return class_124.field_1054;
        }
        return class_124.field_1061;
    }

    private String stripMinecraftFormatting(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return MINECRAFT_COLOR_CODE_PATTERN.matcher(text).replaceAll("").trim();
    }

    private boolean containsLetters(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (!Character.isLetter(text.charAt(index))) continue;
            return true;
        }
        return false;
    }

    private String sanitizeLogText(String text) {
        if (text == null) {
            return "";
        }
        return text.replace('\n', ' ').replace('\r', ' ');
    }

    private String normalizeLookupName(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
