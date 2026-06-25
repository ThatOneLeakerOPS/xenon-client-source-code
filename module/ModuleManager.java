/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module;

import com.xenon.module.ActivatableModule;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.StubModule;
import com.xenon.module.modules.client.CloudConfigs;
import com.xenon.module.modules.client.Friends;
import com.xenon.module.modules.client.Hud;
import com.xenon.module.modules.client.SpotifyHud;
import com.xenon.module.modules.client.Themes;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.module.modules.combat.AnchorMacro;
import com.xenon.module.modules.combat.AutoCrystal;
import com.xenon.module.modules.combat.AutoDoubleHand;
import com.xenon.module.modules.combat.AutoInvTotem;
import com.xenon.module.modules.combat.AutoTotem;
import com.xenon.module.modules.combat.DoubleAnchor;
import com.xenon.module.modules.combat.Hitbox;
import com.xenon.module.modules.combat.HoverTotem;
import com.xenon.module.modules.combat.ShieldBreaker;
import com.xenon.module.modules.combat.SpearSwap;
import com.xenon.module.modules.combat.Triggerbot;
import com.xenon.module.modules.donut.ActivityDebug;
import com.xenon.module.modules.donut.AntiTrap;
import com.xenon.module.modules.donut.BoneDropperBot;
import com.xenon.module.modules.donut.ChunkFinder;
import com.xenon.module.modules.donut.FakeRoles;
import com.xenon.module.modules.donut.FakeStats;
import com.xenon.module.modules.donut.GrowthFinder;
import com.xenon.module.modules.donut.SpawnerProtect;
import com.xenon.module.modules.misc.AutoLog;
import com.xenon.module.modules.misc.AutoTool;
import com.xenon.module.modules.misc.CoordSnapper;
import com.xenon.module.modules.misc.FastPlace;
import com.xenon.module.modules.misc.Freelook;
import com.xenon.module.modules.misc.HomeSetter;
import com.xenon.module.modules.misc.NameProtect;
import com.xenon.module.modules.misc.NameTags;
import com.xenon.module.modules.misc.SkinChanger;
import com.xenon.module.modules.misc.Sprint;
import com.xenon.module.modules.misc.SwingSpeed;
import com.xenon.module.modules.misc.TabDetector;
import com.xenon.module.modules.misc.WeatherNotifier;
import com.xenon.module.modules.render.AmethystESP;
import com.xenon.module.modules.render.BlockESP;
import com.xenon.module.modules.render.Freecam;
import com.xenon.module.modules.render.FullBright;
import com.xenon.module.modules.render.HoleESP;
import com.xenon.module.modules.render.JumpCircles;
import com.xenon.module.modules.render.LightDebug;
import com.xenon.module.modules.render.MobESP;
import com.xenon.module.modules.render.NoRender;
import com.xenon.module.modules.render.PlayerESP;
import com.xenon.module.modules.render.StorageESP;
import com.xenon.setting.BlocksSetting;
import com.xenon.setting.Setting;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_2248;
import net.minecraft.class_2596;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_7923;

public class ModuleManager {
    private static final String CONFIG_HEADER = "XENON_CONFIG_V2";
    private static final String MODULE_PREFIX = "MODULE";
    private static final String SETTING_PREFIX = "SETTING";
    private static final String HUDPOS_PREFIX = "HUDPOS";
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList();
    private boolean initialized = false;
    private boolean loadingConfig = false;

    public void init() {
        if (this.initialized) {
            return;
        }
        this.modules.add(new XenonPlus());
        this.modules.add(new Themes());
        this.modules.add(new Hud());
        this.modules.add(new SpotifyHud());
        this.modules.add(new Friends());
        this.modules.add(new CloudConfigs());
        this.modules.add(new PlayerESP());
        this.modules.add(new BlockESP());
        this.modules.add(new AmethystESP());
        this.modules.add(new MobESP());
        this.modules.add(new StorageESP());
        this.modules.add(new Freecam());
        this.modules.add(new LightDebug());
        this.modules.add(new HoleESP());
        this.modules.add(new FullBright());
        this.modules.add(new NoRender());
        this.modules.add(new JumpCircles());
        this.modules.add(new StubModule("Elytra Swap", Category.COMBAT));
        this.modules.add(new StubModule("Mace Swap", Category.COMBAT));
        this.modules.add(new AutoTotem());
        this.modules.add(new HoverTotem());
        this.modules.add(new AutoInvTotem());
        this.modules.add(new Hitbox());
        this.modules.add(new AnchorMacro());
        this.modules.add(new AutoCrystal());
        this.modules.add(new DoubleAnchor());
        this.modules.add(new Triggerbot());
        this.modules.add(new ShieldBreaker());
        this.modules.add(new AutoDoubleHand());
        this.modules.add(new SpearSwap());
        this.modules.add(new NameProtect());
        this.modules.add(new NameTags());
        this.modules.add(new CoordSnapper());
        this.modules.add(new HomeSetter());
        this.modules.add(new SwingSpeed());
        this.modules.add(new Freelook());
        this.modules.add(new FastPlace());
        this.modules.add(new AutoTool());
        this.modules.add(new AutoLog());
        this.modules.add(new SkinChanger());
        this.modules.add(new Sprint());
        this.modules.add(new TabDetector());
        this.modules.add(new WeatherNotifier());
        this.modules.add(new ActivityDebug());
        this.modules.add(new FakeStats());
        this.modules.add(new FakeRoles());
        this.modules.add(new ChunkFinder());
        this.modules.add(new GrowthFinder());
        this.modules.add(new AntiTrap());
        this.modules.add(new BoneDropperBot());
        this.modules.add(new SpawnerProtect());
        this.initialized = true;
        this.loadConfig();
    }

    public void onSettingChanged() {
        if (!this.initialized || this.loadingConfig) {
            return;
        }
        this.saveConfig();
    }

    public void saveConfig() {
        if (!this.initialized || this.loadingConfig) {
            return;
        }
        Path configFile = this.getConfigPath();
        Files.createDirectories(configFile.getParent(), new FileAttribute[0]);
        BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8, new OpenOption[0]);
        writer.write("XENON_CONFIG_V2");
        writer.newLine();
        for (Module module : this.modules) {
            writer.write("MODULE");
            writer.write(9);
            writer.write(this.encode(module.getName()));
            writer.write(9);
            writer.write(Integer.toString(module.getBind()));
            writer.write(9);
            ActivatableModule activatableModule = module;
            int activationKey = module instanceof ActivatableModule ? activatableModule.getActivationKey() : 0;
            writer.write(Integer.toString(activationKey));
            writer.write(9);
            writer.write(Boolean.toString(module.isEnabled()));
            writer.newLine();
            for (Setting<?> setting : module.getSettings()) {
                String serialized = this.serializeSettingValue(setting);
                while (serialized == null) {
                }
                writer.write("SETTING");
                writer.write(9);
                writer.write(this.encode(module.getName()));
                writer.write(9);
                writer.write(this.encode(setting.getName()));
                writer.write(9);
                writer.write(this.encode(serialized));
                writer.newLine();
            }
        }
        for (el : Hud.HudElement.values()) {
            int[] pos = Hud.getElementPos(el);
            writer.write("HUDPOS");
            writer.write(9);
            writer.write(el.name());
            writer.write(9);
            writer.write(Integer.toString(pos[0]));
            writer.write(9);
            writer.write(Integer.toString(pos[1]));
            writer.newLine();
        }
        if (writer != null) {
            writer.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadConfig() {
        Path configFile = this.getConfigPath();
        if (!Files.exists(configFile, new LinkOption[0])) {
            return;
        }
        this.loadingConfig = true;
        try {
            List<String> lines = Files.readAllLines(configFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null) continue;
                if (line.isBlank()) continue;
                while ("XENON_CONFIG_V2".equals(line)) {
                }
                while (line.startsWith("MODULE\t")) {
                    this.loadModuleLine(line);
                }
                while (line.startsWith("SETTING\t")) {
                    this.loadSettingLine(line);
                }
                while (line.startsWith("HUDPOS\t")) {
                    this.loadHudPosLine(line);
                }
                this.loadLegacyModuleLine(line);
            }
        }
        catch (IOException lines) {
            this.loadingConfig = false;
        }
        finally {
            this.loadingConfig = false;
            throw var5;
        }
    }

    private void loadHudPosLine(String line) {
        try {
            String[] parts = line.split("\t");
            if (parts.length < 4) {
                return;
            }
        }
        catch (Exception parts) {
            return;
        }
        try {
            HudElement el = Hud.HudElement.valueOf(parts[1]);
            int x = Integer.parseInt(parts[2]);
            int y = Integer.parseInt(parts[3]);
            Hud.setElementPos(el, x, y);
        }
        catch (Exception parts) {
            return;
        }
    }

    public boolean isLoadingConfig() {
        return this.loadingConfig;
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public List<Module> getModulesInCategory(Category category) {
        List<Module> categoryModules = new ArrayList();
        for (Module module : this.modules) {
            if (module.getCategory() != category) continue;
            categoryModules.add(module);
        }
        return categoryModules;
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public void onTick() {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.onTick();
        }
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.onRender(matrices, tickDelta);
        }
    }

    public void onPacketReceive(class_2596<?> packet) {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.onPacketReceive(packet);
        }
    }

    public boolean onPacketSend(class_2596<?> packet) {
        boolean cancel = false;
        Iterator var3 = this.modules.iterator();
        if (!var3.hasNext()) return cancel;
        Module module = var3.next();
        while (!module.isEnabled()) {
        }
        try {
            cancel = cancel | module.onPacketSend(packet);
        }
        catch (Exception e5) {
            /* goto @12; */
            return cancel;
        }
        /* goto @12; */
        return cancel;
    }

    private Path getConfigPath() {
        return class_310.method_1551().field_1697.toPath().resolve("xenon_config.txt");
    }

    private void loadModuleLine(String line) {
        String[] parts = line.split("\t", 5);
        if (parts.length < 5) {
            return;
        }
        Module module = this.getModuleByName(this.decode(parts[1]));
        if (module == null) {
            return;
        }
        try {
            module.applyBind(Integer.parseInt(parts[2]));
            if (module instanceof ActivatableModule) {
                ActivatableModule activatableModule = module;
                activatableModule.applyActivationKey(Integer.parseInt(parts[3]));
            }
            module.applyEnabled(Boolean.parseBoolean(parts[4]));
        }
        catch (Exception activatableModule) {
            return;
        }
    }

    private void loadSettingLine(String line) {
        String[] parts = line.split("\t", 4);
        if (parts.length < 4) {
            return;
        }
        Module module = this.getModuleByName(this.decode(parts[1]));
        if (module == null) {
            return;
        }
        Setting<?> setting = this.getSettingByName(module, this.decode(parts[2]));
        if (setting == null) {
            return;
        }
        this.applySettingValue(setting, this.decode(parts[3]));
    }

    private void loadLegacyModuleLine(String line) {
        String[] parts = line.split(":", 4);
        if (parts.length < 2) {
            return;
        }
        Module module = this.getModuleByName(parts[0]);
        if (module == null) {
            return;
        }
        try {
            if (parts.length >= 2) {
                module.applyBind(Integer.parseInt(parts[1]));
            }
            ActivatableModule activatableModule = module;
            if (parts.length >= 3 && module instanceof ActivatableModule) {
                activatableModule.applyActivationKey(Integer.parseInt(parts[2]));
            }
            if (parts.length >= 4) {
                module.applyEnabled(Boolean.parseBoolean(parts[3]));
            }
        }
        catch (Exception activatableModule) {
            return;
        }
    }

    private Setting<?> getSettingByName(Module module, String settingName) {
        for (Setting<?> setting : module.getSettings()) {
            if (!setting.matchesName(settingName)) continue;
            return setting;
        }
        return null;
    }

    private String serializeSettingValue(Setting<?> setting) {
        Object value = setting.getValue();
        if (setting instanceof BlocksSetting) {
            BlocksSetting blocksSetting = setting;
            return this.serializeBlocks(blocksSetting);
        }
        if (value instanceof Boolean) {
            Boolean boolValue = value;
            return Boolean.toString(boolValue.booleanValue());
        }
        if (value instanceof Float) {
            Float floatValue = value;
            return Float.toString(floatValue.floatValue());
        }
        if (value instanceof Integer) {
            Integer intValue = value;
            return Integer.toString(intValue.intValue());
        }
        if (value instanceof Double) {
            Double doubleValue = value;
            return Double.toString(doubleValue.doubleValue());
        }
        if (value instanceof String) {
            String stringValue = value;
            return stringValue;
        }
        if (value instanceof Color) {
            Color colorValue = value;
            return colorValue.getRed() + "," + colorValue.getGreen() + "," + colorValue.getBlue() + "," + colorValue.getAlpha();
        }
        return null;
    }

    private void applySettingValue(Setting<?> setting, String serialized) {
        Object value = setting.getValue();
        try {
            if (setting instanceof BlocksSetting) {
                BlocksSetting blocksSetting = setting;
                blocksSetting.setValue(this.deserializeBlocks(serialized));
                return;
            }
        }
        catch (Exception colorParts) {
            return;
        }
        try {
            if (value instanceof Boolean) {
                setting.setValue(Boolean.parseBoolean(serialized));
                return;
            }
        }
        catch (Exception colorParts) {
            return;
        }
        try {
            if (value instanceof Float) {
                setting.setValue(Float.parseFloat(serialized));
                return;
            }
        }
        catch (Exception colorParts) {
            return;
        }
        try {
            if (value instanceof Integer) {
                parsed = Math.round(Float.parseFloat(serialized));
                setting.setValue(parsed);
                return;
            }
        }
        catch (Exception colorParts) {
            return;
        }
        try {
            if (value instanceof Double) {
                setting.setValue(Double.parseDouble(serialized));
                return;
            }
        }
        catch (Exception colorParts) {
            return;
        }
        try {
            if (value instanceof String) {
                setting.setValue(serialized);
                return;
            }
        }
        catch (Exception colorParts) {
            return;
        }
        try {
            if (value instanceof Color) {
                colorParts = serialized.split(",", 4);
                if (colorParts.length == 4) {
                    Color color = new Color(Integer.parseInt(colorParts[0]), Integer.parseInt(colorParts[1]), Integer.parseInt(colorParts[2]), Integer.parseInt(colorParts[3]));
                    setting.setValue(color);
                }
            }
        }
        catch (Exception colorParts) {
            return;
        }
    }

    private String serializeBlocks(BlocksSetting setting) {
        StringBuilder builder = new StringBuilder();
        for (class_2248 block : setting.getSelectedBlocks()) {
            class_2960 id = class_7923.field_41175.method_10221(block);
            while (id == null) {
            }
            if (builder.isEmpty()) continue;
            builder.append(',');
            builder.append(id);
        }
        return builder.toString();
    }

    private Set<class_2248> deserializeBlocks(String serialized) {
        LinkedHashSet<class_2248> blocks = new LinkedHashSet();
        if (serialized == null || serialized.isBlank()) {
            return blocks;
        }
        for (String rawId : serialized.split(",")) {
            String blockId = rawId.trim();
            if (!(blockId.isEmpty())) {
                class_2960 identifier = class_2960.method_12829(blockId);
                if (identifier != null) {
                    class_2248 block = class_7923.field_41175.method_63535(identifier);
                    if (block == null) continue;
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String decode(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        }
        catch (IllegalArgumentException ignored) {
            return value;
        }
    }
}
