/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.ModuleManager;

public abstract class ActivatableModule
extends Module {
    private int activationKey = 0;
    public boolean wasActivationKeyPressed = false;

    public ActivatableModule(String name, Category category) {
        super(name, category);
    }

    public void onActivationKeyPressed() {
        this.toggle();
    }

    public int getActivationKey() {
        return this.activationKey;
    }

    public void setActivationKey(int activationKey) {
        this.activationKey = activationKey;
        ModuleManager.INSTANCE.saveConfig();
    }

    void applyActivationKey(int activationKey) {
        this.activationKey = activationKey;
    }
}
