/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module;

import com.xenon.gui.notification.NotificationManager;
import com.xenon.module.Category;
import com.xenon.module.ModuleManager;
import com.xenon.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2596;
import net.minecraft.class_310;
import net.minecraft.class_4587;

public abstract class Module {
    private final String name;
    private final Category category;
    private boolean enabled;
    private int bind = 0;
    private boolean expanded = false;
    public boolean wasBindPressed = false;
    private final List<Setting<?>> settings = new ArrayList();
    protected static final class_310 mc = class_310.method_1551();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
        this.enabled = false;
    }

    public void addSetting(Setting<?> setting) {
        this.settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
        ModuleManager.INSTANCE.saveConfig();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void toggle() {
        this.setEnabled(!this.enabled);
        NotificationManager.INSTANCE.pushToggle(this.name, this.enabled, this.category.getIcon());
    }

    public void onBindPressed() {
        this.toggle();
    }

    public int getBind() {
        return this.bind;
    }

    public void setBind(int bind) {
        this.bind = bind;
        ModuleManager.INSTANCE.saveConfig();
    }

    void applyBind(int bind) {
        this.bind = bind;
    }

    void applyEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onRender(class_4587 matrices, float tickDelta) {
    }

    public void onPacketReceive(class_2596<?> packet) {
    }

    public boolean onPacketSend(class_2596<?> packet) {
        return false;
    }
}
