package com.israeltv.plus;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Rational;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.PathInterpolator;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.window.BackEvent;
import android.window.OnBackAnimationCallback;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.core.splashscreen.SplashScreen;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Israel TV+ for Android phones, tablets and Android TV / Google TV / Fire TV (targets Android 17, runs on 5.0+).
 *
 * The web app is bundled in assets/www and loaded from file:///android_asset, with file pages allowed to
 * reach any origin: hls.js can then read streams that send no CORS headers, and http streams play
 * (the https web site can't play either). The remote control's Back, colour and channel keys are
 * forwarded to the app's key handler (the same key codes as LG webOS).
 *
 * Phones: edge-to-edge (the app draws behind transparent system bars; the bar / cutout sizes go to the page as
 * CSS insets), Material You colours (Android 12+ wallpaper palette), predictive back with the in-app layer
 * following the gesture (Android 14+), picture-in-picture (auto-enter on Android 12+) and haptic ticks.
 * TVs: immersive full screen as before.
 * Start: the AndroidX core-splashscreen (the Android 12 splash on every version) stays until the page has drawn
 * its first screen (Bridge.ready, at most SPLASH_MAX_MS), then zooms away; a short launch sound plays on a cold
 * start (setting, silent when the phone is on silent / vibrate). Phones run at the screen's highest refresh rate.
 * Updates: the web app checks GitHub; a new APK is downloaded and handed to the system installer from here.
 */
public class MainActivity extends Activity {
    static final String ACTION_INSTALL_STATUS = "com.israeltv.plus.INSTALL_STATUS";
    static final long SPLASH_MAX_MS = 3000;
    private WebView web;
    private boolean tv;
    private boolean immersive;
    private boolean playing;
    private volatile boolean pageReady;
    private long splashUntil;
    private SharedPreferences prefs;
    private String insetsJson = "{\"t\":0,\"r\":0,\"b\":0,\"l\":0}";

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle state) {
        SplashScreen splash = SplashScreen.installSplashScreen(this);   // before super.onCreate (sets the app theme back)
        super.onCreate(state);
        prefs = getSharedPreferences("itv", MODE_PRIVATE);
        splashUntil = SystemClock.uptimeMillis() + SPLASH_MAX_MS;
        splash.setKeepOnScreenCondition(() -> !pageReady && SystemClock.uptimeMillis() < splashUntil);
        splash.setOnExitAnimationListener(p -> {
            // the icon grows and the splash fades: Material "emphasized decelerate"
            PathInterpolator ease = new PathInterpolator(0.05f, 0.7f, 0.1f, 1f);
            View icon = null;
            // core-splashscreen 1.0.1 throws on Android 12+ when the system's splash has no icon view (warm starts)
            try { icon = p.getIconView(); } catch (RuntimeException ignored) { }
            if (icon != null) icon.animate().scaleX(1.6f).scaleY(1.6f).alpha(0f).setDuration(420).setInterpolator(ease).start();
            try {
                p.getView().animate().alpha(0f).setStartDelay(80).setDuration(380).setInterpolator(ease)
                    .withEndAction(p::remove).start();
            } catch (RuntimeException e) {
                p.remove();
            }
        });
        if (state == null) playLaunchSound();
        tv = isTv();
        Window w = getWindow();
        w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 30) {
            w.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        } else if (Build.VERSION.SDK_INT >= 28) {
            w.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        WebView.setWebContentsDebuggingEnabled(true);
        web = new WebView(this);
        web.setBackgroundColor(0xff000000);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        // TVs: honour the page's fixed-width viewport (app.js sets width=1600 for "AndroidTV"), like the LG app
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setUserAgentString(s.getUserAgentString() + " IsraelTVPlus/3 " + (tv ? "AndroidTV" : "AndroidPhone"));

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if ("file".equals(u.getScheme())) return false;
                openUrl(u.toString()); // links (downloads page, service sites) go to the browser
                return true;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                sendInsets();
                // builds without Bridge.ready (older live updates): the scripts have loaded by now
                v.postDelayed(() -> pageReady = true, 250);
            }

            /** The page's process ran out of memory or crashed: start the app again instead of closing it. */
            @Override
            public boolean onRenderProcessGone(WebView v, android.webkit.RenderProcessGoneDetail d) {
                if (web == v) web = null;
                try { ((android.view.ViewGroup) v.getParent()).removeView(v); } catch (Exception ignored) { }
                v.destroy();
                recreate();
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new Bridge(), "AndroidApp");
        setContentView(web);
        if (tv) hideSystemBars();
        else { edgeToEdge(); highRefreshRate(); }
        if (Build.VERSION.SDK_INT >= 33) registerBack();
        if (state != null) web.restoreState(state);
        else web.loadUrl("file:///android_asset/www/index.html");
    }

    // ------------------------------------------------------------------ edge-to-edge (phones)
    @SuppressWarnings("deprecation")
    void edgeToEdge() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 30) {
            w.setDecorFitsSystemWindows(false);
        } else {
            web.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT < 35) {   // Android 15+ draws transparent bars by itself
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.TRANSPARENT);
            w.setNavigationBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            w.setNavigationBarContrastEnforced(false);
            w.setStatusBarContrastEnforced(false);
        }
        web.setOnApplyWindowInsetsListener((v, insets) -> {
            float d = getResources().getDisplayMetrics().density;
            int t, r, b, l, k = 0;
            if (Build.VERSION.SDK_INT >= 30) {
                android.graphics.Insets i = insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
                t = i.top; r = i.right; b = i.bottom; l = i.left;
                // the keyboard: edge-to-edge windows don't shrink for it, so the page lifts its glass bar by this much
                k = insets.getInsets(WindowInsets.Type.ime()).bottom;
            } else {
                // Android 5-10: adjustResize shrinks the window for the keyboard, nothing to add
                t = insets.getSystemWindowInsetTop(); r = insets.getSystemWindowInsetRight();
                b = insets.getSystemWindowInsetBottom(); l = insets.getSystemWindowInsetLeft();
            }
            insetsJson = String.format(Locale.US, "{\"t\":%.1f,\"r\":%.1f,\"b\":%.1f,\"l\":%.1f,\"k\":%.1f}", t / d, r / d, b / d, l / d, k / d);
            sendInsets();
            return insets;
        });
        web.requestApplyInsets();
    }

    void sendInsets() {
        if (tv || web == null) return;
        web.evaluateJavascript("(function(i){window.__itvInsets&&window.__itvInsets(i.t,i.r,i.b,i.l,i.k)})(" + insetsJson + ")", null);
    }

    @SuppressWarnings("deprecation")
    void setBarsLight(boolean light) {
        if (tv) return;
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController c = getWindow().getInsetsController();
            if (c == null) return;
            int mask = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
            c.setSystemBarsAppearance(light ? mask : 0, mask);
        } else if (Build.VERSION.SDK_INT >= 23) {
            int f = web.getSystemUiVisibility();
            f = light ? f | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : f & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            web.setSystemUiVisibility(f);
        }
    }

    /** Phones: the bars hide while a channel plays full screen (swipe from the edge shows them). */
    @SuppressWarnings("deprecation")
    void setBarsHidden(boolean on) {
        immersive = on;
        if (tv) return;
        if (Build.VERSION.SDK_INT >= 30) {
            WindowInsetsController c = getWindow().getInsetsController();
            if (c == null) return;
            c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            if (on) c.hide(WindowInsets.Type.systemBars());
            else c.show(WindowInsets.Type.systemBars());
        } else {
            int base = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            web.setSystemUiVisibility(on ? base | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY : base);
        }
    }

    // ------------------------------------------------------------------ 120 Hz, launch sound
    /** Phones: ask for the screen's fastest mode at the current resolution (120 Hz on most phones), not the 60 Hz default. */
    @SuppressWarnings("deprecation")
    void highRefreshRate() {
        if (Build.VERSION.SDK_INT < 23) return;
        try {
            Display d = Build.VERSION.SDK_INT >= 30 ? getDisplay() : getWindowManager().getDefaultDisplay();
            if (d == null) return;
            Display.Mode cur = d.getMode(), best = cur;
            for (Display.Mode m : d.getSupportedModes()) {
                if (m.getPhysicalWidth() == cur.getPhysicalWidth() && m.getPhysicalHeight() == cur.getPhysicalHeight()
                        && m.getRefreshRate() > best.getRefreshRate()) best = m;
            }
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.preferredDisplayModeId = best.getModeId();
            lp.preferredRefreshRate = best.getRefreshRate();
            getWindow().setAttributes(lp);
            // Android 15+: the WebView asks for the high frame-rate category too (else ARR may drop it to 60 when idle-ish)
            if (Build.VERSION.SDK_INT >= 35) web.setRequestedFrameRate(View.REQUESTED_FRAME_RATE_CATEGORY_HIGH);
        } catch (Exception ignored) {
        }
    }

    /** A short chime (res/raw/launch.wav, tools/make_launch_sound.py) on a cold start, unless off or the phone is silent. */
    void playLaunchSound() {
        if (!prefs.getBoolean("launchSound", true)) return;
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null || am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) return;
        try {
            final SoundPool pool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build();
            pool.setOnLoadCompleteListener((sp, id, status) -> {
                if (status == 0) sp.play(id, 0.55f, 0.55f, 1, 0, 1f);
                new Handler(Looper.getMainLooper()).postDelayed(sp::release, 2500);
            });
            pool.load(this, R.raw.launch, 1);
        } catch (Exception ignored) {
        }
    }

    /**
     * The web app's haptic kinds -> the system haptic constants (the same set Compose's HapticFeedbackType maps to),
     * newest constant first. Follows the system haptics setting.
     */
    static int hapticConstant(String kind) {
        int sdk = Build.VERSION.SDK_INT;
        switch (kind == null ? "" : kind) {
            case "long": return HapticFeedbackConstants.LONG_PRESS;
            case "press": return sdk >= 27 ? HapticFeedbackConstants.KEYBOARD_PRESS : HapticFeedbackConstants.VIRTUAL_KEY;
            case "confirm": return sdk >= 30 ? HapticFeedbackConstants.CONFIRM : HapticFeedbackConstants.VIRTUAL_KEY;
            case "reject": return sdk >= 30 ? HapticFeedbackConstants.REJECT : HapticFeedbackConstants.LONG_PRESS;
            case "on": return sdk >= 34 ? HapticFeedbackConstants.TOGGLE_ON : HapticFeedbackConstants.CLOCK_TICK;
            case "off": return sdk >= 34 ? HapticFeedbackConstants.TOGGLE_OFF : HapticFeedbackConstants.CLOCK_TICK;
            case "start": return sdk >= 30 ? HapticFeedbackConstants.GESTURE_START : HapticFeedbackConstants.CLOCK_TICK;
            case "end": return sdk >= 30 ? HapticFeedbackConstants.GESTURE_END : HapticFeedbackConstants.CLOCK_TICK;
            case "edge": return sdk >= 34 ? HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE : HapticFeedbackConstants.CLOCK_TICK;
            case "segment": return sdk >= 34 ? HapticFeedbackConstants.SEGMENT_TICK : HapticFeedbackConstants.CLOCK_TICK;
            default:   // "tick": tabs, chips, small switches
                return sdk >= 34 ? HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                    : sdk >= 27 ? HapticFeedbackConstants.KEYBOARD_PRESS : HapticFeedbackConstants.VIRTUAL_KEY;
        }
    }

    // ------------------------------------------------------------------ Material You
    /** The Android 12+ dynamic (wallpaper) palette as #rrggbb, or "" before Android 12. */
    String dynamicColors() {
        if (Build.VERSION.SDK_INT < 31) return "";
        JSONObject o = new JSONObject();
        int[][] ids = {
            {android.R.color.system_accent1_100, android.R.color.system_accent1_200, android.R.color.system_accent1_600,
                android.R.color.system_accent1_700, android.R.color.system_accent1_800},
            {android.R.color.system_accent2_200, android.R.color.system_accent2_600},
            {android.R.color.system_accent3_200, android.R.color.system_accent3_400, android.R.color.system_accent3_600},
            {android.R.color.system_neutral1_50, android.R.color.system_neutral1_100, android.R.color.system_neutral1_200,
                android.R.color.system_neutral1_800, android.R.color.system_neutral1_900},
            {android.R.color.system_neutral2_700, android.R.color.system_neutral2_800}};
        String[][] names = {{"a1_100", "a1_200", "a1_600", "a1_700", "a1_800"}, {"a2_200", "a2_600"}, {"a3_200", "a3_400", "a3_600"},
            {"n1_50", "n1_100", "n1_200", "n1_800", "n1_900"}, {"n2_700", "n2_800"}};
        try {
            for (int g = 0; g < ids.length; g++)
                for (int i = 0; i < ids[g].length; i++)
                    o.put(names[g][i], String.format("#%06x", getColor(ids[g][i]) & 0xffffff));
        } catch (Exception e) {
            return "";
        }
        return o.toString();
    }

    // ------------------------------------------------------------------ predictive back (Android 13+ / 14+ with progress)
    private Object backCallback;

    void registerBack() {
        OnBackInvokedDispatcher d = getOnBackInvokedDispatcher();
        if (Build.VERSION.SDK_INT >= 34) {
            OnBackAnimationCallback cb = new OnBackAnimationCallback() {
                @Override public void onBackStarted(BackEvent e) { progress(0f, e.getSwipeEdge()); }
                @Override public void onBackProgressed(BackEvent e) { progress(e.getProgress(), e.getSwipeEdge()); }
                @Override public void onBackCancelled() { js("window.__itvBackCancel && window.__itvBackCancel()"); }
                @Override public void onBackInvoked() { key(461); }
            };
            backCallback = cb;
            d.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, cb);
        } else {
            OnBackInvokedCallback cb = () -> key(461);
            backCallback = cb;
            d.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, cb);
        }
    }

    void progress(float p, int edge) {
        if (tv) return;
        js(String.format(Locale.US, "window.__itvBackProgress && window.__itvBackProgress(%.3f,%b)", p, edge == BackEvent.EDGE_LEFT));
    }

    void key(int code) { js("window.__itvKey && window.__itvKey(" + code + ")"); }

    void js(String code) { if (web != null) web.evaluateJavascript(code, null); }

    // ------------------------------------------------------------------ picture-in-picture
    boolean canPip() {
        return !tv && Build.VERSION.SDK_INT >= 26 && getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
    }

    PictureInPictureParams pipParams() {
        PictureInPictureParams.Builder b = new PictureInPictureParams.Builder().setAspectRatio(new Rational(16, 9));
        if (Build.VERSION.SDK_INT >= 31) b.setAutoEnterEnabled(playing).setSeamlessResizeEnabled(true);
        return b.build();
    }

    void setPlaying(boolean on) {
        playing = on;
        if (canPip()) {
            try { setPictureInPictureParams(pipParams()); } catch (Exception ignored) { }
        }
    }

    void enterPip() {
        if (!canPip()) return;
        try { enterPictureInPictureMode(pipParams()); } catch (Exception ignored) { }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        // Android 8-11: no auto-enter, so go to picture-in-picture when the user leaves while a channel plays
        if (playing && Build.VERSION.SDK_INT >= 26 && Build.VERSION.SDK_INT < 31) enterPip();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean inPip, Configuration c) {
        super.onPictureInPictureModeChanged(inPip, c);
        js("window.__itvPip && window.__itvPip(" + inPip + ")");
    }

    // ------------------------------------------------------------------ JavaScript bridge
    /** Called from JavaScript as window.AndroidApp.* */
    public class Bridge {
        @JavascriptInterface
        public void exit() { runOnUiThread(MainActivity.this::finish); }

        @JavascriptInterface
        public boolean isTv() { return tv; }

        @JavascriptInterface
        public String insets() { return insetsJson; }

        @JavascriptInterface
        public String dynamicColors() { return MainActivity.this.dynamicColors(); }

        @JavascriptInterface
        public void setBarsLight(boolean light) { runOnUiThread(() -> MainActivity.this.setBarsLight(light)); }

        @JavascriptInterface
        public void setImmersive(boolean on) { runOnUiThread(() -> MainActivity.this.setBarsHidden(on)); }

        @JavascriptInterface
        public void setPlaying(boolean on) { runOnUiThread(() -> MainActivity.this.setPlaying(on)); }

        @JavascriptInterface
        public boolean canPip() { return MainActivity.this.canPip(); }

        @JavascriptInterface
        public void enterPip() { runOnUiThread(MainActivity.this::enterPip); }

        /** tick / press / long / confirm / reject / on / off / start / end / edge / segment (see hapticConstant). */
        @JavascriptInterface
        public boolean haptic(String kind) {
            final int c = hapticConstant(kind);
            runOnUiThread(() -> { if (web != null) web.performHapticFeedback(c); });
            return true;
        }

        /** The page drew its first screen: the splash screen may leave. */
        @JavascriptInterface
        public void ready() { pageReady = true; }

        @JavascriptInterface
        public boolean launchSound() { return prefs.getBoolean("launchSound", true); }

        @JavascriptInterface
        public void setLaunchSound(boolean on) { prefs.edit().putBoolean("launchSound", on).apply(); }

        /** Opens a TV-service app: the TV build on a TV, else the phone build; not installed -> Play Store, else the site. */
        @JavascriptInterface
        public void openApp(String tvPkg, String pkg, String url) {
            runOnUiThread(() -> {
                PackageManager pm = getPackageManager();
                String[] order = tv ? new String[]{tvPkg, pkg} : new String[]{pkg, tvPkg};
                for (String p : order) {
                    if (p == null || p.isEmpty()) continue;
                    Intent i = tv ? pm.getLeanbackLaunchIntentForPackage(p) : null;
                    if (i == null) i = pm.getLaunchIntentForPackage(p);
                    if (i != null && start(i)) return;
                }
                String store = order[0] != null && !order[0].isEmpty() ? order[0] : pkg;
                if (store != null && !store.isEmpty()
                        && start(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + store)))) return;
                openUrl(url);
            });
        }

        @JavascriptInterface
        public void openUrl(String url) { runOnUiThread(() -> MainActivity.this.openUrl(url)); }

        /** Model, Android version and memory: shown in Settings → About, and slow devices get the fast UI mode. */
        @JavascriptInterface
        public String deviceInfo() {
            JSONObject o = new JSONObject();
            try {
                o.put("manufacturer", Build.MANUFACTURER);
                o.put("model", Build.MODEL);
                o.put("release", Build.VERSION.RELEASE);
                o.put("sdk", Build.VERSION.SDK_INT);
                o.put("tv", tv);
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                if (am != null) {
                    ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
                    am.getMemoryInfo(mi);
                    o.put("ramMb", mi.totalMem / 1048576);
                    o.put("lowRam", am.isLowRamDevice());
                }
                o.put("versionName", getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            } catch (Exception ignored) {
            }
            return o.toString();
        }

        /** Downloads a new APK (GitHub release) and hands it to the system installer. Progress: window.__itvUpdate. */
        @JavascriptInterface
        public void installUpdate(final String url) {
            new Thread(() -> download(url)).start();
        }
    }

    void report(String state, String value) {
        final String js = "window.__itvUpdate && window.__itvUpdate(" + JSONObject.quote(state) + "," + JSONObject.quote(value) + ")";
        runOnUiThread(() -> web.evaluateJavascript(js, null));
    }

    void download(String url) {
        try {
            if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
                report("error", "יש לאשר התקנת עדכונים מהאפליקציה (נפתח מסך ההגדרות) ואז לנסות שוב");
                runOnUiThread(() -> start(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()))));
                return;
            }
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setInstanceFollowRedirects(true);
            c.setConnectTimeout(20000);
            c.setReadTimeout(30000);
            long len = c.getContentLength();
            PackageInstaller pi = getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            if (len > 0) params.setSize(len);
            int id = pi.createSession(params);
            PackageInstaller.Session session = pi.openSession(id);
            InputStream in = c.getInputStream();
            OutputStream out = session.openWrite("update.apk", 0, len > 0 ? len : -1);
            byte[] buf = new byte[65536];
            long done = 0;
            int n, last = -1;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
                done += n;
                if (len > 0) {
                    int pct = (int) (done * 100 / len);
                    if (pct != last) { last = pct; report("progress", String.valueOf(pct)); }
                }
            }
            session.fsync(out);
            out.close();
            in.close();
            Intent i = new Intent(this, MainActivity.class).setAction(ACTION_INSTALL_STATUS);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_MUTABLE : 0);
            PendingIntent pending = PendingIntent.getActivity(this, id, i, flags);
            report("installing", "");
            session.commit(pending.getIntentSender());
            session.close();
        } catch (Exception e) {
            report("error", String.valueOf(e.getMessage()));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null || !ACTION_INSTALL_STATUS.equals(intent.getAction())) return;
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            Intent confirm = (Intent) intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (confirm != null) start(confirm);
        } else if (status != PackageInstaller.STATUS_SUCCESS) {
            report("error", String.valueOf(intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)));
        }
    }

    boolean isTv() {
        UiModeManager ui = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        return (ui != null && ui.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION)
            || getPackageManager().hasSystemFeature("android.software.leanback");
    }

    boolean start(Intent i) {
        try {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return true;
        } catch (ActivityNotFoundException | SecurityException e) {
            return false;
        }
    }

    void openUrl(String url) {
        if (url != null && !url.isEmpty()) start(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /** Remote / keyboard key -> the web app's key code (webOS numbering), or 0 to let the WebView handle it. */
    static int mapKey(int k) {
        switch (k) {
            case KeyEvent.KEYCODE_BACK: return 461;
            case KeyEvent.KEYCODE_PROG_RED: return 403;
            case KeyEvent.KEYCODE_PROG_GREEN: return 404;
            case KeyEvent.KEYCODE_PROG_YELLOW: return 405;
            case KeyEvent.KEYCODE_PROG_BLUE: return 406;
            case KeyEvent.KEYCODE_CHANNEL_UP: return 427;
            case KeyEvent.KEYCODE_CHANNEL_DOWN: return 428;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE: return 32;
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_SETTINGS: return 83;
            case KeyEvent.KEYCODE_INFO:
            case KeyEvent.KEYCODE_GUIDE: return 406;
            default: return 0;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        // Android 13+: Back goes through the OnBackInvokedCallback (predictive back), not as a key
        if (e.getKeyCode() == KeyEvent.KEYCODE_BACK && backCallback != null) return super.dispatchKeyEvent(e);
        int code = mapKey(e.getKeyCode());
        if (code != 0) {
            if (e.getAction() == KeyEvent.ACTION_DOWN) key(code);
            return true;
        }
        return super.dispatchKeyEvent(e);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (backCallback == null) key(461);   // never reached on Android 13+ (callback); kept for odd OEM builds
    }

    @SuppressWarnings("deprecation")
    void hideSystemBars() {
        web.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (focus && tv) hideSystemBars();
        else if (focus && immersive) setBarsHidden(true);
    }

    @Override
    public void onConfigurationChanged(Configuration c) {
        super.onConfigurationChanged(c);
        js("window.__itvColors && window.__itvColors()");   // dark/light or wallpaper colours may have changed
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        if (web != null) web.saveState(out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // keep playing in picture-in-picture; otherwise pause the page (saves battery)
        if (Build.VERSION.SDK_INT >= 24 && isInPictureInPictureMode()) return;
        if (web != null) web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) web.onResume();
    }

    @Override
    protected void onDestroy() {
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
