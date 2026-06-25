/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.combat;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import java.util.Iterator;
import net.minecraft.class_1661;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_9304;
import net.minecraft.class_9334;

public final class SpearSwap
extends Module {
    public static SpearSwap INSTANCE;
    private final Setting<Boolean> lunge = new Setting("Lunge", true);
    private final Setting<Boolean> sharpness = new Setting("Sharpness", false);
    private final Setting<Boolean> onlySword = new Setting("Only Sword", false);
    private final Setting<Boolean> onlyAxe = new Setting("Only Axe", false);
    private final Setting<Boolean> switchBack = new Setting("Switch Back", true);
    private final Setting<Float> switchDelay = new Setting("Switch Delay", 1f, 1f, 20f);
    private int previousSlot = -1;
    private int countdown = 0;
    private boolean attackHeldLastCheck = false;

    public SpearSwap() {
        super("SpearSwap", Category.COMBAT);
        this.addSetting(this.lunge);
        this.addSetting(this.sharpness);
        this.addSetting(this.onlySword);
        this.addSetting(this.onlyAxe);
        this.addSetting(this.switchBack);
        this.addSetting(this.switchDelay);
        INSTANCE = this;
    }

    public void preAttack() {
        if (mc.field_1724 == null) {
            return;
        }
        boolean wasHeld = this.attackHeldLastCheck;
        this.attackHeldLastCheck = true;
        if (wasHeld) {
            return;
        }
        if (this.countdown > 0) {
            return;
        }
        class_1661 inv = mc.field_1724.method_31548();
        int slot = this.findBestWeaponSlot();
        if (slot < 0) {
            return;
        }
        if (inv.method_67532() == slot) {
            return;
        }
        this.previousSlot = inv.method_67532();
        inv.method_61496(slot);
        this.countdown = Math.max(1, ((Float)this.switchDelay.getValue()).intValue());
    }

    public void noAttack() {
        this.attackHeldLastCheck = false;
    }

    public void onEnable() {
        this.previousSlot = -1;
        this.countdown = 0;
    }

    public void onDisable() {
        this.previousSlot = -1;
        this.countdown = 0;
    }

    public void onTick() {
        if (mc.field_1724 == null || mc.field_1690 == null) {
            return;
        }
        class_1661 inv = mc.field_1724.method_31548();
        if (this.countdown > 0) {
            this.countdown = this.countdown - 1;
            if (this.countdown == 0) {
                if (((Boolean)this.switchBack.getValue()).booleanValue()) {
                    if (this.previousSlot >= 0) {
                        if (this.previousSlot < 9) {
                            if (inv.method_67532() != this.previousSlot) {
                                inv.method_61496(this.previousSlot);
                            }
                        }
                    }
                }
                this.previousSlot = -1;
            }
        }
        if (mc.field_1690.field_1886.method_1434()) {
            this.preAttack();
        } else {
            this.noAttack();
        }
    }

    private int findBestWeaponSlot() {
        class_1661 inv = mc.field_1724.method_31548();
        boolean swordOnly = ((Boolean)this.onlySword.getValue()).booleanValue();
        boolean axeOnly = ((Boolean)this.onlyAxe.getValue()).booleanValue();
        boolean wantLunge = ((Boolean)this.lunge.getValue()).booleanValue();
        boolean wantSharp = ((Boolean)this.sharpness.getValue()).booleanValue();
        int bestSlot = -1;
        int bestScore = -2147483648;
        for (int i = 0; i < 9; i++) {
            class_1799 s = inv.method_5438(i);
            if (!(s.method_7960())) {
                String path = class_7923.field_41178.method_10221(s.method_7909()).method_12832();
                String displayName = "";
            }
            try {
                displayName = s.method_7964().getString().toLowerCase();
            }
            catch (Throwable isSword) {
                isSword = path.endsWith("_sword");
                boolean isAxe = s.method_7909() instanceof class_1743;
                if (s.method_7909() != class_1802.field_8547) {
                    if (s.method_7909() != class_1802.field_49814) {
                        if (path.contains("spear")) { /* goto @207; */ }
                    }
                }
            }
            isSword = path.endsWith("_sword");
            isAxe = s.method_7909() instanceof class_1743;
            boolean isSpear = s.method_7909() == class_1802.field_8547 || s.method_7909() == class_1802.field_49814 || path.contains("spear") || displayName.contains("spear");
            boolean hasLunge = this.hasLungeEnchant(s);
            boolean lungeItem = wantLunge && hasLunge;
            if (!(swordOnly)) {
                if (!(axeOnly)) {
                    if (!swordOnly) {
                        if (!axeOnly) {
                            if (!isSword) {
                                if (!isAxe) {
                                    if (isSpear) { /* goto @294; */ }
                                }
                            }
                        }
                    } else {
                        int score = 0;
                        if (!lungeItem) continue;
                        score += 500;
                        if (isSpear) {
                            score += 300;
                        } else {
                            if (isSword) {
                                score += 200;
                            } else {
                                if (!isAxe) continue;
                                score += 100;
                            }
                        }
                        if (!wantSharp) continue;
                        score = score + this.sharpnessLevel(s) * 60;
                        if (score > bestScore) {
                            bestScore = score;
                            bestSlot = i;
                        }
                    }
                }
            }
        }
        return bestSlot;
    }

    private boolean hasLungeEnchant(class_1799 s) {
        try {
            class_9304 comp = s.method_58694(class_9334.field_49633);
            if (comp == null) {
                return false;
            }
        }
        catch (Throwable comp) {
            return false;
        }
        try {
            Iterator var3 = comp.method_57534().iterator();
            if (var3.hasNext()) {
                class_6880<class_1887> entry = var3.next();
                class_5321<class_1887> key = entry.method_40230().orElse(null);
                if (key != null) {
                    if (key.equals(class_1893.field_50159) || key.equals(class_1893.field_50157) || key.equals(class_1893.field_9104)) {
                        return true;
                    }
                }
            }
        }
        catch (Throwable comp) {
            return false;
        }
        try {
            String idPath = key.method_29177().toString().toLowerCase();
            if (idPath.contains("lunge")) {
                return true;
            }
        }
        catch (Throwable comp) {
            return false;
        }
        try {
            /* goto @27; */
        }
        catch (Throwable comp) {
            return false;
        }
        return false;
    }

    private int sharpnessLevel(class_1799 s) {
        try {
            class_9304 comp = s.method_58694(class_9334.field_49633);
            if (comp == null) {
                return 0;
            }
        }
        catch (Throwable comp) {
            return 0;
        }
        try {
            Iterator var3 = comp.method_57534().iterator();
            if (var3.hasNext()) {
                class_6880<class_1887> entry = var3.next();
                class_5321<class_1887> key = entry.method_40230().orElse(null);
                if (key != null) {
                    if (key.equals(class_1893.field_9118)) {
                        return comp.method_57536(entry);
                    }
                }
            }
        }
        catch (Throwable comp) {
            return 0;
        }
        try {
            /* goto @27; */
        }
        catch (Throwable comp) {
            return 0;
        }
        return 0;
    }
}
