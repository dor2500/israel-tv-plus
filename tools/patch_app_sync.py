import os

app_path = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\app.js"
with open(app_path, "r", encoding="utf-8") as f:
    code = f.read()

# 1. Add cloud sync helper functions
cloud_helpers = """
  function gatherCloudData() {
    var vodMap = {};
    try {
      for (var i = 0; i < localStorage.length; i++) {
        var k = localStorage.key(i);
        if (k && k.indexOf('itv_vod_pos_') === 0) {
          vodMap[k] = localStorage.getItem(k);
        }
      }
    } catch (e) {}

    return {
      profiles: tvProfiles || [],
      favorites: state.favorites || [],
      recents: state.recents || [],
      settings: S || {},
      vodProgress: vodMap,
      updatedAt: Date.now()
    };
  }

  function applyCloudData(d) {
    if (!d) return;
    try {
      if (Array.isArray(d.favorites)) {
        state.favorites = d.favorites;
        lsSet('israel_tv_favorites', state.favorites);
      }
      if (Array.isArray(d.recents)) {
        state.recents = d.recents;
        lsSet('israel_tv_recents', state.recents);
      }
      if (Array.isArray(d.profiles) && d.profiles.length) {
        tvProfiles = d.profiles;
        saveTvProfiles();
      }
      if (d.settings && typeof d.settings === 'object') {
        for (var k in d.settings) {
          if (k !== 'cols' && k !== 'uiScale') S[k] = d.settings[k];
        }
        saveSettings();
      }
      if (d.vodProgress && typeof d.vodProgress === 'object') {
        for (var vk in d.vodProgress) {
          try { localStorage.setItem(vk, d.vodProgress[vk]); } catch (ve) {}
        }
      }
      buildCategories();
      filterList();
      if (TVUI && typeof atvBuild === 'function') atvBuild();
    } catch (err) {
      console.error('applyCloudData error:', err);
    }
  }

  function triggerCloudSync(notify) {
    if (!window.GDrive || !window.GDrive.isSignedIn()) {
      if (notify) toast('⚠️ חשבון Google אינו מחובר. התחברו בהגדרות כדי לסנכרן.');
      return;
    }
    if (notify) toast('🔄 מסנכרן מול Google Drive...');
    var local = gatherCloudData();
    window.GDrive.syncData(local, function (appliedCloud) {
      if (appliedCloud) {
        applyCloudData(appliedCloud);
        if (notify) toast('✅ הגדרות ומועדפים עודכנו מהענן!');
      } else {
        if (notify) toast('✅ הנתונים וההגדרות נשמרו בענן!');
      }
    }, function (err) {
      if (notify) toast('⚠️ שגיאה בסנכרון: ' + (err.message || ''));
    });
  }

  var syncDebounceTimer = null;
  function scheduleCloudSync() {
    if (!window.GDrive || !window.GDrive.isSignedIn()) return;
    clearTimeout(syncDebounceTimer);
    syncDebounceTimer = setTimeout(function () {
      triggerCloudSync(false);
    }, 4000);
  }
"""

if "function gatherCloudData" not in code:
    code = code.replace("  function openDriveRecordingsDialog() {", cloud_helpers + "\n  function openDriveRecordingsDialog() {")
    print("1. Added gatherCloudData and triggerCloudSync to app.js!")

# 2. Update toggleFav and saveTvProfiles to trigger scheduleCloudSync
if "scheduleCloudSync();" not in code:
    code = code.replace("lsSet('israel_tv_favorites', state.favorites);", "lsSet('israel_tv_favorites', state.favorites);\n    scheduleCloudSync();")
    print("2. Added scheduleCloudSync to favorites!")

# 3. Update settings section for Google Cloud with native Google feel and Sync controls
old_sec = """      { id: 'google_cloud', ico: '☁️', name: 'חשבון Google & הקלטות ענן', desc: 'Google Drive והקלטת סדרות ושידורים חיים', rows: [
        { type: 'card', state: function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          return (u && u.access_token) ? 'connected' : 'disconnected';
        }, html: function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          if (u && u.access_token) {
            var pic = u.picture ? '<img class="gdrive-avatar" src="' + esc(u.picture) + '" alt="">' : '<div class="gdrive-avatar" style="background:#3b82f6;display:flex;align-items:center;justify-content:center;font-size:1.5rem;color:#fff;">👤</div>';
            return '<div class="gdrive-card">' +
              '<div class="gdrive-user-row">' + pic +
              '<div><div style="font-size:1.25rem;font-weight:700;">' + esc(u.name || 'משתמש מחובר') + '</div>' +
              '<div style="font-size:0.95rem;color:rgba(255,255,255,0.7);margin-bottom:0.4rem;">' + esc(u.email || '') + '</div>' +
              '<span class="gdrive-badge-connected">● מחובר ל-Google Drive</span></div></div>' +
              '<div style="margin-top:1rem;font-size:0.95rem;color:rgba(255,255,255,0.85);">' +
              '📁 תיקיית הקלטות: <b>ישראל TV+ - הקלטות</b></div></div>';
          }
          return '<div class="gdrive-card">' +
            '<div style="display:flex;align-items:center;gap:1rem;margin-bottom:0.8rem;">' +
            '<div style="width:48px;height:48px;border-radius:12px;background:rgba(255,255,255,0.1);display:flex;align-items:center;justify-content:center;font-size:1.8rem;">☁️</div>' +
            '<div><div style="font-size:1.2rem;font-weight:700;">חשבון Google אינו מחובר</div>' +
            '<div style="font-size:0.95rem;color:rgba(255,255,255,0.7);">התחברו כדי להקליט סדרות ושידורים חיים ישירות ל-Google Drive</div></div></div></div>';
        }, act: function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          if (u && u.access_token) {
            confirmBox('להתנתק מחשבון Google?', function () {
              if (window.GDrive) window.GDrive.signOut(function () { renderSettings(); });
            });
          } else {
            if (window.GDrive) {
              if (IS_TV) window.GDrive.startDeviceFlow(function () { renderSettings(); });
              else window.GDrive.signInWeb(function () { renderSettings(); });
            }
          }
        } },
        action('📁 ההקלטות שלי ב-Google Drive', function () { openDriveRecordingsDialog(); }, 'צפייה וניהול בכל הסדרות והשידורים שהקלטתם בענן'),
        action(function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          return (u && u.access_token) ? '🚪 התנתק מחשבון Google' : '🔑 התחבר עם חשבון Google';
        }, function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          if (u && u.access_token) {
            confirmBox('להתנתק מחשבון Google?', function () {
              if (window.GDrive) window.GDrive.signOut(function () { renderSettings(); });
            });
          } else {
            if (window.GDrive) {
              if (IS_TV) window.GDrive.startDeviceFlow(function () { renderSettings(); });
              else window.GDrive.signInWeb(function () { renderSettings(); });
            }
          }
        })
      ] }"""

new_sec = """      { id: 'google_cloud', ico: '☁️', name: 'חשבון Google & סנכרון ענן', desc: 'חיבור Google Drive, סנכרון מועדפים והגדרות והקלטות', rows: [
        { type: 'card', state: function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          return (u && u.access_token) ? 'connected' : 'disconnected';
        }, html: function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          if (u && u.access_token) {
            var pic = u.picture ? '<img class="gdrive-avatar" src="' + esc(u.picture) + '" alt="">' : '<div class="gdrive-avatar" style="background:#4285F4;display:flex;align-items:center;justify-content:center;font-size:1.6rem;color:#fff;">G</div>';
            return '<div class="gdrive-card" style="border:1.5px solid rgba(66,133,244,0.4);background:linear-gradient(135deg, rgba(66,133,244,0.12) 0%, rgba(15,23,42,0.4) 100%);">' +
              '<div class="gdrive-user-row">' + pic +
              '<div><div style="font-size:1.3rem;font-weight:800;color:#fff;">' + esc(u.name || 'משתמש מחובר') + '</div>' +
              '<div style="font-size:0.95rem;color:rgba(255,255,255,0.75);margin-bottom:0.4rem;">' + esc(u.email || '') + '</div>' +
              '<span class="gdrive-badge-connected">● מחובר ל-Google Drive (סנכרון ענן פעיל)</span></div></div>' +
              '<div style="margin-top:1.1rem;padding-top:0.9rem;border-top:1px solid rgba(255,255,255,0.12);display:flex;justify-content:space-between;align-items:center;font-size:0.92rem;color:rgba(255,255,255,0.85);">' +
              '<span>📁 תיקיית ענן: <b>ישראל TV+ - הקלטות</b></span>' +
              '<span style="color:#94a3b8;">סונכרן: ' + (window.GDrive ? window.GDrive.getLastSyncFormatted() : '') + '</span></div></div>';
          }
          return '<div class="gdrive-card" style="border:1.5px solid rgba(255,255,255,0.16);">' +
            '<div style="display:flex;align-items:center;gap:1.2rem;margin-bottom:1rem;">' +
            '<div style="width:52px;height:52px;border-radius:14px;background:#fff;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(0,0,0,0.3);">' +
            '<svg width="28" height="28" viewBox="0 0 24 24"><path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/><path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/><path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/><path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/></svg>' +
            '</div>' +
            '<div style="flex:1;">' +
            '<div style="font-size:1.25rem;font-weight:800;color:#fff;">התחברות עם חשבון Google</div>' +
            '<div style="font-size:0.92rem;color:rgba(255,255,255,0.7);line-height:1.4;">סנכרון ערוצים מועדפים, היסטוריה, פרופילים והגדרות בין כל המכשירים, והקלטה ישירה לענן.</div>' +
            '</div></div></div>';
        }, act: function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          if (u && u.access_token) {
            confirmBox('להתנתק מחשבון Google?', function () {
              if (window.GDrive) window.GDrive.signOut(function () { renderSettings(); });
            });
          } else {
            if (window.GDrive) {
              if (IS_TV) window.GDrive.startDeviceFlow(function () { renderSettings(); });
              else window.GDrive.signInWeb(function () { renderSettings(); });
            }
          }
        } },
        action('🔄 סנכרן עכשיו עם Google Drive', function () { triggerCloudSync(true); }, 'סנכרון מיידי של ערוצים מועדפים, היסטוריה והגדרות מול הענן'),
        action('📁 ההקלטות שלי ב-Google Drive', function () { openDriveRecordingsDialog(); }, 'צפייה, ניגון וניהול של כל הסדרות והשידורים שהקלטתם בענן'),
        action(function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          return (u && u.access_token) ? '🚪 התנתק מחשבון Google' : '🔑 התחבר עם חשבון Google';
        }, function () {
          var u = (window.GDrive && window.GDrive.getUser()) || null;
          if (u && u.access_token) {
            confirmBox('להתנתק מחשבון Google?', function () {
              if (window.GDrive) window.GDrive.signOut(function () { renderSettings(); });
            });
          } else {
            if (window.GDrive) {
              if (IS_TV) window.GDrive.startDeviceFlow(function () { renderSettings(); });
              else window.GDrive.signInWeb(function () { renderSettings(); });
            }
          }
        })
      ] }"""

if old_sec in code:
    code = code.replace(old_sec, new_sec)
    print("3. Updated google_cloud section in app.js!")
else:
    print("WARNING: old_sec not matched in app.js")

# 4. On startup, trigger cloud sync if signed in
old_startup = "if (S.autoUpdate) setTimeout(function () { checkUpdates(false); }, 4000);"
new_startup = "if (S.autoUpdate) setTimeout(function () { checkUpdates(false); }, 4000);\n    if (window.GDrive && window.GDrive.isSignedIn()) setTimeout(function () { triggerCloudSync(false); }, 3000);"
if old_startup in code:
    code = code.replace(old_startup, new_startup)
    print("4. Added startup cloud sync trigger to app.js!")

with open(app_path, "w", encoding="utf-8") as f:
    f.write(code)

print("app.js saved successfully!")
