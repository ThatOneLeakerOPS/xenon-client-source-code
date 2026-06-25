/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.ActivatableModule;
import com.xenon.module.Category;
import com.xenon.setting.ModeSetting;
import com.xenon.setting.Setting;
import net.minecraft.class_3532;
import net.minecraft.class_5498;
import org.lwjgl.glfw.GLFW;

public final class Freelook
extends ActivatableModule {
    private static final float MIN_DISTANCE = 1f;
    private static final float MAX_DISTANCE = 15f;
    private static final float MIN_SENSITIVITY = 0.1f;
    private static final float MAX_SENSITIVITY = 3f;
    public static Freelook instance;
    private final ModeSetting activationMode;
    private final Setting<Float> distance;
    private final Setting<Boolean> wallClip;
    private final Setting<Float> sensitivity;
    private final Setting<Boolean> invertY;
    private boolean active;
    private class_5498 savedPerspective;
    private boolean savedChunkCullingEnabled;
    private float cameraYaw;
    private float cameraPitch;

    public Freelook() {
        super("FreeLook", Category.MISC);
        this.activationMode = new ModeSetting("Mode", "Hold", "Activation Mode", "Hold", "Toggle");
        this.distance = new Setting("Distance", 4f, 1f, 15f);
        this.wallClip = new Setting("Wall Clip", true);
        this.sensitivity = new Setting("Sensitivity", 1f, 0.1f, 3f);
        this.invertY = new Setting("Invert Y", false);
        instance = this;
        this.addSetting(this.activationMode);
        this.addSetting(this.distance);
        this.addSetting(this.wallClip);
        this.addSetting(this.sensitivity);
        this.addSetting(this.invertY);
    }

    public void onEnable() {
        this.active = false;
        this.savedPerspective = null;
    }

    public void onDisable() {
        this.deactivateCamera();
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1690 == null || mc.method_22683() == null) {
            this.deactivateCamera();
            return;
        }
        if (this.isHoldMode()) {
            int key = this.getActivationKey();
            boolean pressed = key != 0 && GLFW.glfwGetKey(mc.method_22683().method_4490(), key) == 1;
            if (pressed) {
                this.activateCamera();
            } else {
                this.deactivateCamera();
            }
        } else {
            if (this.active) {
                this.ensureCameraState();
            }
        }
    }

    public void onActivationKeyPressed() {
        if (!this.isEnabled() || this.isHoldMode()) {
            return;
        }
        if (this.active) {
            this.deactivateCamera();
        } else {
            this.activateCamera();
        }
    }

    public boolean isCameraActive() {
        return this.isEnabled() && this.active && mc.field_1724 != null;
    }

    public void consumeMouseDelta(double deltaX, double deltaY) {
        if (!this.isCameraActive()) {
            return;
        }
        double pitchSign = ((Boolean)this.invertY.getValue()).booleanValue() ? -1 : 1;
        double multiplier = 0.15 * (double)this.getSensitivity();
        this.cameraYaw = class_3532.method_15393(this.cameraYaw + (float)(deltaX * multiplier));
        this.cameraPitch = class_3532.method_15363(this.cameraPitch + (float)(deltaY * multiplier * pitchSign), -90f, 90f);
    }

    public float getCameraYaw() {
        return this.cameraYaw;
    }

    public float getCameraPitch() {
        return this.cameraPitch;
    }

    public float getDistance() {
        return class_3532.method_15363(((Float)this.distance.getValue()).floatValue(), 1f, 15f);
    }

    public boolean shouldWallClip() {
        return ((Boolean)this.wallClip.getValue()).booleanValue();
    }

    public float getSensitivity() {
        return class_3532.method_15363(((Float)this.sensitivity.getValue()).floatValue(), 0.1f, 3f);
    }

    private boolean isHoldMode() {
        return this.activationMode.is("Hold");
    }

    private void activateCamera() {
        if (this.active || mc.field_1724 == null || mc.field_1690 == null) {
            this.ensureCameraState();
            return;
        }
        this.savedPerspective = mc.field_1690.method_31044();
        this.savedChunkCullingEnabled = mc.field_1730;
        mc.field_1690.method_31043(class_5498.field_26665);
        mc.field_1730 = false;
        this.cameraYaw = mc.field_1724.method_36454();
        this.cameraPitch = mc.field_1724.method_36455();
        this.active = true;
    }

    private void ensureCameraState() {
        if (!this.active || mc.field_1690 == null) {
            return;
        }
        if (!mc.field_1690.method_31044().method_31035() && !mc.field_1690.method_31044().method_31034()) {
            return;
        }
        mc.field_1690.method_31043(class_5498.field_26665);
        mc.field_1730 = false;
    }

    private void deactivateCamera() {
        if (!this.active) {
            return;
        }
        this.active = false;
        if (mc.field_1690 != null) {
            mc.field_1690.method_31043(this.savedPerspective == null ? class_5498.field_26664 : this.savedPerspective);
        }
        mc.field_1730 = this.savedChunkCullingEnabled;
    }
}
