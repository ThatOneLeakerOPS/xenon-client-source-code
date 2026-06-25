/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_5498;
import org.joml.Vector3d;

public class Freecam
extends Module {
    private static final float MIN_SCROLL_SPEED = 0.1f;
    private static final float MAX_SCROLL_SPEED = 10f;
    private static final float SCROLL_SPEED_STEP = 0.2f;
    public final Vector3d currentPosition = new Vector3d();
    public final Vector3d previousPosition = new Vector3d();
    private final Vector3d velocity = new Vector3d();
    public float yaw;
    public float pitch;
    public float previousYaw;
    public float previousPitch;
    public final Setting<Float> speed = new Setting("Speed", 1f, 0.1f, 10f);
    private boolean smoothing = true;
    private float lookSensitivity = 0.5f;
    private float currentSpeed;
    private class_5498 savedPerspective;
    private boolean savedChunkCullingEnabled;
    private long lastFrameTime;
    private float savedPlayerYaw;
    private float savedPlayerPitch;
    public static Freecam instance;

    public Freecam() {
        super("Freecam", Category.RENDER);
        instance = this;
        this.addSetting(this.speed);
    }

    public void onEnable() {
        if (mc.field_1724 == null || mc.field_1687 == null) {
            this.toggle();
            return;
        }
        this.savedPerspective = mc.field_1690.method_31044();
        this.savedChunkCullingEnabled = mc.field_1730;
        mc.field_1730 = false;
        this.savedPlayerYaw = mc.field_1724.method_36454();
        this.savedPlayerPitch = mc.field_1724.method_36455();
        this.yaw = mc.field_1724.method_36454();
        this.pitch = mc.field_1724.method_36455();
        class_243 eyePos = mc.field_1724.method_5836(1f);
        this.currentPosition.set(eyePos.field_1352, eyePos.field_1351, eyePos.field_1350);
        this.previousPosition.set(eyePos.field_1352, eyePos.field_1351, eyePos.field_1350);
        this.previousYaw = this.yaw;
        this.previousPitch = this.pitch;
        this.lastFrameTime = System.currentTimeMillis();
        this.velocity.set(0, 0, 0);
        this.currentSpeed = this.getConfiguredSpeed();
        mc.field_1724.method_18800(0, mc.field_1724.method_18798().field_1351, 0);
    }

    public void onDisable() {
        if (mc.field_1724 != null) {
            mc.field_1724.method_36456(this.savedPlayerYaw);
            mc.field_1724.method_36457(this.savedPlayerPitch);
            mc.field_1724.method_5847(this.savedPlayerYaw);
            mc.field_1724.method_5636(this.savedPlayerYaw);
        }
        if (this.savedPerspective != null) {
            mc.field_1690.method_31043(this.savedPerspective);
        } else {
            mc.field_1690.method_31043(class_5498.field_26664);
        }
        mc.field_1730 = this.savedChunkCullingEnabled;
        this.velocity.set(0, 0, 0);
        this.currentSpeed = this.getConfiguredSpeed();
    }

    public void onTick() {
        if (mc.field_1724 == null) {
            return;
        }
        mc.field_1724.method_36456(this.savedPlayerYaw);
        mc.field_1724.method_36457(this.savedPlayerPitch);
        mc.field_1724.method_5847(this.savedPlayerYaw);
        mc.field_1724.method_5636(this.savedPlayerYaw);
    }

    public void updateCameraMovement() {
        if (mc.field_1724 == null) {
            return;
        }
        this.previousPosition.set(this.currentPosition);
        this.previousYaw = this.yaw;
        this.previousPitch = this.pitch;
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - this.lastFrameTime) / 1000f;
        this.lastFrameTime = currentTime;
        deltaTime = Math.min(deltaTime, 0.1f);
        if (deltaTime < 0.001f) {
            deltaTime = 0.016f;
        }
        float yawRad = Math.toRadians((double)this.yaw);
        double forwardX = -Math.sin((double)yawRad);
        double forwardZ = Math.cos((double)yawRad);
        double rightX = -Math.cos((double)yawRad);
        double rightZ = -Math.sin((double)yawRad);
        double moveX = 0;
        double moveY = 0;
        double moveZ = 0;
        double moveSpeed = this.currentSpeed * 2;
        if (mc.field_1690 != null) {
            if (mc.field_1690.field_1867.method_1434()) {
                moveSpeed = moveSpeed * 2;
            }
        }
        if (mc.field_1690.field_1894.method_1434()) {
            moveX = moveX + forwardX * moveSpeed;
            moveZ = moveZ + forwardZ * moveSpeed;
        }
        if (mc.field_1690.field_1881.method_1434()) {
            moveX = moveX - forwardX * moveSpeed;
            moveZ = moveZ - forwardZ * moveSpeed;
        }
        if (mc.field_1690.field_1849.method_1434()) {
            moveX = moveX + rightX * moveSpeed;
            moveZ = moveZ + rightZ * moveSpeed;
        }
        if (mc.field_1690.field_1913.method_1434()) {
            moveX = moveX - rightX * moveSpeed;
            moveZ = moveZ - rightZ * moveSpeed;
        }
        if (mc.field_1690.field_1903.method_1434()) {
            moveY = moveY + moveSpeed;
        }
        if (mc.field_1690.field_1832.method_1434()) {
            moveY = moveY - moveSpeed;
        }
        double multiplier = 5;
        if (this.smoothing) {
            double lerpFactor = 1 - Math.pow(0.001, (double)deltaTime);
            this.velocity.x = class_3532.method_16436(lerpFactor, this.velocity.x, moveX * multiplier);
            this.velocity.y = class_3532.method_16436(lerpFactor, this.velocity.y, moveY * multiplier);
            this.velocity.z = class_3532.method_16436(lerpFactor, this.velocity.z, moveZ * multiplier);
        } else {
            this.velocity.set(moveX * multiplier, moveY * multiplier, moveZ * multiplier);
        }
        this.currentPosition.x = this.currentPosition.x + this.velocity.x * (double)deltaTime;
        this.currentPosition.y = this.currentPosition.y + this.velocity.y * (double)deltaTime;
        this.currentPosition.z = this.currentPosition.z + this.velocity.z * (double)deltaTime;
    }

    public void onScrollWheel(double scrollDelta) {
        float current = this.currentSpeed;
        float step = 0.5f;
        float newVal = current + (float)scrollDelta * step;
        this.currentSpeed = class_3532.method_15363(newVal, 0.1f, 10f);
    }

    public void updateRotation(double deltaYaw, double deltaPitch) {
        this.yaw = this.yaw + (float)deltaYaw;
        this.pitch = this.pitch + (float)deltaPitch;
        this.yaw = class_3532.method_15393(this.yaw);
        this.pitch = class_3532.method_15363(this.pitch, -90f, 90f);
    }

    public double getInterpolatedX(float partialTicks) {
        return class_3532.method_16436((double)partialTicks, this.previousPosition.x, this.currentPosition.x);
    }

    public double getInterpolatedY(float partialTicks) {
        return class_3532.method_16436((double)partialTicks, this.previousPosition.y, this.currentPosition.y);
    }

    public double getInterpolatedZ(float partialTicks) {
        return class_3532.method_16436((double)partialTicks, this.previousPosition.z, this.currentPosition.z);
    }

    public float getInterpolatedYaw(float partialTicks) {
        return class_3532.method_16439(partialTicks, this.previousYaw, this.yaw);
    }

    public float getInterpolatedPitch(float partialTicks) {
        return class_3532.method_16439(partialTicks, this.previousPitch, this.pitch);
    }

    public float getLookSensitivity() {
        return this.lookSensitivity;
    }

    public void adjustSpeed(double scrollAmount) {
        if (scrollAmount == 0) {
            return;
        }
        float nextSpeed = this.currentSpeed + (float)Math.signum(scrollAmount) * 0.2f;
        this.currentSpeed = class_3532.method_15363(nextSpeed, 0.1f, 10f);
    }

    private float getConfiguredSpeed() {
        return class_3532.method_15363(((Float)this.speed.getValue()).floatValue(), 0.1f, 10f);
    }
}
