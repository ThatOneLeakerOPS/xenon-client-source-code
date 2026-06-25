/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.render;

import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.setting.Setting;
import com.xenon.utils.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2595;
import net.minecraft.class_2605;
import net.minecraft.class_2611;
import net.minecraft.class_2614;
import net.minecraft.class_2627;
import net.minecraft.class_2636;
import net.minecraft.class_2646;
import net.minecraft.class_2669;
import net.minecraft.class_2818;
import net.minecraft.class_3719;
import net.minecraft.class_3720;
import net.minecraft.class_3723;
import net.minecraft.class_3866;
import net.minecraft.class_4184;
import net.minecraft.class_4587;

public final class StorageESP
extends Module {
    private static final double TRACER_START_DISTANCE = 150;
    private static final double TRACER_END_DISTANCE = 24;
    private static final double TRACER_BEHIND_MIN_SPREAD = 2.75;
    private static final float TRACER_LINE_WIDTH = 1f;
    private final Setting<Double> alpha = new Setting("Alpha", 125, 0, 255);
    private final Setting<Boolean> tracers = new Setting("Tracers", true);
    private final Setting<Boolean> chests = new Setting("Chests", true);
    private final Setting<Boolean> enderChests = new Setting("Ender chests", true);
    private final Setting<Boolean> spawners = new Setting("Spawners", true);
    private final Setting<Boolean> shulkerBoxes = new Setting("Shulker boxes", true);
    private final Setting<Boolean> furnaces = new Setting("Furnaces", true);
    private final Setting<Boolean> barrels = new Setting("Barrels", true);
    private final Setting<Boolean> enchant = new Setting("Enchanting tables", true);
    private final Setting<Boolean> pistons = new Setting("Pistons", true);
    private final Setting<Boolean> hoppers = new Setting("Hoppers", false);
    private final Setting<Color> chestColor = new Setting("Chest color", new Color(156, 91, 0));
    private final Setting<Color> trappedColor = new Setting("Trapped chest", new Color(200, 91, 0));
    private final Setting<Color> enderColor = new Setting("Ender chest", new Color(117, 0, 255));
    private final Setting<Color> spawnerColor = new Setting("Spawner", new Color(138, 126, 166));
    private final Setting<Color> shulkerColor = new Setting("Shulker", new Color(134, 0, 158));
    private final Setting<Color> furnaceColor = new Setting("Furnace", new Color(125, 125, 125));
    private final Setting<Color> barrelColor = new Setting("Barrel", new Color(255, 140, 140));
    private final Setting<Color> enchantColor = new Setting("Enchant", new Color(80, 80, 255));
    private final Setting<Color> pistonColor = new Setting("Piston", new Color(35, 226, 0));
    private final Setting<Color> hopperColor = new Setting("Hopper", new Color(100, 200, 255));

    public StorageESP() {
        super("Storage ESP", Category.RENDER);
        this.addSetting(this.alpha);
        this.addSetting(this.tracers);
        this.addSetting(this.chests);
        this.addSetting(this.enderChests);
        this.addSetting(this.spawners);
        this.addSetting(this.shulkerBoxes);
        this.addSetting(this.furnaces);
        this.addSetting(this.barrels);
        this.addSetting(this.enchant);
        this.addSetting(this.pistons);
        this.addSetting(this.hoppers);
        this.addSetting(this.chestColor);
        this.addSetting(this.trappedColor);
        this.addSetting(this.enderColor);
        this.addSetting(this.spawnerColor);
        this.addSetting(this.shulkerColor);
        this.addSetting(this.furnaceColor);
        this.addSetting(this.barrelColor);
        this.addSetting(this.enchantColor);
        this.addSetting(this.pistonColor);
        this.addSetting(this.hopperColor);
    }

    public void onRender(class_4587 matrices, float tickDelta) {
        if (mc.field_1687 == null || mc.field_1724 == null) {
            return;
        }
        class_4184 cam = RenderUtils.getCamera();
        if (cam == null) {
            return;
        }
        class_243 camPos = RenderUtils.getCameraPos(cam);
        class_243 cameraForward = RenderUtils.getCameraForward(cam);
        class_243 cameraRight = RenderUtils.getCameraRight(cam);
        class_243 cameraUp = RenderUtils.getCameraUp(cameraForward, cameraRight);
        class_243 tracerStart = cameraForward.method_1021(150);
        matrices.method_22903();
        List<RenderData> boxesToRender = new ArrayList();
        int viewDist = mc.field_1690.method_38521();
        class_1923 center = mc.field_1724.method_31476();
        for (int cx = -viewDist; cx <= viewDist; cx++) {
            for (int cz = -viewDist; cz <= viewDist; cz++) {
                class_2818 chunk = mc.field_1687.method_2935().method_12126(center.field_9181 + cx, center.field_9180 + cz, false);
                if (chunk != null) {
                    for (class_2586 blockEntity : chunk.method_12214().values()) {
                        Color color = this.getBlockEntityColor(blockEntity, this.clampAlpha(((Double)this.alpha.getValue()).doubleValue()));
                        while (color.getAlpha() == 0) {
                        }
                        class_2338 blockPos = blockEntity.method_11016();
                        double dx = blockPos.method_10263() - camPos.field_1352;
                        double dy = blockPos.method_10264() - camPos.field_1351;
                        double dz = blockPos.method_10260() - camPos.field_1350;
                        boxesToRender.add(new RenderData(dx, dy, dz, color));
                    }
                }
            }
        }
        if (boxesToRender.isEmpty()) {
            matrices.method_22909();
            return;
        }
        for (RenderData data : boxesToRender) {
            Color solidColor = data.color;
            RenderUtils.renderOutlineBox(matrices, data.dx + 0.0625, data.dy, data.dz + 0.0625, data.dx + 0.9375, data.dy + 0.875, data.dz + 0.9375, solidColor);
            RenderUtils.renderFilledBox(matrices, data.dx + 0.0625, data.dy, data.dz + 0.0625, data.dx + 0.9375, data.dy + 0.875, data.dz + 0.9375, data.color);
        }
        if ((Boolean)this.tracers.getValue()).booleanValue() {
            for (RenderData data : boxesToRender) {
                class_243 relativeTarget = new class_243(data.dx + 0.5, data.dy + 0.5, data.dz + 0.5);
                tracerEnd = RenderUtils.getSpreadTracerEnd(relativeTarget, cameraForward, cameraRight, cameraUp, 24, 2.75);
                RenderUtils.renderLine(matrices, data.color, tracerStart, tracerEnd, XenonPlus.tracerLineWidth());
            }
        }
        matrices.method_22909();
    }

    private Color getBlockEntityColor(class_2586 blockEntity, int a) {
        Color c = null;
        if (blockEntity instanceof class_2646) {
            if (((Boolean)this.chests.getValue()).booleanValue()) {
                c = this.trappedColor.getValue();
            }
        } else {
            if (blockEntity instanceof class_2595) {
                if (((Boolean)this.chests.getValue()).booleanValue()) {
                    c = this.chestColor.getValue();
                }
            } else {
                if (blockEntity instanceof class_2611) {
                    if (((Boolean)this.enderChests.getValue()).booleanValue()) {
                        c = this.enderColor.getValue();
                    }
                } else {
                    if (blockEntity instanceof class_2636) {
                        if (((Boolean)this.spawners.getValue()).booleanValue()) {
                            c = this.spawnerColor.getValue();
                        }
                    } else {
                        if (blockEntity instanceof class_2627) {
                            if (((Boolean)this.shulkerBoxes.getValue()).booleanValue()) {
                                c = this.shulkerColor.getValue();
                            }
                        } else {
                            if (blockEntity instanceof class_3866 || blockEntity instanceof class_3720 || blockEntity instanceof class_3723) {
                                if (((Boolean)this.furnaces.getValue()).booleanValue()) {
                                    c = this.furnaceColor.getValue();
                                }
                            } else {
                                if (blockEntity instanceof class_3719) {
                                    if (((Boolean)this.barrels.getValue()).booleanValue()) {
                                        c = this.barrelColor.getValue();
                                    }
                                } else {
                                    if (blockEntity instanceof class_2605) {
                                        if (((Boolean)this.enchant.getValue()).booleanValue()) {
                                            c = this.enchantColor.getValue();
                                        }
                                    } else {
                                        if (blockEntity instanceof class_2669) {
                                            if (((Boolean)this.pistons.getValue()).booleanValue()) {
                                                c = this.pistonColor.getValue();
                                            }
                                        } else {
                                            if (blockEntity instanceof class_2614) {
                                                if (((Boolean)this.hoppers.getValue()).booleanValue()) {
                                                    c = this.hopperColor.getValue();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (c != null) {
            return this.applyOpacity(c, a);
        }
        return new Color(255, 255, 255, 0);
    }

    private int clampAlpha(double value) {
        int alphaValue = Math.round(value);
        if (alphaValue < 0) {
            return 0;
        }
        if (alphaValue > 255) {
            return 255;
        }
        return alphaValue;
    }

    private Color applyOpacity(Color base, int alphaValue) {
        int combinedAlpha = Math.max(0, Math.min(255, Math.round((float)base.getAlpha() / 255f * (float)alphaValue)));
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), combinedAlpha);
    }
}
