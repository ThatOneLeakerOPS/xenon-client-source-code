/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.misc;

import com.xenon.module.Category;
import com.xenon.module.Module;

public final class Sprint
extends Module {
    private boolean hadSprintToggled = false;

    public Sprint() {
        super("Sprint", Category.MISC);
    }

    public void onEnable() {
        if (mc == null || mc.field_1690 == null) {
            return;
        }
        this.hadSprintToggled = this.getSprintToggledOption();
        this.setSprintToggledOption(false);
    }

    public void onDisable() {
        if (mc == null || mc.field_1690 == null) {
            return;
        }
        this.setSprintToggledOption(this.hadSprintToggled);
        try {
            mc.field_1690.field_1867.method_23481(false);
        }
        catch (Throwable e1) {
            return;
        }
    }

    public void onTick() {
        if (mc == null || mc.field_1724 == null || mc.field_1690 == null) {
            return;
        }
        this.setSprintToggledOption(false);
        try {
            mc.field_1690.field_1867.method_23481(true);
        }
        catch (Throwable e1) {
            return;
        }
    }

    private boolean getSprintToggledOption() {
        try {
            Object opt = mc.field_1690.getClass().getMethod("getSprintToggled", new Class[0]).invoke(mc.field_1690, new Object[0]);
            if (opt == null) {
                return false;
            }
        }
        catch (Throwable ignored) {
            return false;
        }
        try {
            Object v = opt.getClass().getMethod("getValue", new Class[0]).invoke(opt, new Object[0]);
            Boolean b = v;
            if (b.booleanValue()) {
                return v instanceof Boolean;
            }
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private void setSprintToggledOption(boolean value) {
        try {
            Object opt = mc.field_1690.getClass().getMethod("getSprintToggled", new Class[0]).invoke(mc.field_1690, new Object[0]);
            if (opt == null) {
                return;
            }
        }
        catch (Throwable opt) {
            return;
        }
        try {
            Class[] tmp0 = new Class[1];
            tmp0[0] = Object.class;
            Object[] tmp1 = new Object[1];
            tmp1[0] = value;
            opt.getClass().getMethod("setValue", tmp0).invoke(opt, tmp1);
        }
        catch (Throwable opt) {
            return;
        }
    }
}
