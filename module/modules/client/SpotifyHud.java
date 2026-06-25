/*
 * Decompiled with https://jar.tools
 */
package com.xenon.module.modules.client;

import com.xenon.gui.ClickGUI;
import com.xenon.module.Category;
import com.xenon.module.Module;
import com.xenon.module.modules.client.Hud;
import com.xenon.module.modules.client.XenonPlus;
import com.xenon.setting.Setting;
import com.xenon.utils.renderer.RenderUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;

public final class SpotifyHud
extends Module {
    private static SpotifyHud INSTANCE;
    private final Setting<Float> scale = new Setting("Scale", 1f, 0.6f, 2f);
    private volatile String title = "";
    private volatile String artist = "";
    private volatile boolean isPlaying = false;
    private volatile long anchorPosMs = 0L;
    private volatile long anchorTimeMs = 0L;
    private volatile long durMs = 0L;
    private ScheduledExecutorService scheduler;
    private ExecutorService actionExec;
    private File pollScriptFile;
    private File ctrlScriptFile;
    private File artFile;
    private static final class_2960 ART_ID = class_2960.method_60655("xenon", "spotify_art");
    private class_1043 artTex = null;
    private volatile long artLastModified = 0L;
    private volatile boolean hasArt = false;
    private float fade = 0f;
    private long lastNanos = 0L;
    private float scrollX = 0f;
    private long scrollNanos = 0L;
    private volatile boolean hasPolledOnce = false;
    private final AtomicBoolean polling = new AtomicBoolean(false);
    private final AtomicInteger fastPollCount = new AtomicInteger(0);
    private static final int BASE_W = 230;
    private static final int BASE_H = 76;
    private static final int BASE_ART = 52;
    private static final int BASE_PAD_X = 8;
    private static final float SCROLL_PX_S = 28f;
    private static final int SCROLL_GAP = 18;
    private static final int ROW_H = 8;
    private static final int GAP_TITLE = 3;
    private static final int GAP_ART = 5;
    private static final int GAP_BAR = 4;
    private static final int GAP_TIME = 5;
    private static final int BAR_H_BASE = 2;
    private static final String POLL_SCRIPT = "[void][System.Reflection.Assembly]::LoadFile('C:\\Windows\\Microsoft.NET\\Framework64\\v4.0.30319\\System.Runtime.WindowsRuntime.dll')\r\n$null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]\r\n$g = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -like 'IAsyncOperation*' })[0]\r\nfunction Aw($op,$t){$m=$g.MakeGenericMethod($t);$task=$m.Invoke($null,@($op));$task.GetAwaiter().GetResult()}\r\n$asStreamForRead = [System.IO.WindowsRuntimeStreamExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsStreamForRead' -and $_.GetParameters().Count -eq 1 } | Select-Object -First 1\r\ntry {\r\n  $mgr = Aw([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\r\n  $s = $mgr.GetCurrentSession()\r\n  if ($s) {\r\n    $p = Aw($s.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])\r\n    $tl = $s.GetTimelineProperties()\r\n    $pb = $s.GetPlaybackInfo()\r\n    if ($p.Title) {\r\n      if ($p.Thumbnail -and $asStreamForRead) {\r\n        try {\r\n          $stream = Aw($p.Thumbnail.OpenReadAsync()) ([Windows.Storage.Streams.IRandomAccessStreamWithContentType])\r\n          $netStream = $asStreamForRead.Invoke($null, @($stream))\r\n          $outPath = Join-Path $env:TEMP 'xenon_spotify_art.png'\r\n          $fs = [System.IO.File]::Create($outPath)\r\n          $netStream.CopyTo($fs)\r\n          $fs.Close()\r\n          $netStream.Close()\r\n        } catch {}\r\n      }\r\n      Write-Output ($p.Artist + '|||' + $p.Title + '|||' + [long]$tl.Position.TotalMilliseconds + '|||' + [long]$tl.EndTime.TotalMilliseconds + '|||' + ($pb.PlaybackStatus.ToString() -eq 'Playing'))\r\n    }\r\n  }\r\n} catch {}\r\n";
    private static final String CTRL_SCRIPT = "param([string]$action)\r\n[void][System.Reflection.Assembly]::LoadFile('C:\\Windows\\Microsoft.NET\\Framework64\\v4.0.30319\\System.Runtime.WindowsRuntime.dll')\r\n$null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]\r\n$g = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -like 'IAsyncOperation*' })[0]\r\nfunction Aw($op,$t){$m=$g.MakeGenericMethod($t);$task=$m.Invoke($null,@($op));$task.GetAwaiter().GetResult()}\r\ntry {\r\n  $mgr = Aw([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\r\n  $s = $mgr.GetCurrentSession()\r\n  if ($s) {\r\n    switch ($action) {\r\n      'next'   { Aw($s.TrySkipNextAsync()) ([bool]) | Out-Null }\r\n      'prev'   { Aw($s.TrySkipPreviousAsync()) ([bool]) | Out-Null }\r\n      'toggle' { Aw($s.TryTogglePlayPauseAsync()) ([bool]) | Out-Null }\r\n    }\r\n  }\r\n} catch {}\r\n";

    public SpotifyHud() {
        super("Spotify HUD", Category.RENDER);
        this.addSetting(this.scale);
        INSTANCE = this;
    }

    public static boolean isActive() {
        return INSTANCE != null && INSTANCE.isEnabled();
    }

    public static float getScale() {
        return INSTANCE == null ? 1f : (Float)INSTANCE.scale.getValue();
    }

    public static void setScale(float v) {
        if (INSTANCE == null) {
            return;
        }
        Float f = INSTANCE.scale.getMin();
        Float f = f;
        float lo = f instanceof Float ? f : 0.6f;
        Object var4 = INSTANCE.scale.getMax();
        f = var4;
        float hi = var4 instanceof Float ? f : 2f;
        INSTANCE.scale.setValue(Math.max(lo, Math.min(hi, v)));
    }

    public static int getCardW() {
        return Math.round(230f * SpotifyHud.getScale());
    }

    public static int getCardH() {
        return Math.round(76f * SpotifyHud.getScale());
    }

    private static int s(int base) {
        return Math.round((float)base * SpotifyHud.getScale());
    }

    private static float sf(float base) {
        return base * SpotifyHud.getScale();
    }

    private static int clampX(int x) {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.method_22683() == null) {
            return x;
        }
        int maxX = Math.max(0, mc.method_22683().method_4486() - SpotifyHud.getCardW());
        return Math.max(0, Math.min(x, maxX));
    }

    private static int clampY(int y) {
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.method_22683() == null) {
            return y;
        }
        int maxY = Math.max(0, mc.method_22683().method_4502() - SpotifyHud.getCardH());
        return Math.max(0, Math.min(y, maxY));
    }

    private static int getPosX() {
        int[] pos = Hud.getElementPos(Hud.HudElement.SPOTIFY_HUD);
        return SpotifyHud.clampX(pos[0]);
    }

    private static int getPosY() {
        int[] pos = Hud.getElementPos(Hud.HudElement.SPOTIFY_HUD);
        return SpotifyHud.clampY(pos[1]);
    }

    public void onEnable() {
        this.startBackground();
    }

    public void onTick() {
        if (this.scheduler == null || this.scheduler.isShutdown()) {
            this.startBackground();
        }
    }

    private void startBackground() {
        this.fade = 0f;
        this.scrollX = 0f;
        this.lastNanos = 0L;
        this.scrollNanos = 0L;
        this.hasPolledOnce = false;
        this.fastPollCount.set(0);
        this.writeScripts();
        this.artFile = new File(System.getenv("TEMP"), "xenon_spotify_art.png");
        Thread initialPoll = new Thread(this::poll, "xenon-spotify-initial-poll");
        initialPoll.setDaemon(true);
        initialPoll.start();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(SpotifyHud::lambda$startBackground$0);
        this.actionExec = Executors.newSingleThreadExecutor(SpotifyHud::lambda$startBackground$1);
        this.scheduler.scheduleAtFixedRate(this::lambda$startBackground$2, 1000L, 800L, TimeUnit.MILLISECONDS);
        this.scheduler.scheduleAtFixedRate(this::poll, 2L, 2L, TimeUnit.SECONDS);
    }

    public void onDisable() {
        if (this.scheduler != null) {
            this.scheduler.shutdownNow();
        }
        if (this.actionExec != null) {
            this.actionExec.shutdownNow();
        }
        this.title = "";
        this.artist = "";
        this.isPlaying = false;
        this.anchorPosMs = 0L;
        this.anchorTimeMs = 0L;
        this.durMs = 0L;
        this.fade = 0f;
        this.scrollX = 0f;
        this.hasArt = false;
        try {
            class_310.method_1551().method_1531().method_4615(ART_ID);
        }
        catch (Exception e1) {
            this.artTex = null;
            return;
        }
        this.artTex = null;
    }

    private void writeScripts() {
        this.pollScriptFile = File.createTempFile("xenon_smtc_poll_", ".ps1");
        this.pollScriptFile.deleteOnExit();
        Writer w = new FileWriter(this.pollScriptFile, StandardCharsets.UTF_8);
        w.write("[void][System.Reflection.Assembly]::LoadFile('C:\\Windows\\Microsoft.NET\\Framework64\\v4.0.30319\\System.Runtime.WindowsRuntime.dll')\r\n$null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]\r\n$g = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -like 'IAsyncOperation*' })[0]\r\nfunction Aw($op,$t){$m=$g.MakeGenericMethod($t);$task=$m.Invoke($null,@($op));$task.GetAwaiter().GetResult()}\r\n$asStreamForRead = [System.IO.WindowsRuntimeStreamExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsStreamForRead' -and $_.GetParameters().Count -eq 1 } | Select-Object -First 1\r\ntry {\r\n  $mgr = Aw([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\r\n  $s = $mgr.GetCurrentSession()\r\n  if ($s) {\r\n    $p = Aw($s.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])\r\n    $tl = $s.GetTimelineProperties()\r\n    $pb = $s.GetPlaybackInfo()\r\n    if ($p.Title) {\r\n      if ($p.Thumbnail -and $asStreamForRead) {\r\n        try {\r\n          $stream = Aw($p.Thumbnail.OpenReadAsync()) ([Windows.Storage.Streams.IRandomAccessStreamWithContentType])\r\n          $netStream = $asStreamForRead.Invoke($null, @($stream))\r\n          $outPath = Join-Path $env:TEMP 'xenon_spotify_art.png'\r\n          $fs = [System.IO.File]::Create($outPath)\r\n          $netStream.CopyTo($fs)\r\n          $fs.Close()\r\n          $netStream.Close()\r\n        } catch {}\r\n      }\r\n      Write-Output ($p.Artist + '|||' + $p.Title + '|||' + [long]$tl.Position.TotalMilliseconds + '|||' + [long]$tl.EndTime.TotalMilliseconds + '|||' + ($pb.PlaybackStatus.ToString() -eq 'Playing'))\r\n    }\r\n  }\r\n} catch {}\r\n");
        w.close();
        this.ctrlScriptFile = File.createTempFile("xenon_smtc_ctrl_", ".ps1");
        this.ctrlScriptFile.deleteOnExit();
        Writer w = new FileWriter(this.ctrlScriptFile, StandardCharsets.UTF_8);
        w.write("param([string]$action)\r\n[void][System.Reflection.Assembly]::LoadFile('C:\\Windows\\Microsoft.NET\\Framework64\\v4.0.30319\\System.Runtime.WindowsRuntime.dll')\r\n$null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager,Windows.Media.Control,ContentType=WindowsRuntime]\r\n$g = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object { $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -like 'IAsyncOperation*' })[0]\r\nfunction Aw($op,$t){$m=$g.MakeGenericMethod($t);$task=$m.Invoke($null,@($op));$task.GetAwaiter().GetResult()}\r\ntry {\r\n  $mgr = Aw([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])\r\n  $s = $mgr.GetCurrentSession()\r\n  if ($s) {\r\n    switch ($action) {\r\n      'next'   { Aw($s.TrySkipNextAsync()) ([bool]) | Out-Null }\r\n      'prev'   { Aw($s.TrySkipPreviousAsync()) ([bool]) | Out-Null }\r\n      'toggle' { Aw($s.TryTogglePlayPauseAsync()) ([bool]) | Out-Null }\r\n    }\r\n  }\r\n} catch {}\r\n");
        w.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void poll() {
        if (this.pollScriptFile == null || !this.pollScriptFile.exists()) {
            this.writeScripts();
        }
        if (this.pollScriptFile == null) {
            this.hasPolledOnce = true;
            return;
        }
        if (!this.polling.compareAndSet(false, true)) {
            return;
        }
        long beforeMs = System.currentTimeMillis();
        String[] tmp0 = new String[7];
        tmp0[0] = "powershell";
        tmp0[1] = "-NoProfile";
        tmp0[2] = "-NonInteractive";
        tmp0[3] = "-ExecutionPolicy";
        tmp0[4] = "Bypass";
        tmp0[5] = "-File";
        tmp0[6] = this.pollScriptFile.getAbsolutePath();
        Process proc = new ProcessBuilder(tmp0).redirectErrorStream(true).start();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
        String tmp1 = br.readLine();
        String l = tmp1;
        if (tmp1 != null) {
            l = l.trim();
        }
        if (l.contains("|||")) {
            line = l;
        }
        br.close();
        proc.waitFor(8L, TimeUnit.SECONDS);
        long afterMs = System.currentTimeMillis();
        long effectiveMs = (beforeMs + afterMs) / 2L;
        if (line == null) { /* goto L533; */ }
        String[] p = line.split("\\|\\|\\|", -1);
        if (p.length < 2) { /* goto L533; */ }
        if (p[1].trim().isEmpty()) { /* goto L533; */ }
        String newTitle = p[1].trim();
        String newArtist = p[0].trim();
        long newPosMs = p.length > 2 ? SpotifyHud.parseLong(p[2]) : 0L;
        long newDurMs = p.length > 3 ? SpotifyHud.parseLong(p[3]) : 0L;
        boolean playing = p.length > 4 && p[4].trim().equalsIgnoreCase("True");
        boolean trackChanged = !newTitle.equals(this.title);
        boolean stateChanged = playing != this.isPlaying;
        long predicted = this.isPlaying ? this.anchorPosMs + Math.max(0L, effectiveMs - this.anchorTimeMs) : this.anchorPosMs;
        boolean bigJump = Math.abs(newPosMs - predicted) > 4000L;
        if (trackChanged) {
            this.scrollX = 0f;
        }
        this.title = newTitle;
        this.artist = newArtist;
        this.durMs = newDurMs;
        this.isPlaying = playing;
        if (trackChanged || stateChanged || bigJump || this.anchorTimeMs == 0L) {
            this.anchorPosMs = newPosMs;
            this.anchorTimeMs = effectiveMs;
        }
        this.hasPolledOnce = true;
        this.polling.set(false);
        return;
        this.hasPolledOnce = true;
        this.polling.set(false);
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s.trim());
        }
        catch (Exception e) {
            return 0L;
        }
    }

    public static void runAction(String action) {
        if (INSTANCE == null || INSTANCE.actionExec == null || INSTANCE.ctrlScriptFile == null) {
            return;
        }
        long nowMs = System.currentTimeMillis();
        if ("toggle".equals(action)) {
            if (INSTANCE.isPlaying) {
                long elapsed = INSTANCE.anchorTimeMs > 0L ? Math.max(0L, nowMs - INSTANCE.anchorTimeMs) : 0L;
                INSTANCE.anchorPosMs = INSTANCE.anchorPosMs + elapsed;
                INSTANCE.anchorTimeMs = nowMs;
                INSTANCE.isPlaying = false;
            } else {
                INSTANCE.anchorTimeMs = nowMs;
                INSTANCE.isPlaying = true;
            }
        }
        INSTANCE.actionExec.submit(SpotifyHud::lambda$runAction$3 /* captured: action */);
    }

    private static int[] computeLayout(int by, int cardH) {
        int barH = Math.max(2, SpotifyHud.s(2));
        int contentH = 8 + SpotifyHud.s(3) + 8 + SpotifyHud.s(5) + barH + SpotifyHud.s(4) + 8 + SpotifyHud.s(5) + 8;
        int top = by + (cardH - contentH) / 2;
        int titleY = top;
        int artistY = titleY + 8 + SpotifyHud.s(3);
        int barY = artistY + 8 + SpotifyHud.s(5);
        int timeY = barY + barH + SpotifyHud.s(4);
        int btnY = timeY + 8 + SpotifyHud.s(5);
        int[] tmp0 = new int[5];
        tmp0[0] = titleY;
        tmp0[1] = artistY;
        tmp0[2] = barY;
        tmp0[3] = timeY;
        tmp0[4] = btnY;
        return tmp0;
    }

    public static boolean handleClick(double mouseX, double mouseY) {
        if (INSTANCE == null || !INSTANCE.isEnabled()) {
            return false;
        }
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1772 == null) {
            return false;
        }
        int bx = SpotifyHud.getPosX();
        int by = SpotifyHud.getPosY();
        int cardW = SpotifyHud.getCardW();
        int cardH = SpotifyHud.getCardH();
        class_327 tr = mc.field_1772;
        String prev = "\u25c0\u25c0";
        String togg = INSTANCE.isPlaying ? "\u2759\u2759" : "\u25b6";
        String next = "\u25b6\u25b6";
        int[] layout = SpotifyHud.computeLayout(by, cardH);
        int btnY = layout[4];
        int cx = bx + cardW / 2;
        int gap = SpotifyHud.s(14);
        int togX = cx - tr.method_1727(togg) / 2;
        int prvX = togX - gap - tr.method_1727(prev);
        int nxtX = togX + tr.method_1727(togg) + gap;
        int padX = SpotifyHud.s(5);
        int padY = SpotifyHud.s(3);
        int[][] tmp0 = new int[3][];
        int[] tmp1 = new int[4];
        tmp1[0] = prvX - padX;
        tmp1[1] = btnY - padY;
        tmp1[2] = tr.method_1727(prev) + 2 * padX;
        tmp1[3] = 8 + 2 * padY;
        tmp0[0] = tmp1;
        int[] tmp2 = new int[4];
        tmp2[0] = togX - padX;
        tmp2[1] = btnY - padY;
        tmp2[2] = tr.method_1727(togg) + 2 * padX;
        tmp2[3] = 8 + 2 * padY;
        tmp0[1] = tmp2;
        int[] tmp3 = new int[4];
        tmp3[0] = nxtX - padX;
        tmp3[1] = btnY - padY;
        tmp3[2] = tr.method_1727(next) + 2 * padX;
        tmp3[3] = 8 + 2 * padY;
        tmp0[2] = tmp3;
        int[][] rects = tmp0;
        String[] tmp4 = new String[3];
        tmp4[0] = "prev";
        tmp4[1] = "toggle";
        tmp4[2] = "next";
        String[] actions = tmp4;
        for (int i = 0; i < rects.length; i++) {
            int[] r = rects[i];
            SpotifyHud.runAction(actions[i]);
            if (mouseX < (double)r[0] && mouseX <= (double)(r[0] + r[2]) && mouseY >= (double)r[1] && mouseY <= (double)(r[1] + r[3])) continue;
            return true;
        }
        return false;
    }

    private void tryLoadArt() {
        if (this.artFile == null || !this.artFile.exists()) {
            return;
        }
        long mod = this.artFile.lastModified();
        long size = this.artFile.length();
        if (size < 200L) {
            return;
        }
        if (mod == this.artLastModified && this.artTex != null) {
            return;
        }
        byte[] bytes = Files.readAllBytes(this.artFile.toPath());
        InputStream in = new ByteArrayInputStream(bytes);
        class_1011 img = class_1011.method_4309(in);
        if (this.artTex != null) {
            class_310.method_1551().method_1531().method_4615(ART_ID);
        }
        this.artTex = new class_1043(SpotifyHud::lambda$tryLoadArt$4, img);
        class_310.method_1551().method_1531().method_4616(ART_ID, this.artTex);
        this.artLastModified = mod;
        this.hasArt = true;
        in.close();
    }

    public static void renderHud(class_332 ctx) {
        if (INSTANCE == null || !INSTANCE.isEnabled()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1724 == null) {
            return;
        }
        if (mc.field_1755 instanceof ClickGUI) {
            return;
        }
        if (mc.method_53526().method_53536()) {
            return;
        }
        long now = System.nanoTime();
        float dt = INSTANCE.lastNanos == 0L ? 0.016f : Math.min(0.1f, (float)(now - INSTANCE.lastNanos) / 1000000000f);
        INSTANCE.lastNanos = now;
        INSTANCE.fade = INSTANCE.fade + (1f - INSTANCE.fade) * (1f - (float)Math.exp((double)(-12f * dt)));
        float a = INSTANCE.fade;
        if (a < 0.01f) {
            return;
        }
        INSTANCE.tryLoadArt();
        int cardW = SpotifyHud.getCardW();
        int cardH = SpotifyHud.getCardH();
        int artSize = SpotifyHud.s(52);
        int padX = SpotifyHud.s(8);
        int bx = SpotifyHud.getPosX() + Math.round((1f - a) * (float)-(cardW + SpotifyHud.s(20)));
        int by = SpotifyHud.getPosY();
        class_327 tr = mc.field_1772;
        int accent = XenonPlus.getAccentARGB();
        int cBg = SpotifyHud.alpha(921879, (int)(230f * a));
        int cBorder = SpotifyHud.alpha(1975344, (int)(200f * a));
        int cArtBg = SpotifyHud.alpha(1448998, (int)(210f * a));
        int cTitle = SpotifyHud.alpha(16777215, (int)(255f * a));
        int cArtist = SpotifyHud.alpha(9083818, (int)(200f * a));
        int cBarBg = SpotifyHud.alpha(1975344, (int)(210f * a));
        int cBarFill = accent & ((int)(200f * a) << 24 | 16777215);
        int cTime = SpotifyHud.alpha(5924984, (int)(180f * a));
        int cBtn = SpotifyHud.alpha(13421772, (int)(220f * a));
        int cNote = SpotifyHud.alpha(accent & 16777215, (int)(160f * a));
        float radius = SpotifyHud.sf(2f);
        RenderUtil.drawRoundedRect(ctx, (float)bx, (float)by, (float)cardW, (float)cardH, radius, cBg, false);
        RenderUtil.drawOutline(ctx, (float)bx, (float)by, (float)cardW, (float)cardH, radius, 1f, cBorder, false);
        int ax = bx + padX;
        int ay = by + (cardH - artSize) / 2;
        if (INSTANCE.hasArt) {
            int tint = Math.max(0, Math.min(255, (int)(255f * a))) << 24 | 16777215;
            RenderUtil.drawTexture(ctx, (float)ax, (float)ay, (float)artSize, ART_ID, tint, SpotifyHud.sf(2f), false);
        } else {
            RenderUtil.drawRoundedRect(ctx, (float)ax, (float)ay, (float)artSize, (float)artSize, SpotifyHud.sf(2f), cArtBg, false);
            String noteChar = "\u266b";
            int nw = tr.method_1727(noteChar);
            ctx.method_51433(tr, noteChar, ax + (artSize - nw) / 2, ay + (artSize - 9) / 2, cNote, false);
        }
        int tx = ax + artSize + padX;
        int maxTw = Math.max(20, bx + cardW - padX - tx);
        int[] layout = SpotifyHud.computeLayout(by, cardH);
        int titleY = layout[0];
        int artistY = layout[1];
        int barY = layout[2];
        int timeY = layout[3];
        int btnY = layout[4];
        int barH = Math.max(2, SpotifyHud.s(2));
        String song = !INSTANCE.hasPolledOnce ? "" : INSTANCE.title.isEmpty() ? "No track" : INSTANCE.title;
        if (song.isEmpty()) { /* goto L907; */ }
        int songW = tr.method_1727(song);
        if (songW > maxTw) {
            long sNow = System.nanoTime();
            float sdt = INSTANCE.scrollNanos == 0L ? 0f : Math.min(0.1f, (float)(sNow - INSTANCE.scrollNanos) / 1000000000f);
            INSTANCE.scrollNanos = sNow;
            INSTANCE.scrollX = INSTANCE.scrollX + 28f * sdt;
            if (INSTANCE.scrollX > (float)(songW + 18)) {
                INSTANCE.scrollX = 0f;
            }
            int off = INSTANCE.scrollX;
            ctx.method_44379(tx, titleY - 1, tx + maxTw, titleY + 10);
            ctx.method_51433(tr, song, tx - off, titleY, cTitle, false);
            ctx.method_51433(tr, song, tx - off + songW + 18, titleY, cTitle, false);
            ctx.method_44380();
        } else {
            INSTANCE.scrollX = 0f;
            ctx.method_51433(tr, song, tx, titleY, cTitle, false);
        }
        if (!INSTANCE.artist.isEmpty()) {
            ctx.method_51433(tr, tr.method_27523(INSTANCE.artist, maxTw), tx, artistY, cArtist, false);
        }
        RenderUtil.drawRoundedRect(ctx, (float)tx, (float)barY, (float)maxTw, (float)barH, (float)barH / 2f, cBarBg, false);
        long nowMs = System.currentTimeMillis();
        if (INSTANCE.isPlaying) {
            if (INSTANCE.anchorTimeMs > 0L) {
                long elapsed = Math.max(0L, nowMs - INSTANCE.anchorTimeMs);
                long posMsNow = INSTANCE.anchorPosMs + elapsed;
                if (INSTANCE.durMs > 0L) {
                    posMsNow = Math.min(INSTANCE.durMs, posMsNow);
                }
            }
        } else {
            long posMsNow = INSTANCE.anchorPosMs;
        }
        float pct = Math.min(1f, (float)posMsNow / (float)INSTANCE.durMs);
        if (INSTANCE.durMs > 0L && posMsNow > 0L) {
            int fill = Math.max(barH, Math.round((float)maxTw * pct));
            int fillCol = Math.max(0, Math.min(255, (int)(200f * a))) << 24 | accent & 16777215;
            RenderUtil.drawRoundedRect(ctx, (float)tx, (float)barY, (float)fill, (float)barH, (float)barH / 2f, fillCol, false);
        }
        long pos = posMsNow / 1000L;
        long dur = INSTANCE.durMs / 1000L;
        String tPos = SpotifyHud.fmt(pos);
        String tDur = dur > 0L ? SpotifyHud.fmt(dur) : "--:--";
        ctx.method_51433(tr, tPos, tx, timeY, cTime, false);
        ctx.method_51433(tr, tDur, tx + maxTw - tr.method_1727(tDur), timeY, cTime, false);
        String prev = "\u25c0\u25c0";
        String togg = INSTANCE.isPlaying ? "\u2759\u2759" : "\u25b6";
        String next = "\u25b6\u25b6";
        int cx = bx + cardW / 2;
        int gap = SpotifyHud.s(14);
        int togX = cx - tr.method_1727(togg) / 2;
        int prvX = togX - gap - tr.method_1727(prev);
        int nxtX = togX + tr.method_1727(togg) + gap;
        int cToggle = INSTANCE.isPlaying ? Math.max(0, Math.min(255, (int)(230f * a))) << 24 | accent & 16777215 : cBtn;
        ctx.method_51433(tr, prev, prvX, btnY, cBtn, false);
        ctx.method_51433(tr, togg, togX, btnY, cToggle, false);
        ctx.method_51433(tr, next, nxtX, btnY, cBtn, false);
    }

    private static String fmt(long sec) {
        Object[] tmp0 = new Object[2];
        tmp0[0] = sec / 60L;
        tmp0[1] = sec % 60L;
        return String.format("%d:%02d", tmp0);
    }

    private static int alpha(int rgb, int a) {
        return Math.max(0, Math.min(255, a)) << 24 | rgb & 16777215;
    }
}
