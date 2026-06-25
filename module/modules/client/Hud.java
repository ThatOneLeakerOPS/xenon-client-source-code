/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.client;

import com.xenon.gui.ClickGUI;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.ModuleManager;
import com.xenon.module.modules.client.SpotifyHud;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.setting.Setting;
import com.xenon.utils.renderer.RenderUtil;
import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_640;
import net.minecraft.class_7923;

public final class Hud
extends Module {
    private static Hud INSTANCE;
    private final Setting<Boolean> watermark = new Setting("Watermark", true);
    private final Setting<Boolean> coordinates = new Setting("Coordinates", true);
    private final Setting<Boolean> info = new Setting("Info", true);
    private final Setting<Boolean> moduleList = new Setting("Module List", true);
    private final Setting<Boolean> potionEffects = new Setting("Potion Effects", true);
    private final Setting<Boolean> armor = new Setting("Armor", true);
    private final Setting<Boolean> keybinds = new Setting("Keybinds", true);
    private final Setting<Boolean> notifications = new Setting("Notifications", true);
    private static final EnumMap<HudElement, int[]> positions = new EnumMap(HudElement.class);

    public static int[] getElementPos(HudElement el) {
        return positions.computeIfAbsent(el, Hud::lambda$getElementPos$0);
    }

    public static void setElementPos(HudElement el, int x, int y) {
        int[] tmp0 = new int[2];
        tmp0[0] = x;
        tmp0[1] = y;
        positions.put(el, tmp0);
        ModuleManager.INSTANCE.onSettingChanged();
    }

    public static int getElementW(HudElement el) {
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return 100;
        }
        switch (el.ordinal()) {
            default:
                throw new MatchException(null);
            case 0:
                return mc.field_1772.method_1727("Xenon +") + 20;
            case 1:
                return mc.field_1772.method_1727("XYZ: -00000.0 / -256.0 / -00000.0") + 14;
            case 2:
                return mc.field_1772.method_1727("999 FPS | 999ms | 23:59:59") + 14;
            case 3:
                return 120;
            case 4:
                return 110;
            case 5:
                return mc.field_1772.method_1727(" 100%") + 22;
            case 6:
                return 120;
            case 7:
                return SpotifyHud.getCardW();
        }
    }

    public static int[] getElementBounds(HudElement el) {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1724 == null) {
            int[] pos = Hud.getElementPos(el);
            int[] tmp0 = new int[4];
            tmp0[0] = pos[0];
            tmp0[1] = pos[1];
            tmp0[2] = Hud.getElementW(el);
            tmp0[3] = 14;
            return tmp0;
        }
        class_327 tr = mc.field_1772;
        int[] pos = Hud.getElementPos(el);
        switch (el.ordinal()) {
            default:
                throw new MatchException(null);
            case 0:
                int nameW = tr.method_1727("Xenon");
                int plusW = tr.method_1727(" +");
                int bw = nameW + plusW + 16;
                int[] tmp1 = new int[4];
                tmp1[0] = pos[0];
                tmp1[1] = pos[1];
                tmp1[2] = bw;
                tmp1[3] = 16;
                var var4 = tmp1;
                break;
            case 1:
                Object[] tmp2 = new Object[3];
                tmp2[0] = mc.field_1724.method_23317();
                tmp2[1] = mc.field_1724.method_23318();
                tmp2[2] = mc.field_1724.method_23321();
                String coords = String.format("%.1f / %.1f / %.1f", tmp2);
                int bw = tr.method_1727(coords) + 14;
                int[] tmp3 = new int[4];
                tmp3[0] = pos[0];
                tmp3[1] = pos[1];
                tmp3[2] = bw;
                tmp3[3] = 14;
                var4 = tmp3;
                break;
            case 2:
                int fps = mc.method_47599();
                int ping = 0;
                try {
                    if (mc.method_1562() != null) {
                        class_640 entry = mc.method_1562().method_2871(mc.field_1724.method_5667());
                        if (entry != null) {
                            ping = entry.method_2959();
                        }
                    }
                }
                catch (Exception time) {
                    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    String infoText = fps + " FPS | " + ping + "ms | " + time;
                    int bw = tr.method_1727(infoText) + 14;
                    int[] tmp4 = new int[4];
                    tmp4[0] = pos[0];
                    tmp4[1] = pos[1];
                    tmp4[2] = bw;
                    tmp4[3] = 14;
                    var4 = tmp4;
                }
                time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                infoText = fps + " FPS | " + ping + "ms | " + time;
                bw = tr.method_1727(infoText) + 14;
                int[] tmp5 = new int[4];
                tmp5[0] = pos[0];
                tmp5[1] = pos[1];
                tmp5[2] = bw;
                tmp5[3] = 14;
                var4 = tmp5;
                break;
            case 3:
                var4 = Hud.getModuleListBounds();
                break;
            case 4:
                List<class_1293> effects = new ArrayList(mc.field_1724.method_6026());
                int[] tmp6 = new int[4];
                tmp6[0] = pos[0];
                tmp6[1] = pos[1];
                tmp6[2] = Hud.getElementW(el);
                tmp6[3] = 14;
                var4 = tmp6;
                int maxW = 0;
                for (class_1293 eff : effects) {
                    int w = tr.method_1727(Hud.getEffectName(eff)) + 14;
                    if (w <= maxW) continue;
                    maxW = w;
                }
                totalH = effects.size() * 14 + (effects.size() - 1) * 3;
                int[] tmp6 = new int[4];
                tmp6[0] = pos[0];
                tmp6[1] = pos[1];
                tmp6[2] = Math.max(1, maxW);
                tmp6[3] = Math.max(1, totalH);
                var4 = tmp6;
                break;
            case 5:
                int maxTextW = 0;
                int rows = 0;
                class_1304[] tmp7 = new class_1304[4];
                tmp7[0] = class_1304.field_6169;
                tmp7[1] = class_1304.field_6174;
                tmp7[2] = class_1304.field_6172;
                tmp7[3] = class_1304.field_6166;
                class_1304[] slots = tmp7;
                for (class_1304 sl : slots) {
                    class_1799 stack = mc.field_1724.method_6118(sl);
                    if (stack != null) {
                        if (!(stack.method_7960())) {
                            rows++;
                            int pct = Math.round((1 - (double)stack.method_7919() / (double)stack.method_7936()) * 100);
                            if ((stack.method_7963() && stack.method_7936() > 0)) continue;
                            int pct = 100;
                            int w = tr.method_1727(pct + "%");
                            if (w <= maxTextW) continue;
                            maxTextW = w;
                        }
                    }
                }
                int[] tmp8 = new int[4];
                tmp8[0] = pos[0];
                tmp8[1] = pos[1];
                tmp8[2] = Hud.getElementW(el);
                tmp8[3] = 18;
                var4 = tmp8;
                bw = 22 + maxTextW + 6;
                bh = rows * 18 + (rows - 1) * 2;
                int[] tmp8 = new int[4];
                tmp8[0] = pos[0];
                tmp8[1] = pos[1];
                tmp8[2] = bw;
                tmp8[3] = bh;
                var4 = tmp8;
                break;
            case 6:
                List<Module> bound = Hud.collectBoundModules();
                int[] tmp9 = new int[4];
                tmp9[0] = pos[0];
                tmp9[1] = pos[1];
                tmp9[2] = Hud.getElementW(el);
                tmp9[3] = 14;
                var4 = tmp9;
                int maxW = 0;
                for (Module m : bound) {
                    int w = tr.method_1727(Hud.formatBindLine(m)) + 14;
                    if (w <= maxW) continue;
                    maxW = w;
                }
                totalH = bound.size() * 14 + (bound.size() - 1) * 3;
                int[] tmp9 = new int[4];
                tmp9[0] = pos[0];
                tmp9[1] = pos[1];
                tmp9[2] = Math.max(1, maxW);
                tmp9[3] = Math.max(1, totalH);
                var4 = tmp9;
                break;
            case 7:
                int[] tmp10 = new int[4];
                tmp10[0] = pos[0];
                tmp10[1] = pos[1];
                tmp10[2] = SpotifyHud.getCardW();
                tmp10[3] = SpotifyHud.getCardH();
                var4 = tmp10;
                /* note: stack not empty at case end */
        }
        try {
            if (mc.method_1562() != null) {
                entry = mc.method_1562().method_2871(mc.field_1724.method_5667());
                if (entry != null) {
                    ping = entry.method_2959();
                }
            }
        }
        catch (Exception time) {
            time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            infoText = fps + " FPS | " + ping + "ms | " + time;
            bw = tr.method_1727(infoText) + 14;
            int[] tmp11 = new int[4];
            tmp11[0] = pos[0];
            tmp11[1] = pos[1];
            tmp11[2] = bw;
            tmp11[3] = 14;
            var4 = tmp11;
            return var4;
        }
        time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        infoText = fps + " FPS | " + ping + "ms | " + time;
        bw = tr.method_1727(infoText) + 14;
        int[] tmp12 = new int[4];
        tmp12[0] = pos[0];
        tmp12[1] = pos[1];
        tmp12[2] = bw;
        tmp12[3] = 14;
        var4 = tmp12;
        return var4;
    }

    private static List<Module> collectBoundModules() {
        List<Module> bound = new ArrayList();
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m.getBind() == 0) continue;
            bound.add(m);
        }
        bound.sort(Comparator.comparing(Module::getName));
        return bound;
    }

    private static String formatBindLine(Module m) {
        return m.getName() + " [" + ClickGUI.getKeyDisplayNameStatic(m.getBind()) + "]";
    }

    public static boolean isElementVisible(HudElement el) {
        if (INSTANCE == null || !INSTANCE.isEnabled()) {
            return false;
        }
        switch (el.ordinal()) {
            default:
                throw new MatchException(null);
            case 0:
                return ((Boolean)INSTANCE.watermark.getValue()).booleanValue();
            case 1:
                return ((Boolean)INSTANCE.coordinates.getValue()).booleanValue();
            case 2:
                return ((Boolean)INSTANCE.info.getValue()).booleanValue();
            case 3:
                return ((Boolean)INSTANCE.moduleList.getValue()).booleanValue();
            case 4:
                return ((Boolean)INSTANCE.potionEffects.getValue()).booleanValue();
            case 5:
                return ((Boolean)INSTANCE.armor.getValue()).booleanValue();
            case 6:
                return ((Boolean)INSTANCE.keybinds.getValue()).booleanValue();
            case 7:
                return SpotifyHud.isActive();
        }
    }

    private static int[] defaultPos(HudElement el) {
        class_310 mc = class_310.method_1551();
        int W = mc != null ? mc.method_22683().method_4486() : 800;
        switch (el.ordinal()) {
            default:
                throw new MatchException(null);
            case 0:
                int[] tmp0 = new int[2];
                tmp0[0] = 8;
                tmp0[1] = 8;
                return tmp0;
            case 1:
                int[] tmp1 = new int[2];
                tmp1[0] = 8;
                tmp1[1] = 30;
                return tmp1;
            case 2:
                int[] tmp2 = new int[2];
                tmp2[0] = 8;
                tmp2[1] = 48;
                return tmp2;
            case 3:
                int[] tmp3 = new int[2];
                tmp3[0] = W - 130;
                tmp3[1] = 72;
                return tmp3;
            case 4:
                int[] tmp4 = new int[2];
                tmp4[0] = 8;
                tmp4[1] = 66;
                return tmp4;
            case 5:
                int[] tmp5 = new int[2];
                tmp5[0] = W - 60;
                tmp5[1] = 200;
                return tmp5;
            case 6:
                int[] tmp6 = new int[2];
                tmp6[0] = 8;
                tmp6[1] = 120;
                return tmp6;
            case 7:
                int[] tmp7 = new int[2];
                tmp7[0] = 8;
                tmp7[1] = 50;
                return tmp7;
        }
    }

    public Hud() {
        super("Hud", Category.CLIENT);
        this.addSetting(this.watermark);
        this.addSetting(this.coordinates);
        this.addSetting(this.info);
        this.addSetting(this.moduleList);
        this.addSetting(this.potionEffects);
        this.addSetting(this.armor);
        this.addSetting(this.keybinds);
        this.addSetting(this.notifications);
        INSTANCE = this;
    }

    public static boolean showModuleList() {
        return INSTANCE != null && INSTANCE.isEnabled() && ((Boolean)INSTANCE.moduleList.getValue()).booleanValue();
    }

    public static boolean showNotifications() {
        return INSTANCE != null && INSTANCE.isEnabled() && ((Boolean)INSTANCE.notifications.getValue()).booleanValue();
    }

    public static int[] getModuleListBounds() {
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            int[] tmp0 = new int[4];
            tmp0[0] = 0;
            tmp0[1] = 0;
            tmp0[2] = 120;
            tmp0[3] = 14;
            return tmp0;
        }
        class_327 tr = mc.field_1772;
        int[] pos = Hud.getElementPos(HudElement.MODULE_LIST);
        List<Module> enabled = new ArrayList();
        for (Module m : ModuleManager.INSTANCE.getModules()) {
            if (m.isEnabled()) {
                if (m.getCategory() == Category.CLIENT) continue;
                enabled.add(m);
            }
        }
        if (enabled.isEmpty()) {
            int[] tmp1 = new int[4];
            tmp1[0] = pos[0] - 120;
            tmp1[1] = pos[1];
            tmp1[2] = 120;
            tmp1[3] = 14;
            return tmp1;
        }
        enabled.sort(Comparator.comparingInt(Hud::lambda$getModuleListBounds$1 /* captured: tr */).reversed());
        widest = tr.method_1727(((Module)enabled.get(0)).getName()) + 14;
        int totalH = enabled.size() * 14 + (enabled.size() - 1) * 4;
        int[] tmp2 = new int[4];
        tmp2[0] = pos[0] - widest;
        tmp2[1] = pos[1];
        tmp2[2] = widest;
        tmp2[3] = totalH;
        return tmp2;
    }

    public static void renderHud(class_332 context) {
        if (INSTANCE == null || !INSTANCE.isEnabled()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1724 == null || mc.field_1690 == null) {
            return;
        }
        if (mc.field_1755 instanceof ClickGUI) {
            return;
        }
        if (mc.method_53526().method_53536()) {
            return;
        }
        class_327 tr = mc.field_1772;
        int accent = XenonPlus.getAccentARGB();
        Color accentColor = XenonPlus.getAccentColor();
        int W = mc.method_22683().method_4486();
        if (((Boolean)INSTANCE.watermark.getValue()).booleanValue()) {
            int[] pos = Hud.getElementPos(HudElement.WATERMARK);
            int nameW = tr.method_1727("Xenon");
            int plusW = tr.method_1727(" +");
            int bw = nameW + plusW + 16;
            int bh = 16;
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 6f, -652993496, false);
            RenderUtil.drawOutline(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 6f, 1f, -869253035, false);
            context.method_51433(tr, "Xenon", pos[0] + 8, pos[1] + 4, -985864, true);
            context.method_51433(tr, " +", pos[0] + 8 + nameW, pos[1] + 4, accent, true);
        }
        if (((Boolean)INSTANCE.coordinates.getValue()).booleanValue()) {
            int[] pos = Hud.getElementPos(HudElement.COORDINATES);
            Object[] tmp0 = new Object[3];
            tmp0[0] = mc.field_1724.method_23317();
            tmp0[1] = mc.field_1724.method_23318();
            tmp0[2] = mc.field_1724.method_23321();
            String coords = String.format("%.1f / %.1f / %.1f", tmp0);
            int bw = tr.method_1727(coords) + 14;
            int bh = 14;
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 5f, -652993496, false);
            RenderUtil.drawOutline(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 5f, 1f, -869253035, false);
            context.method_51433(tr, coords, pos[0] + 7, pos[1] + 3, -3090200, false);
        }
        if (((Boolean)INSTANCE.info.getValue()).booleanValue()) {
            int[] pos = Hud.getElementPos(HudElement.INFO);
            int fps = mc.method_47599();
            int ping = 0;
        }
        try {
            if (mc.method_1562() != null) {
                if (mc.field_1724 != null) {
                    class_640 entry = mc.method_1562().method_2871(mc.field_1724.method_5667());
                    if (entry != null) {
                        ping = entry.method_2959();
                    }
                }
            }
        }
        catch (Exception time) {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String infoText = fps + " FPS | " + ping + "ms | " + time;
            int bw = tr.method_1727(infoText) + 14;
            int bh = 14;
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 5f, -652993496, false);
            RenderUtil.drawOutline(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 5f, 1f, -869253035, false);
            context.method_51433(tr, infoText, pos[0] + 7, pos[1] + 3, -3090200, false);
            if (!((Boolean)INSTANCE.potionEffects.getValue()).booleanValue()) { /* goto @877; */ }
            List<class_1293> effects = new ArrayList(mc.field_1724.method_6026());
            if (!(effects.isEmpty())) {
                effects.sort(Comparator.comparingInt(Hud::lambda$renderHud$2 /* captured: tr */));
            }
            int[] pos = Hud.getElementPos(HudElement.POTION_EFFECTS);
            int by = pos[1];
            for (class_1293 eff : effects) {
                String name = Hud.getEffectName(eff);
                int bw = tr.method_1727(name) + 14;
                int bh = 14;
                int color = ((class_1291)eff.method_5579().comp_349()).method_5556();
                int pillColor = -872415232 | color & 16777215;
                RenderUtil.drawRoundedRect(context, (float)pos[0], (float)by, (float)bw, (float)bh, 5f, -652993496, false);
                RenderUtil.drawOutline(context, (float)pos[0], (float)by, (float)bw, (float)bh, 5f, 1f, pillColor, false);
                RenderUtil.drawRoundedRect(context, (float)pos[0], (float)by, 2f, (float)bh, 2f, pillColor, false);
                context.method_51433(tr, name, pos[0] + 7, by + 3, -3090200, false);
                by = by + (bh + 3);
            }
        }
        time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        infoText = fps + " FPS | " + ping + "ms | " + time;
        bw = tr.method_1727(infoText) + 14;
        bh = 14;
        RenderUtil.drawRoundedRect(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 5f, -652993496, false);
        RenderUtil.drawOutline(context, (float)pos[0], (float)pos[1], (float)bw, (float)bh, 5f, 1f, -869253035, false);
        context.method_51433(tr, infoText, pos[0] + 7, pos[1] + 3, -3090200, false);
        if ((Boolean)INSTANCE.potionEffects.getValue()).booleanValue() {
            effects = new ArrayList(mc.field_1724.method_6026());
        }
        if (!(effects.isEmpty())) {
            effects.sort(Comparator.comparingInt(Hud::lambda$renderHud$2 /* captured: tr */));
        }
        pos = Hud.getElementPos(HudElement.POTION_EFFECTS);
        by = pos[1];
        for (eff : effects) {
            name = Hud.getEffectName(eff);
            bw = tr.method_1727(name) + 14;
            bh = 14;
            color = ((class_1291)eff.method_5579().comp_349()).method_5556();
            pillColor = -872415232 | color & 16777215;
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)by, (float)bw, (float)bh, 5f, -652993496, false);
            RenderUtil.drawOutline(context, (float)pos[0], (float)by, (float)bw, (float)bh, 5f, 1f, pillColor, false);
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)by, 2f, (float)bh, 2f, pillColor, false);
            context.method_51433(tr, name, pos[0] + 7, by + 3, -3090200, false);
            by = by + (bh + 3);
        }
        if (!((Boolean)INSTANCE.armor.getValue()).booleanValue()) { /* goto L1387; */ }
        int[] pos = Hud.getElementPos(HudElement.ARMOR);
        List<class_1799> pieces = new ArrayList();
        class_1304[] tmp1 = new class_1304[4];
        tmp1[0] = class_1304.field_6169;
        tmp1[1] = class_1304.field_6174;
        tmp1[2] = class_1304.field_6172;
        tmp1[3] = class_1304.field_6166;
        class_1304[] slots = tmp1;
        maxTextW = slots;
        int bw = maxTextW.length;
        for (class_1799 s = 0; s < bw; s++) {
            class_1304 sl = maxTextW[s];
            class_1799 s = mc.field_1724.method_6118(sl);
            if (s != null) {
                if (s.method_7960()) continue;
                pieces.add(s);
            }
        }
        if (!(pieces.isEmpty())) {
            maxTextW = 0;
        }
        for (s : pieces) {
            int pct = s.method_7963() && s.method_7936() > 0 ? (int)Math.round((1 - (double)s.method_7919() / (double)s.method_7936()) * 100) : 100;
            int w = tr.method_1727(pct + "%");
            if (w <= maxTextW) continue;
            maxTextW = w;
        }
        bw = 22 + maxTextW + 6;
        int bh = 18;
        int ay = pos[1];
        int bh = pieces.iterator();
        while (bh.hasNext()) {
            class_1799 s = bh.next();
            int pct = s.method_7963() && s.method_7936() > 0 ? (int)Math.round((1 - (double)s.method_7919() / (double)s.method_7936()) * 100) : 100;
            if (pct >= 66) {
                int barColor = -867508608;
            } else {
                if (pct >= 33) {
                    int barColor = -855978987;
                } else {
                    int barColor = -856734652;
                }
            }
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)ay, (float)bw, (float)bh, 5f, -652993496, false);
            RenderUtil.drawOutline(context, (float)pos[0], (float)ay, (float)bw, (float)bh, 5f, 1f, -869253035, false);
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)ay, 2f, (float)bh, 2f, barColor, false);
            context.method_51427(s, pos[0] + 4, ay + 1);
            String pctStr = pct + "%";
            context.method_51433(tr, pctStr, pos[0] + 22, ay + 5, -3090200, false);
            ay = ay + (bh + 2);
        }
        if (!((Boolean)INSTANCE.keybinds.getValue()).booleanValue()) { /* goto L1591; */ }
        List<Module> bound = Hud.collectBoundModules();
        if (bound.isEmpty()) { /* goto L1591; */ }
        int[] pos = Hud.getElementPos(HudElement.KEYBINDS);
        int by = pos[1];
        for (Module m : bound) {
            String line = Hud.formatBindLine(m);
            int bw = tr.method_1727(line) + 14;
            bh = 14;
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)by, (float)bw, (float)bh, 5f, -652993496, false);
            RenderUtil.drawOutline(context, (float)pos[0], (float)by, (float)bw, (float)bh, 5f, 1f, -869253035, false);
            RenderUtil.drawRoundedRect(context, (float)pos[0], (float)by, 2f, (float)bh, 2f, accent, false);
            context.method_51433(tr, line, pos[0] + 7, by + 3, -3090200, false);
            by = by + (bh + 3);
        }
        if (!((Boolean)INSTANCE.moduleList.getValue()).booleanValue()) { /* goto L1878; */ }
        int[] pos = Hud.getElementPos(HudElement.MODULE_LIST);
        List<Module> enabled = new ArrayList();
        for (m : ModuleManager.INSTANCE.getModules()) {
            if (m.isEnabled()) {
                if (m.getCategory() == Category.CLIENT) continue;
                enabled.add(m);
            }
        }
        enabled.sort(Comparator.comparingInt(Hud::lambda$renderHud$3 /* captured: tr */).reversed());
        ey = pos[1];
        for (Module m : enabled) {
            String label = m.getName();
            int bw = tr.method_1727(label) + 14;
            int bh = 14;
            int bx = pos[0] - bw;
            RenderUtil.drawRoundedRect(context, (float)bx, (float)ey, (float)bw, (float)bh, 6f, -652993496, false);
            RenderUtil.drawOutline(context, (float)bx, (float)ey, (float)bw, (float)bh, 6f, 1f, -869253035, false);
            RenderUtil.drawRoundedRect(context, (float)bx, (float)ey, 2f, (float)bh, 2f, accent, false);
            context.method_51433(tr, label, bx + 6, ey + 3, -854277, false);
            ey = ey + (bh + 4);
        }
    }

    public static String getEffectName(class_1293 eff) {
        String raw = class_7923.field_41174.method_10221((class_1291)eff.method_5579().comp_349()).method_12832();
        String[] parts = raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
                if (p.length() <= 1) continue;
                sb.append(p.substring(1));
                sb.append(' ');
            }
        }
        name = sb.toString().trim();
        amp = eff.method_5578();
        if (amp > 0) {
            name = name + " " + Hud.toRoman(amp + 1);
        }
        ticks = eff.method_5584();
        if (ticks < 32767) {
            int secs = ticks / 20;
            Object[] tmp0 = new Object[2];
            tmp0[0] = secs / 60;
            tmp0[1] = secs % 60;
            name = name + " " + String.format("%d:%02d", tmp0);
        }
        return name;
    }

    private static String toRoman(int n) {
        switch (n) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return String.valueOf(n);
        }
    }
}
