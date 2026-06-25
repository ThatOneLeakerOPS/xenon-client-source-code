/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1299;

public final class AntiTrap
extends Module {
    private final Setting<Boolean> armorStands = new Setting("Armor Stands", true);
    private final Setting<Boolean> minecarts = new Setting("Minecarts", true);
    private final Setting<Boolean> chestMinecarts = new Setting("Chest Minecarts", true);
    private final Setting<Boolean> hopperMinecarts = new Setting("Hopper Minecarts", true);

    public AntiTrap() {
        super("AntiTrap", Category.DONUT);
        this.addSetting(this.armorStands);
        this.addSetting(this.minecarts);
        this.addSetting(this.chestMinecarts);
        this.addSetting(this.hopperMinecarts);
    }

    public void onEnable() {
        this.removeTrapEntities();
    }

    public void onTick() {
        this.removeTrapEntities();
    }

    private void removeTrapEntities() {
        if (mc.field_1687 == null) {
            return;
        }
        List<class_1297> trapEntities = new ArrayList();
        mc.field_1687.method_18112().forEach(this::lambda$removeTrapEntities$0 /* captured: trapEntities */);
        trapEntities.forEach(AntiTrap::lambda$removeTrapEntities$1);
    }

    private boolean isTrapEntity(class_1299<?> type) {
        if (type == null) {
            return false;
        }
        if (((Boolean)this.armorStands.getValue()).booleanValue() && type.equals(class_1299.field_6131)) {
            return true;
        }
        if (((Boolean)this.minecarts.getValue()).booleanValue() && type.equals(class_1299.field_6096)) {
            return true;
        }
        if (((Boolean)this.chestMinecarts.getValue()).booleanValue() && type.equals(class_1299.field_6126)) {
            return true;
        }
        if (((Boolean)this.hopperMinecarts.getValue()).booleanValue() && type.equals(class_1299.field_6058)) {
            return true;
        }
        return false;
    }
}
