import os, re

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"

# 1. Update MainActivity.java
main_path = os.path.join(ROOT, "platforms", "android", "src", "com", "israeltv", "plus", "MainActivity.java")
with open(main_path, "r", encoding="utf-8") as f:
    main_code = f.read().replace("\r\n", "\n")

target_main = """    boolean isTv() {
        UiModeManager ui = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        return (ui != null && ui.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION)
            || getPackageManager().hasSystemFeature("android.software.leanback");
    }"""

repl_main = """    boolean isTv() {
        UiModeManager ui = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        if (ui != null && ui.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) return true;
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature("android.software.leanback")
            || pm.hasSystemFeature("android.hardware.type.television")
            || pm.hasSystemFeature("android.software.live_tv")) return true;
        if (!pm.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) return true;
        String b = (Build.MODEL + " " + Build.PRODUCT + " " + Build.DEVICE + " " + Build.MANUFACTURER).toLowerCase(Locale.US);
        return b.contains("tv") || b.contains("box") || b.contains("bravia") || b.contains("aft") || b.contains("chromecast") || b.contains("shield") || b.contains("atv");
    }"""

if target_main in main_code:
    main_code = main_code.replace(target_main, repl_main)
    with open(main_path, "w", encoding="utf-8") as f:
        f.write(main_code)
    print("MainActivity.java patched!")
else:
    print("MainActivity.java target not found")

# 2. Update PhoneActivity.kt
phone_path = os.path.join(ROOT, "android", "app", "src", "main", "kotlin", "com", "israeltv", "plus", "PhoneActivity.kt")
with open(phone_path, "r", encoding="utf-8") as f:
    phone_code = f.read().replace("\r\n", "\n")

target_phone = """    private fun isTv(): Boolean {
        val ui = getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        return ui?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION ||
            packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }"""

repl_phone = """    private fun isTv(): Boolean {
        val ui = getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
        if (ui?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) return true
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
            packageManager.hasSystemFeature("android.hardware.type.television") ||
            packageManager.hasSystemFeature("android.software.live_tv")) return true
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) return true
        val b = "${Build.MODEL} ${Build.PRODUCT} ${Build.DEVICE} ${Build.MANUFACTURER}".lowercase(Locale.US)
        return b.contains("tv") || b.contains("box") || b.contains("bravia") || b.contains("aft") || b.contains("chromecast") || b.contains("shield") || b.contains("atv")
    }"""

if target_phone in phone_code:
    phone_code = phone_code.replace(target_phone, repl_phone)
    with open(phone_path, "w", encoding="utf-8") as f:
        f.write(phone_code)
    print("PhoneActivity.kt patched!")
else:
    print("PhoneActivity.kt target not found")

# 3. Update app.js
app_path = os.path.join(ROOT, "app.js")
with open(app_path, "r", encoding="utf-8") as f:
    app_code = f.read().replace("\r\n", "\n")

target_app_istv = """  var IS_TV = PLATFORM === 'webos' || PLATFORM === 'tizen' || /AndroidTV/.test(UA) ||
    (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.isTv && window.AndroidApp.isTv());"""

repl_app_istv = """  var IS_TV = PLATFORM === 'webos' || PLATFORM === 'tizen' ||
    /Web0S|webOS|Tizen|SMART-TV|SmartTV|AndroidTV|GoogleTV|AppleTV|HbbTV|NetCast|VIDAA|AFT[BMT]|POV_TV|BRAVIA|MiTV|MiBOX/i.test(UA) ||
    (PLATFORM === 'android' && window.AndroidApp && window.AndroidApp.isTv && window.AndroidApp.isTv()) ||
    (new URLSearchParams(location.search).get('tv') === '1');"""

if target_app_istv in app_code:
    app_code = app_code.replace(target_app_istv, repl_app_istv)
    print("app.js IS_TV patched!")
else:
    print("app.js IS_TV target not found")

# Also ensure Google Sign-in calls in app.js use GDrive.signIn or startDeviceFlow
app_code = app_code.replace(
    "if (IS_TV) window.GDrive.startDeviceFlow(function () { renderSettings(); });\n              else window.GDrive.signInWeb(function () { renderSettings(); });",
    "if (window.GDrive) window.GDrive.signIn(function () { renderSettings(); });"
)
app_code = app_code.replace(
    "if (IS_TV) window.GDrive.startDeviceFlow(function () { startLiveRecordingDialog(ch); });\n      else window.GDrive.signInWeb(function () { startLiveRecordingDialog(ch); });",
    "if (window.GDrive) window.GDrive.signIn(function () { startLiveRecordingDialog(ch); });"
)
app_code = app_code.replace(
    "if (IS_TV) window.GDrive.startDeviceFlow(function () { openDriveRecordingsDialog(); });\n      else window.GDrive.signInWeb(function () { openDriveRecordingsDialog(); });",
    "if (window.GDrive) window.GDrive.signIn(function () { openDriveRecordingsDialog(); });"
)

with open(app_path, "w", encoding="utf-8") as f:
    f.write(app_code)
print("app.js updated successfully!")

# 4. Update gdrive.js
gdrive_path = os.path.join(ROOT, "gdrive.js")
with open(gdrive_path, "r", encoding="utf-8") as f:
    gdrive_code = f.read().replace("\r\n", "\n")

# Add isTv and signIn methods to GDrive, improve signInWeb, improve showTvDeviceModal
old_signin_web_block = """    // ------------------------------------------------------------- Web Sign-In (GIS)
    signInWeb: function (onSuccess, onError) {
      // Google accepts the browser flow only on registered origins: installed apps (file://, capacitor://) use the code flow
      if (WEB_ORIGINS.indexOf(window.location.origin) === -1 || !WEB_CLIENT_ID) {
        GDrive.startDeviceFlow(onSuccess, onError);
        return;
      }
      if (!(window.google && window.google.accounts && window.google.accounts.oauth2)) {
        GDrive.startDeviceFlow(onSuccess, onError);
        return;
      }
      try {
        if (!activeTokenClient) {
          activeTokenClient = window.google.accounts.oauth2.initTokenClient({
            client_id: WEB_CLIENT_ID,
            scope: SCOPE,
            callback: function () {}
          });
        }
        activeTokenClient.callback = function (tokenResp) {
          if (tokenResp && tokenResp.access_token) {
            GDrive.fetchUserProfile(tokenResp.access_token, tokenResp.expires_in, onSuccess, onError);
          } else {
            GDrive.reportAuthError(new Error((tokenResp && tokenResp.error) || 'ההתחברות בוטלה'), onError);
          }
        };
        activeTokenClient.error_callback = function (e) {
          if (e && e.type === 'popup_failed_to_open') GDrive.startDeviceFlow(onSuccess, onError);
          else if (onError) onError(new Error('ההתחברות בוטלה'));
        };
        activeTokenClient.requestAccessToken({ prompt: '' });
      } catch (e) {
        console.warn('GIS error, falling back to the code flow:', e);
        GDrive.startDeviceFlow(onSuccess, onError);
      }
    },"""

new_signin_block = """    isTv: function () {
      if (typeof window !== 'undefined' && window.IS_TV) return true;
      var ua = (typeof navigator !== 'undefined' && navigator.userAgent) || '';
      if (/Web0S|webOS|Tizen|SMART-TV|SmartTV|AndroidTV|GoogleTV|AppleTV|HbbTV|NetCast|VIDAA|AFT[BMT]|POV_TV|BRAVIA|MiTV|MiBOX/i.test(ua)) return true;
      if (typeof window !== 'undefined' && window.AndroidApp && window.AndroidApp.isTv && window.AndroidApp.isTv()) return true;
      if (typeof window !== 'undefined' && window.location && new URLSearchParams(window.location.search).get('tv') === '1') return true;
      if (typeof window !== 'undefined' && window.TVUI) return true;
      return false;
    },

    signIn: function (onSuccess, onError) {
      if (GDrive.isTv()) {
        GDrive.startDeviceFlow(onSuccess, onError);
      } else {
        GDrive.signInWeb(onSuccess, onError);
      }
    },

    // ------------------------------------------------------------- Web Sign-In (GIS)
    signInWeb: function (onSuccess, onError) {
      // Smart TVs and limited environments must always use the Device Code Flow
      if (GDrive.isTv() || WEB_ORIGINS.indexOf(window.location.origin) === -1 || !WEB_CLIENT_ID) {
        GDrive.startDeviceFlow(onSuccess, onError);
        return;
      }
      if (!(window.google && window.google.accounts && window.google.accounts.oauth2)) {
        GDrive.startDeviceFlow(onSuccess, onError);
        return;
      }
      try {
        if (!activeTokenClient) {
          activeTokenClient = window.google.accounts.oauth2.initTokenClient({
            client_id: WEB_CLIENT_ID,
            scope: SCOPE,
            callback: function () {}
          });
        }
        activeTokenClient.callback = function (tokenResp) {
          if (tokenResp && tokenResp.access_token) {
            GDrive.fetchUserProfile(tokenResp.access_token, tokenResp.expires_in, onSuccess, onError);
          } else {
            console.warn('GIS returned no token, falling back to device flow:', tokenResp);
            GDrive.startDeviceFlow(onSuccess, onError);
          }
        };
        activeTokenClient.error_callback = function (e) {
          console.warn('GIS error callback, falling back to device flow:', e);
          GDrive.startDeviceFlow(onSuccess, onError);
        };
        activeTokenClient.requestAccessToken({ prompt: '' });
      } catch (e) {
        console.warn('GIS error, falling back to the code flow:', e);
        GDrive.startDeviceFlow(onSuccess, onError);
      }
    },"""

if old_signin_web_block in gdrive_code:
    gdrive_code = gdrive_code.replace(old_signin_web_block, new_signin_block)
    print("gdrive.js signInWeb patched!")
else:
    print("gdrive.js signInWeb target not found")

old_modal_block = """    showTvDeviceModal: function (userCode, verifyUrl) {
      var modalHtml = '<div style="text-align:center;padding:1.5rem 1rem;">' +
        '<div style="display:flex;justify-content:center;margin-bottom:1.2rem;">' +
        '<div id="gdrive-qr" style="background:#fff;padding:12px;border-radius:14px;display:inline-block;"></div>' +
        '</div>' +
        '<p style="font-size:1.15rem;margin:0 0 0.8rem;color:#e2e8f0;">1. סרקו את קוד ה-QR או היכנסו בנייד אל:</p>' +
        '<div style="font-size:1.35rem;font-weight:700;color:#60a5fa;margin-bottom:1.2rem;letter-spacing:0.02em;">' + verifyUrl + '</div>' +
        '<p style="font-size:1.15rem;margin:0 0 0.6rem;color:#e2e8f0;">2. הזינו את קוד ההתחברות:</p>' +
        '<div style="font-size:2.8rem;font-weight:900;letter-spacing:0.25em;background:rgba(255,255,255,0.08);' +
        'border:2px dashed #3b82f6;border-radius:16px;padding:0.6rem 1.2rem;display:inline-block;color:#facc15;font-family:monospace;">' +
        userCode + '</div>' +
        '<p style="font-size:0.95rem;color:#94a3b8;margin:1.4rem 0 0;">לאחר האישור, הטלוויזיה תתחבר אוטומטית לחשבון ה-Google Drive שלכם.</p>' +
        '</div>';

      if (window.openDialog) {
        window.openDialog({
          ico: '☁️',
          title: 'התחברות לחשבון Google והקלטות Drive',
          html: modalHtml,
          buttons: [{ label: 'ביטול' }],
          onClose: GDrive.stopDevicePoll
        });

        if (window.loadQrLib) {
          window.loadQrLib(function () {
            var qrBox = document.getElementById('gdrive-qr');
            if (!qrBox || !window.qrcode) return;
            try {
              var q = window.qrcode(0, 'M');
              q.addData(verifyUrl);
              q.make();
              qrBox.innerHTML = q.createSvgTag(5, 2);
            } catch (e) {}
          });
        }
      }
    },"""

new_modal_block = """    showTvDeviceModal: function (userCode, verifyUrl) {
      var baseVerify = verifyUrl || 'https://www.google.com/device';
      // Embedding the user_code parameter allows mobile QR scanners to automatically prefill the code on Google's page!
      var pairUrl = baseVerify + (baseVerify.indexOf('?') === -1 ? '?user_code=' : '&user_code=') + encodeURIComponent(userCode);

      var modalHtml = '<div style="display:flex;flex-wrap:wrap;align-items:center;justify-content:center;gap:2.5rem;padding:1.5rem 1rem;direction:rtl;text-align:right;">' +
        '<div style="text-align:center;background:#fff;padding:16px;border-radius:20px;box-shadow:0 12px 32px rgba(0,0,0,0.4);display:inline-block;">' +
        '<div id="gdrive-qr" style="width:200px;height:200px;display:flex;align-items:center;justify-content:center;color:#475569;font-size:0.95rem;font-weight:600;">' +
        '<span>טוען קוד QR...</span>' +
        '</div>' +
        '<div style="margin-top:8px;font-size:0.85rem;font-weight:700;color:#2563eb;letter-spacing:0.02em;">סריקה מהירה בנייד 📱</div>' +
        '</div>' +
        '<div style="max-width:400px;text-align:right;">' +
        '<div style="font-size:1.2rem;font-weight:800;color:#fff;margin-bottom:0.4rem;">1. סרקו את הקוד עם מצלמת הנייד</div>' +
        '<div style="font-size:0.95rem;color:#94a3b8;margin-bottom:1.2rem;line-height:1.4;">קוד ההתחברות יוזן אוטומטית בטלפון ללא צורך בהקלדה.</div>' +
        '<div style="font-size:1.1rem;font-weight:700;color:#fff;margin-bottom:0.4rem;">2. או היכנסו בדפדפן אל:</div>' +
        '<div style="font-size:1.2rem;font-weight:800;color:#60a5fa;margin-bottom:0.9rem;direction:ltr;text-align:right;user-select:all;">' + baseVerify + '</div>' +
        '<div style="font-size:1.1rem;font-weight:700;color:#fff;margin-bottom:0.4rem;">והזינו את הקוד:</div>' +
        '<div style="font-size:2.5rem;font-weight:900;letter-spacing:0.18em;background:rgba(255,255,255,0.08);' +
        'border:2px dashed #3b82f6;border-radius:14px;padding:0.4rem 1rem;display:inline-block;color:#facc15;font-family:monospace;direction:ltr;user-select:all;">' +
        userCode + '</div>' +
        '<div style="font-size:0.88rem;color:#94a3b8;margin-top:1.2rem;line-height:1.4;">לאחר האישור, הטלוויזיה תתחבר מיד לחשבון ה-Google Drive שלכם ותסנכרן את ההקלטות והמועדפים.</div>' +
        '</div>' +
        '</div>';

      if (window.openDialog) {
        window.openDialog({
          ico: '☁️',
          title: 'התחברות לחשבון Google בטלוויזיה',
          html: modalHtml,
          wide: true,
          buttons: [{ label: 'ביטול', primary: true }],
          onClose: GDrive.stopDevicePoll
        });

        var renderQr = function () {
          var qrBox = document.getElementById('gdrive-qr');
          if (!qrBox || !window.qrcode) return;
          try {
            var q = window.qrcode(0, 'M');
            q.addData(pairUrl);
            q.make();
            qrBox.innerHTML = q.createSvgTag(5, 0);
            var svg = qrBox.querySelector('svg');
            if (svg) {
              svg.style.width = '200px';
              svg.style.height = '200px';
              svg.style.display = 'block';
            }
          } catch (e) {
            console.warn('QR error, fallback to short url:', e);
            try {
              var q2 = window.qrcode(0, 'L');
              q2.addData(baseVerify);
              q2.make();
              qrBox.innerHTML = q2.createSvgTag(5, 0);
              var svg2 = qrBox.querySelector('svg');
              if (svg2) {
                svg2.style.width = '200px';
                svg2.style.height = '200px';
                svg2.style.display = 'block';
              }
            } catch (e2) {}
          }
        };

        if (window.loadQrLib) {
          window.loadQrLib(renderQr);
        } else if (window.qrcode) {
          renderQr();
        }
      }
    },"""

if old_modal_block in gdrive_code:
    gdrive_code = gdrive_code.replace(old_modal_block, new_modal_block)
    print("gdrive.js showTvDeviceModal patched!")
else:
    print("gdrive.js showTvDeviceModal target not found")

with open(gdrive_path, "w", encoding="utf-8") as f:
    f.write(gdrive_code)
print("gdrive.js updated successfully!")

# 5. Update index.html
index_path = os.path.join(ROOT, "index.html")
with open(index_path, "r", encoding="utf-8") as f:
    index_code = f.read().replace("\r\n", "\n")

target_index = """  <script src="assets/hls.min.js"></script>
  <script src="https://accounts.google.com/gsi/client" async defer></script>"""

repl_index = """  <script src="assets/hls.min.js"></script>
  <script src="assets/qrcode.js"></script>
  <script src="https://accounts.google.com/gsi/client" async defer></script>"""

if target_index in index_code:
    index_code = index_code.replace(target_index, repl_index)
    with open(index_path, "w", encoding="utf-8") as f:
        f.write(index_code)
    print("index.html patched with assets/qrcode.js!")
else:
    print("index.html target not found (may already contain qrcode.js)")
