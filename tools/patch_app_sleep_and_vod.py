import os, re

p = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\app.js"
with open(p, "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace("\r\n", "\n")

# 1. Add Sleep Timer implementation and VOD resume to app.js
sleep_timer_code = """
  var sleepTimerId = null;
  var sleepTimerEndTime = 0;

  function setSleepTimer(minutes) {
    if (sleepTimerId) {
      clearTimeout(sleepTimerId);
      sleepTimerId = null;
      sleepTimerEndTime = 0;
    }
    if (!minutes || minutes <= 0) {
      toast('💤 טיימר שינה בוטל');
      renderSettings();
      return;
    }
    sleepTimerEndTime = Date.now() + minutes * 60 * 1000;
    toast('💤 טיימר שינה הופעל: יכבה בעוד ' + minutes + ' דקות');
    renderSettings();
    sleepTimerId = setTimeout(function () {
      sleepTimerId = null;
      sleepTimerEndTime = 0;
      if (el.video) {
        el.video.pause();
        el.video.muted = true;
      }
      closePlayer();
      toast('💤 כיבוי אוטומטי - שינה ערבה!');
      renderSettings();
    }, minutes * 60 * 1000);
  }

  function getSleepTimerRemaining() {
    if (!sleepTimerEndTime) return 0;
    var rem = Math.round((sleepTimerEndTime - Date.now()) / 60000);
    return rem > 0 ? rem : 0;
  }
"""

if "function setSleepTimer" not in c:
    c = c.replace(
        "  function cyclePlaybackSpeed() {",
        sleep_timer_code + "\n  function cyclePlaybackSpeed() {"
    )

# 2. VOD resume in playVodEpisode
old_play_vod = """    state.playing = vodCh;
    openPlayer();
    fillOsd(vodCh);
    showOsd();
    loadStream(vodCh);
  }"""

new_play_vod = """    state.playing = vodCh;
    openPlayer();
    fillOsd(vodCh);
    showOsd();
    loadStream(vodCh);
    try {
      var savedPos = parseFloat(localStorage.getItem('itv_vod_pos_' + ep.id) || '0');
      if (savedPos > 15) {
        setTimeout(function () {
          if (el.video && el.video.currentTime < 10) {
            el.video.currentTime = savedPos;
            toast('ממשיך מצפייה קודמת');
          }
        }, 1200);
      }
    } catch (ve) {}
  }"""

if old_play_vod in c:
    c = c.replace(old_play_vod, new_play_vod)

# 3. Add Sleep Timer in settingsSections under google_cloud or playback section
old_cloud_sec_end = """        action('🔄 סנכרן עכשיו עם Google Drive', function () { triggerCloudSync(true); }, 'סנכרון מיידי של ערוצים מועדפים, היסטוריה והגדרות מול הענן'),
        action('📁 ההקלטות שלי ב-Google Drive', function () { openDriveRecordingsDialog(); }, 'צפייה, ניגון וניהול של כל הסדרות והשידורים שהקלטתם בענן'),"""

new_cloud_sec_end = """        action('🔄 סנכרן עכשיו עם Google Drive', function () { triggerCloudSync(true); }, 'סנכרון מיידי של ערוצים מועדפים, היסטוריה והגדרות מול הענן'),
        action('📁 ההקלטות שלי ב-Google Drive', function () { openDriveRecordingsDialog(); }, 'צפייה, ניגון וניהול של כל הסדרות והשידורים שהקלטתם בענן'),
        action(function () {
          var rem = getSleepTimerRemaining();
          return rem > 0 ? '💤 טיימר שינה (פעיל: ' + rem + ' דקות)' : '💤 טיימר שינה (כיבוי אוטומטי)';
        }, function () {
          var cur = getSleepTimerRemaining();
          var next = cur === 0 ? 30 : cur <= 30 ? 60 : cur <= 60 ? 90 : 0;
          setSleepTimer(next);
        }, 'הגדרת טיימר לכיבוי אוטומטי של השידור והטלוויזיה (לחיצה משנה: 30 / 60 / 90 דק / כבוי)'),"""

if old_cloud_sec_end in c:
    c = c.replace(old_cloud_sec_end, new_cloud_sec_end)

with open(p, "w", encoding="utf-8") as f:
    f.write(c)

print("app.js updated successfully with Sleep Timer and VOD auto-resume!")

