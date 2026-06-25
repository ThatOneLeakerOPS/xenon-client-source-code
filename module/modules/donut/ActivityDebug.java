/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.donut;

import com.xenon.gui.notification.NotificationManager;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_4184;
import net.minecraft.class_4587;

public final class ActivityDebug
extends Module {
    private static final long NOTIFY_COOLDOWN_MS = 1250L;
    private static final double MARKER_SURFACE_Y = 57;
    private static final double MARKER_SURFACE_THICKNESS = 0.05;
    private final Setting<Float> yLevel = new Setting("y-level", 16f, -64f, 320f);
    private final Setting<Boolean> notification = new Setting("Notification", false);
    private final Set<class_1923> susChunks = Collections.newSetFromMap(new ConcurrentHashMap());
    private final Map<Long, Long> lastNotifiedAt = new ConcurrentHashMap();
    private static final Color YELLOW = new Color(255, 220, 0, 180);
    private static final Color YELLOW_OUTLINE = new Color(255, 220, 0, 255);
    private final Map<Class<?>, List<Field>> doubleFields = new ConcurrentHashMap();
    private final Map<Class<?>, List<Field>> nestedFields = new ConcurrentHashMap();
    private final Map<Class<?>, List<Field>> blockPosFields = new ConcurrentHashMap();
    private final Map<Class<?>, List<Field>> vec3Fields = new ConcurrentHashMap();
    private final ThreadLocal<Set<Integer>> exploredObjects;

    public ActivityDebug() {
        super("ActivityDebug", Category.DONUT);
        this.exploredObjects = ThreadLocal.withInitial(ActivityDebug::lambda$new$0);
        this.addSetting(this.yLevel);
        this.addSetting(this.notification);
    }

    public void onDisable() {
        this.susChunks.clear();
        this.lastNotifiedAt.clear();
        this.doubleFields.clear();
        this.nestedFields.clear();
        this.blockPosFields.clear();
        this.vec3Fields.clear();
    }

    public void onPacketReceive(class_2596<?> packet) {
        if (packet instanceof class_2637) {
            class_2637 sectionUpdate = packet;
            sectionUpdate.method_30621(this::lambda$onPacketReceive$1);
            return;
        }
        if (packet instanceof class_2626) {
            class_2626 blockUpdate = packet;
            class_2338 pos = blockUpdate.method_11309();
            this.checkAndAdd((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
            return;
        }
        ((Set)this.exploredObjects.get()).clear();
        this.analyzeObject(packet, 0);
    }

    private void analyzeObject(Object obj, int depth) {
        if (obj == null || depth > 3) {
            return;
        }
        int hash = System.identityHashCode(obj);
        if (!((Set)this.exploredObjects.get()).add(hash)) {
            return;
        }
        Class<?> clazz = obj.getClass();
        this.blockPosFields.computeIfAbsent(clazz, this::lambda$analyzeObject$2);
        this.vec3Fields.computeIfAbsent(clazz, this::lambda$analyzeObject$3);
        this.doubleFields.computeIfAbsent(clazz, this::lambda$analyzeObject$4);
        this.nestedFields.computeIfAbsent(clazz, ActivityDebug::lambda$analyzeObject$5);
        List<Field> dFields = ((List)this.blockPosFields.get(clazz)).iterator();
        if (dFields.hasNext()) {
            Field f = dFields.next();
        }
        try {
            class_2338 bp = f.get(obj);
            if (bp != null) {
                this.checkAndAdd((double)bp.method_10263(), (double)bp.method_10264(), (double)bp.method_10260());
            }
        }
        catch (Exception v) {
            /* goto @136; */
            dFields = ((List)this.vec3Fields.get(clazz)).iterator();
            if (!dFields.hasNext()) { /* goto @290; */ }
            Field f = dFields.next();
            class_243 v = f.get(obj);
            if (v != null) {
                this.checkAndAdd(v.field_1352, v.field_1351, v.field_1350);
            }
        }
        /* goto @136; */
        dFields = ((List)this.vec3Fields.get(clazz)).iterator();
        if (dFields.hasNext()) {
            f = (Field)dFields.next();
        }
        try {
            v = (class_243)f.get(obj);
            if (v != null) {
                this.checkAndAdd(v.field_1352, v.field_1351, v.field_1350);
            }
        }
        catch (Exception f) {
            /* goto @225; */
            dFields = this.doubleFields.get(clazz);
            if (dFields.size() < 3) { /* goto @425; */ }
            double x = ((Field)dFields.get(0)).getDouble(obj);
            double y = ((Field)dFields.get(1)).getDouble(obj);
            double z = ((Field)dFields.get(2)).getDouble(obj);
            if (Math.abs(x) < 30000000) {
                if (Math.abs(z) < 30000000) {
                    if (y > -2048) {
                        if (y < 2048) {
                            this.checkAndAdd(x, y, z);
                        }
                    }
                }
            }
        }
        /* goto @225; */
        dFields = this.doubleFields.get(clazz);
        try {
            x = ((Field)dFields.get(0)).getDouble(obj);
            y = ((Field)dFields.get(1)).getDouble(obj);
            z = ((Field)dFields.get(2)).getDouble(obj);
            if (Math.abs(x) < 30000000) {
                if (Math.abs(z) < 30000000) {
                    if (y > -2048) {
                        if (y < 2048) {
                            this.checkAndAdd(x, y, z);
                        }
                    }
                }
            }
        }
        catch (Exception x) {
            Iterator x = ((List)this.nestedFields.get(clazz)).iterator();
            if (!x.hasNext()) { /* goto @498; */ }
            Field f = x.next();
            Object nestedObj = f.get(obj);
            if (nestedObj != null) {
                this.analyzeObject(nestedObj, depth + 1);
            }
        }
        x = ((List)this.nestedFields.get(clazz)).iterator();
        if (x.hasNext()) {
            f = (Field)x.next();
        }
        try {
            nestedObj = f.get(obj);
            if (nestedObj != null) {
                this.analyzeObject(nestedObj, depth + 1);
            }
        }
        catch (Exception nestedObj) {
            /* goto @446; */
            return;
        }
        /* goto @446; */
    }

    private List<Field> getFieldsOfType(Class<?> clazz, Class<?> type) {
        List<Field> list = new ArrayList();
        while (clazz != null) {
            if (clazz == Object.class) break;
            for (Field f : clazz.getDeclaredFields()) {
                f.setAccessible(true);
                if (Modifier.isStatic(f.getModifiers()) && f.getType() == type) continue;
                list.add(f);
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    private void checkAndAdd(double x, double y, double z) {
        if (mc.field_1724 != null && mc.field_1724.method_23318() < 0) {
            return;
        }
        if (y <= (double)((Float)this.yLevel.getValue()).floatValue()) {
            class_1923 chunkPos = new class_1923((int)Math.floor(x) >> 4, (int)Math.floor(z) >> 4);
            if (this.susChunks.add(chunkPos)) {
                this.maybeNotify(chunkPos, y);
            }
        }
    }

    private void maybeNotify(class_1923 chunkPos, double y) {
        if (!((Boolean)this.notification.getValue()).booleanValue() || mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        long chunkKey = chunkPos.method_8324();
        long now = System.currentTimeMillis();
        long last = ((Long)this.lastNotifiedAt.getOrDefault(chunkKey, 0L)).longValue();
        if (now - last < 1250L) {
            return;
        }
        this.lastNotifiedAt.put(chunkKey, now);
        NotificationManager.INSTANCE.push("Activity detected", "Chunk " + chunkPos.field_9181 + ", " + chunkPos.field_9180 + "  Y " + (int)Math.floor(y), new class_1799(class_1802.field_8251), YELLOW_OUTLINE.getRGB());
        mc.field_1687.method_43128(mc.field_1724, mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), class_3417.field_14627, class_3419.field_15250, 0.6f, 1.05f);
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null) {
            return;
        }
        class_4184 camera = RenderUtils.getCamera();
        if (camera == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(camera);
        double markerBaseY = Math.max((double)mc.field_1687.method_31607(), Math.min(57, (double)mc.field_1687.method_31600()));
        WorldBatch batch = RenderUtils.beginWorldBatch(matrices);
        for (class_1923 cp : this.susChunks) {
            while (!RenderUtils.isWorldBoxVisible((double)cp.method_8326(), 57, (double)cp.method_8328(), (double)cp.method_8327(), 57.05, (double)cp.method_8329())) {
            }
            double x1 = cp.method_8326() - camPos.field_1352;
            double z1 = cp.method_8328() - camPos.field_1350;
            double y1 = markerBaseY - camPos.field_1351;
            double x2 = x1 + 16;
            double y2 = y1 + 0.05;
            double z2 = z1 + 16;
            batch.renderFilledBox(x1, y1, z1, x2, y2, z2, YELLOW);
        }
        batch.flush();
    }
}
