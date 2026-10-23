package com.israeltv.plus;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Israel TV+ for Android phones, tablets and Android TV / Google TV / Fire TV.
 *
 * The web app is bundled in assets/www and loaded from file:///android_asset, with file pages allowed to
 * reach any origin: hls.js can then read streams that send no CORS headers, and http streams play
 * (the https web site can't play either). The remote control's Back, colour and channel keys are
 * forwarded to the app's key handler (the same key codes as LG webOS).
 */
public class MainActivity extends Activity {
    private WebView web;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 28) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
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
        s.setUserAgentString(s.getUserAgentString() + " IsraelTVPlus/2 " + (isTv() ? "AndroidTV" : "AndroidPhone"));

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if ("file".equals(u.getScheme())) return false;
                openUrl(u.toString()); // links (downloads page, service sites) go to the browser
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new Bridge(), "AndroidApp");
        setContentView(web);
        hideSystemBars();
        if (state != null) web.restoreState(state);
        else web.loadUrl("file:///android_asset/www/index.html");
    }

    /** Called from JavaScript as window.AndroidApp.* */
    public class Bridge {
        @JavascriptInterface
        public void exit() { runOnUiThread(MainActivity.this::finish); }

        @JavascriptInterface
        public boolean isTv() { return MainActivity.this.isTv(); }

        /** Opens a TV-service app: the TV build on a TV, else the phone build; not installed -> Play Store, else the site. */
        @JavascriptInterface
        public void openApp(String tvPkg, String pkg, String url) {
            runOnUiThread(() -> {
                PackageManager pm = getPackageManager();
                String[] order = MainActivity.this.isTv() ? new String[]{tvPkg, pkg} : new String[]{pkg, tvPkg};
                for (String p : order) {
                    if (p == null || p.isEmpty()) continue;
                    Intent i = MainActivity.this.isTv() && Build.VERSION.SDK_INT >= 21 ? pm.getLeanbackLaunchIntentForPackage(p) : null;
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
        int code = mapKey(e.getKeyCode());
        if (code != 0) {
            if (e.getAction() == KeyEvent.ACTION_DOWN) {
                web.evaluateJavascript("window.__itvKey && window.__itvKey(" + code + ")", null);
            }
            return true;
        }
        return super.dispatchKeyEvent(e);
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
        if (focus) hideSystemBars();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        web.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }
}
