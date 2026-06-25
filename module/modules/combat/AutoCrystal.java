/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.ActivatableModule;
import com.xenon.module.Category;
import com.xenon.setting.Setting;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import org.lwjgl.glfw.GLFW;

public final class AutoCrystal
extends ActivatableModule {
    private static final double RANGE = 5;
    private final Setting<Float> placeDelay = new Setting("Place Delay", 0f, 0f, 20f);
    private final Setting<Float> breakDelay = new Setting("Break Delay", 0f, 0f, 20f);
    private int placeDelayCounter;
    private int breakDelayCounter;

    public AutoCrystal() {
        super("Auto Crystal", Category.COMBAT);
        this.addSetting(this.placeDelay);
        this.addSetting(this.breakDelay);
    }

    public void onEnable() {
        this.resetCounters();
        super.onEnable();
    }

    public void onTick() {
        if (mc.field_1755 != null) {
            return;
        }
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        this.updateCounters();
        if (!this.isRightClickHeld()) {
            this.resetCounters();
            return;
        }
        class_1297 nearestCrystal = null;
        double minDistance = 179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000;
        for (class_1297 e : mc.field_1687.method_18112()) {
            if (e instanceof class_1511) {
                double d = mc.field_1724.method_5739(e);
                if (d <= 5) {
                    if (d < minDistance) {
                        minDistance = d;
                        nearestCrystal = e;
                    }
                }
            }
        }
        mc.field_1761.method_2918(mc.field_1724, nearestCrystal);
        if (nearestCrystal != null && this.breakDelayCounter == 0) {
            mc.field_1724.method_6104(class_1268.field_5808);
            this.breakDelayCounter = Math.max(0, ((Float)this.breakDelay.getValue()).intValue());
        }
        class_2338 pos = mc.field_1765;
        if (pos instanceof class_3965) {
            blockHitResult = (class_3965)pos;
        } else {
            return;
        }
        if (blockHitResult.method_17783() != class_239.class_240.field_1332) {
            return;
        }
        pos = blockHitResult.method_17777();
        if (mc.field_1724.method_5707(pos.method_46558()) > 25) {
            return;
        }
        if (!this.isValidCrystalPlacement(pos)) {
            return;
        }
        mc.field_1690.field_1904.method_23481(false);
        int crystalSlot = this.findHotbarSlot(class_1802.field_8301);
        if (crystalSlot == -1) {
            return;
        }
        if (mc.field_1724.method_6047().method_7909() != class_1802.field_8301) {
            mc.field_1724.method_31548().method_61496(crystalSlot);
        }
        if (this.placeDelayCounter == 0) {
            this.interactWithBlock(blockHitResult);
            this.placeDelayCounter = Math.max(0, ((Float)this.placeDelay.getValue()).intValue());
        }
    }

    private boolean isRightClickHeld() {
        return mc.method_22683() != null && GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 1) == 1;
    }

    private void resetCounters() {
        this.placeDelayCounter = 0;
        this.breakDelayCounter = 0;
    }

    private void updateCounters() {
        if (this.placeDelayCounter > 0) {
            this.placeDelayCounter = this.placeDelayCounter - 1;
        }
        if (this.breakDelayCounter > 0) {
            this.breakDelayCounter = this.breakDelayCounter - 1;
        }
    }

    private int findHotbarSlot(class_1792 item) {
        for (int i = 0; i < 9; i++) {
            if (!mc.field_1724.method_31548().method_5438(i).method_31574(item)) continue;
            return i;
        }
        return -1;
    }

    private boolean isValidCrystalPlacement(class_2338 blockPos) {
        if (!mc.field_1687.method_8320(blockPos).method_27852(class_2246.field_10540) && !mc.field_1687.method_8320(blockPos).method_27852(class_2246.field_9987)) {
            return false;
        }
        class_2338 up = blockPos.method_10084();
        if (!mc.field_1687.method_22347(up)) {
            return false;
        }
        int getX = up.method_10263();
        int getY = up.method_10264();
        int compareTo = up.method_10260();
        class_238 box = new class_238((double)getX, (double)getY, (double)compareTo, (double)getX + 1, (double)getY + 2, (double)compareTo + 1);
        return mc.field_1687.method_8335(null, box).isEmpty();
    }

    private void interactWithBlock(class_3965 hit) {
        mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hit);
        mc.field_1724.method_6104(class_1268.field_5808);
    }
}
