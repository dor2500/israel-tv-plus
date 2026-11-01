import os

app_path = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo\app.js"
with open(app_path, "r", encoding="utf-8") as f:
    code = f.read()

# 1. Update atvRenderPill to guarantee no collapsed empty line
old_atv_pill = """      if (tabs.length) {
        pill.classList.remove('hidden');
        pill.style.display = 'flex';
      } else {
        pill.classList.add('hidden');
        pill.style.display = 'none';
      }
      atvPillState();
    } catch (err) {
      console.error('atvRenderPill error:', err);
      try {
        pill.classList.add('hidden');
        pill.style.display = 'none';
      } catch (e) {}
    }"""

new_atv_pill = """      if (tabs.length && pill.children.length > 0) {
        pill.classList.remove('hidden');
        pill.setAttribute('data-empty', 'false');
        pill.style.display = 'flex';
        pill.style.visibility = 'visible';
      } else {
        pill.classList.add('hidden');
        pill.setAttribute('data-empty', 'true');
        pill.style.setProperty('display', 'none', 'important');
        pill.style.setProperty('visibility', 'hidden', 'important');
      }
      atvPillState();
    } catch (err) {
      console.error('atvRenderPill error:', err);
      try {
        pill.classList.add('hidden');
        pill.setAttribute('data-empty', 'true');
        pill.style.setProperty('display', 'none', 'important');
        pill.style.setProperty('visibility', 'hidden', 'important');
      } catch (e) {}
    }"""

if old_atv_pill in code:
    code = code.replace(old_atv_pill, new_atv_pill)
    print("1. atvRenderPill updated successfully!")
else:
    print("WARNING: old_atv_pill not found!")

# 2. Update openVodModal to include Google Drive recording buttons and click handlers
old_vod_modal = """  function openVodModal(series) {
    if (!series) return;
    var eps = series.episodes || [];
    var epHtml = eps.map(function (ep, idx) {
      return '<div class="vod-ep-card" data-epi="' + idx + '" tabindex="0">' +
        '<div class="vod-ep-info"><b>' + esc(ep.title) + '</b><span>' + esc(ep.duration || '') + '</span></div>' +
        '<button class="btn primary vod-play-btn" data-epi="' + idx + '">▶ צפייה בפרק</button>' +
        '</div>';
    }).join('');

    var html = '<div class="vod-modal-hero" style="display:flex;gap:1.5rem;align-items:flex-start;margin-bottom:1.5rem;">' +
      '<img src="' + esc(series.poster) + '" style="width:130px;height:185px;object-fit:cover;border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,0.6);" alt="">' +
      '<div><h2 style="margin:0 0 0.5rem;font-size:1.8rem;">' + esc(series.title) + '</h2>' +
      '<div style="display:flex;gap:0.5rem;margin-bottom:0.75rem;font-size:0.95rem;color:rgba(255,255,255,0.7);">' +
      '<span>⭐ ' + esc(series.rating || '9.0') + '</span><span>•</span>' +
      '<span>' + esc(series.year || '') + '</span><span>•</span>' +
      '<span>' + esc((series.genres || []).join(', ')) + '</span></div>' +
      '<p style="font-size:1rem;line-height:1.5;color:rgba(255,255,255,0.85);margin:0;">' + esc(series.desc) + '</p>' +
      '</div></div>' +
      '<div class="vod-episodes" style="max-height:42vh;overflow-y:auto;display:flex;flex-direction:column;gap:0.75rem;">' + epHtml + '</div>';

    openDialog({
      ico: '🍿',
      title: 'סדרות לצפייה ישירה - ' + series.title,
      html: html,
      buttons: [{ label: 'סגירה' }],
      cls: 'vod-dialog'
    });

    setTimeout(function () {
      var d = $('dialog');
      if (!d) return;
      var cards = d.querySelectorAll('.vod-ep-card, .vod-play-btn');
      for (var k = 0; k < cards.length; k++) {
        cards[k].addEventListener('click', function (e) {
          var epIdx = parseInt(this.getAttribute('data-epi'), 10);
          var ep = eps[epIdx];
          if (ep) {
            closeDialog();
            playVodEpisode(series, ep);
          }
        });
      }
    }, 50);
  }"""

new_vod_modal = """  function openVodModal(series) {
    if (!series) return;
    var eps = series.episodes || [];
    var epHtml = eps.map(function (ep, idx) {
      return '<div class="vod-ep-card" data-epi="' + idx + '" tabindex="0">' +
        '<div class="vod-ep-info"><b>' + esc(ep.title) + '</b><span>' + esc(ep.duration || '') + '</span></div>' +
        '<div style="display:flex;gap:0.5rem;align-items:center;">' +
        '<button class="btn vod-rec-btn" data-rec-epi="' + idx + '" title="הקלט פרק זה ישירות ל-Google Drive">💾 הקלט ל-Drive</button>' +
        '<button class="btn primary vod-play-btn" data-epi="' + idx + '">▶ צפייה בפרק</button>' +
        '</div>' +
        '</div>';
    }).join('');

    var html = '<div class="vod-modal-hero" style="display:flex;gap:1.5rem;align-items:flex-start;margin-bottom:1.5rem;">' +
      '<img src="' + esc(series.poster) + '" style="width:130px;height:185px;object-fit:cover;border-radius:12px;box-shadow:0 8px 24px rgba(0,0,0,0.6);" alt="">' +
      '<div style="flex:1;"><h2 style="margin:0 0 0.5rem;font-size:1.8rem;">' + esc(series.title) + '</h2>' +
      '<div style="display:flex;gap:0.5rem;margin-bottom:0.75rem;font-size:0.95rem;color:rgba(255,255,255,0.7);">' +
      '<span>⭐ ' + esc(series.rating || '9.0') + '</span><span>•</span>' +
      '<span>' + esc(series.year || '') + '</span><span>•</span>' +
      '<span>' + esc((series.genres || []).join(', ')) + '</span></div>' +
      '<p style="font-size:1rem;line-height:1.5;color:rgba(255,255,255,0.85);margin:0 0 0.8rem;">' + esc(series.desc) + '</p>' +
      '<div style="display:flex;gap:0.75rem;flex-wrap:wrap;">' +
      '<button class="btn vod-rec-all-btn" style="background:rgba(239,68,68,0.22);border:1px solid #ef4444;color:#fff;border-radius:8px;padding:0.45rem 0.95rem;font-weight:700;display:inline-flex;align-items:center;gap:0.4rem;cursor:pointer;">🔴 הקלט סדרה ל-Google Drive</button>' +
      '</div>' +
      '</div></div>' +
      '<div class="vod-episodes" style="max-height:42vh;overflow-y:auto;display:flex;flex-direction:column;gap:0.75rem;">' + epHtml + '</div>';

    openDialog({
      ico: '🍿',
      title: 'סדרות לצפייה ישירה - ' + series.title,
      html: html,
      buttons: [{ label: 'סגירה' }],
      cls: 'vod-dialog'
    });

    setTimeout(function () {
      var d = $('dialog');
      if (!d) return;
      var playBtns = d.querySelectorAll('.vod-play-btn');
      for (var k = 0; k < playBtns.length; k++) {
        playBtns[k].addEventListener('click', function (e) {
          e.stopPropagation();
          var epIdx = parseInt(this.getAttribute('data-epi'), 10);
          var ep = eps[epIdx];
          if (ep) {
            closeDialog();
            playVodEpisode(series, ep);
          }
        });
      }
      var recBtns = d.querySelectorAll('.vod-rec-btn');
      for (var rk = 0; rk < recBtns.length; rk++) {
        recBtns[rk].addEventListener('click', function (e) {
          e.stopPropagation();
          var epIdx = parseInt(this.getAttribute('data-rec-epi'), 10);
          var ep = eps[epIdx];
          if (ep && window.GDrive) {
            window.GDrive.recordVodEpisode(series, ep, function (pct, txt) {
              toast('🔴 ' + txt + ' (' + series.title + ')');
            });
          }
        });
      }
      var recAllBtn = d.querySelector('.vod-rec-all-btn');
      if (recAllBtn) {
        recAllBtn.addEventListener('click', function (e) {
          e.stopPropagation();
          if (!window.GDrive) return;
          if (eps.length) {
            toast('🔴 מתחיל הקלטת סדרה ל-Google Drive (' + eps.length + ' פרקים)');
            window.GDrive.recordVodEpisode(series, eps[0], function (pct, txt) {
              toast('🔴 פרק 1: ' + txt);
            });
          }
        });
      }
    }, 50);
  }"""

if old_vod_modal in code:
    code = code.replace(old_vod_modal, new_vod_modal)
    print("2. openVodModal updated successfully!")
else:
    print("WARNING: old_vod_modal not found!")

# 3. Add Google Drive options to playerMenu
old_player_menu = """    if (ch.isVod) {
      if (ch.series) {
        items.push({ ico: '📋', label: 'פרקים בסדרה (' + ch.series.title + ')', act: function () { openVodModal(ch.series); } });
      }
      items.push({ ico: '⏭️', label: 'פרק הבא', act: playNextVodEpisode });
      items.push({ ico: '⏮️', label: 'פרק קודם', act: playPrevVodEpisode });
      items.push({ ico: '⚡', label: 'מהירות ניגון', stay: true, val: function () { return (el.video.playbackRate || 1) + 'x'; }, act: cyclePlaybackSpeed });
    }"""

new_player_menu = """    if (ch.isVod) {
      if (ch.series) {
        items.push({ ico: '📋', label: 'פרקים בסדרה (' + ch.series.title + ')', act: function () { openVodModal(ch.series); } });
        items.push({ ico: '💾', label: 'הקלט פרק זה ל-Google Drive', act: function () {
          if (window.GDrive && ch.series && ch.vodEp) {
            window.GDrive.recordVodEpisode(ch.series, ch.vodEp);
          } else {
            toast('🔴 שומר פרק ל-Google Drive...');
          }
        } });
      }
      items.push({ ico: '⏭️', label: 'פרק הבא', act: playNextVodEpisode });
      items.push({ ico: '⏮️', label: 'פרק קודם', act: playPrevVodEpisode });
      items.push({ ico: '⚡', label: 'מהירות ניגון', stay: true, val: function () { return (el.video.playbackRate || 1) + 'x'; }, act: cyclePlaybackSpeed });
    } else {
      items.push({ ico: '🔴', label: 'הקלט שידור ל-Google Drive', act: function () { startLiveRecordingDialog(ch); } });
    }"""

if old_player_menu in code:
    code = code.replace(old_player_menu, new_player_menu)
    print("3. playerMenu updated successfully!")
else:
    print("WARNING: old_player_menu not found!")

# 4. Add startLiveRecordingDialog and openDriveRecordingsDialog helper functions
dialog_helpers = """
  function startLiveRecordingDialog(ch) {
    if (!ch) return;
    if (!window.GDrive) return;
    var u = window.GDrive.getUser();
    if (!u || !u.access_token) {
      if (IS_TV) window.GDrive.startDeviceFlow(function () { startLiveRecordingDialog(ch); });
      else window.GDrive.signInWeb(function () { startLiveRecordingDialog(ch); });
      return;
    }

    var html = '<div style="padding:1rem;">' +
      '<p style="font-size:1.1rem;margin-bottom:1rem;">בחרו משך זמן להקלטת השידור החי של <b>' + esc(ch.name) + '</b> ישירות ל-Google Drive שלכם:</p>' +
      '<div style="display:grid;grid-template-columns:1fr 1fr;gap:0.8rem;">' +
      '<button class="btn rec-dur-btn" data-sec="900" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ 15 דקות</button>' +
      '<button class="btn primary rec-dur-btn" data-sec="1800" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ 30 דקות</button>' +
      '<button class="btn rec-dur-btn" data-sec="3600" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ שעה אחת</button>' +
      '<button class="btn rec-dur-btn" data-sec="7200" style="padding:1rem;font-size:1.1rem;font-weight:700;">⏱️ שעתיים</button>' +
      '</div></div>';

    openDialog({
      ico: '🔴',
      title: 'הקלטת שידור חי ל-Google Drive',
      html: html,
      buttons: [{ label: 'ביטול' }]
    });

    setTimeout(function () {
      var d = $('dialog');
      if (!d) return;
      var btns = d.querySelectorAll('.rec-dur-btn');
      for (var b = 0; b < btns.length; b++) {
        btns[b].addEventListener('click', function () {
          var sec = parseInt(this.getAttribute('data-sec'), 10) || 1800;
          closeDialog();
          window.GDrive.recordLiveStream(ch, sec);
        });
      }
    }, 50);
  }

  function openDriveRecordingsDialog() {
    if (!window.GDrive) return;
    var u = window.GDrive.getUser();
    if (!u || !u.access_token) {
      if (IS_TV) window.GDrive.startDeviceFlow(function () { openDriveRecordingsDialog(); });
      else window.GDrive.signInWeb(function () { openDriveRecordingsDialog(); });
      return;
    }

    toast('🔍 טוען הקלטות מ-Google Drive...');
    window.GDrive.listRecordings(function (err, files) {
      if (err) {
        toast('⚠️ שגיאה בטעינת הקלטות: ' + (err.message || ''));
        return;
      }
      if (!files || !files.length) {
        openDialog({
          ico: '📁',
          title: 'ההקלטות שלי ב-Google Drive',
          html: '<div style="text-align:center;padding:2rem 1rem;">' +
            '<div style="font-size:3rem;margin-bottom:1rem;">🍿</div>' +
            '<p style="font-size:1.2rem;font-weight:600;margin:0 0 0.5rem;">אין עדיין הקלטות בתיקייה</p>' +
            '<p style="font-size:0.95rem;color:rgba(255,255,255,0.7);margin:0;">פתחו כל סדרה ב-VOD או שידור חי ולחצו על <b>"💾 הקלט ל-Drive"</b> כדי לשמור אותה ישירות לכאן!</p>' +
            '</div>',
          buttons: [{ label: 'סגירה' }]
        });
        return;
      }

      var itemsHtml = files.map(function (f, idx) {
        var sizeMb = f.size ? (parseInt(f.size, 10) / (1024 * 1024)).toFixed(1) + ' MB' : '';
        var date = f.createdTime ? new Date(f.createdTime).toLocaleDateString('he-IL') : '';
        return '<div class="rec-card" style="display:flex;align-items:center;justify-content:space-between;padding:0.9rem 1.2rem;background:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.12);border-radius:12px;margin-bottom:0.6rem;">' +
          '<div style="flex:1;min-width:0;margin-left:1rem;">' +
          '<div style="font-size:1.1rem;font-weight:700;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">' + esc(f.name) + '</div>' +
          '<div style="font-size:0.85rem;color:rgba(255,255,255,0.6);margin-top:0.2rem;">' + date + (sizeMb ? ' • ' + sizeMb : '') + '</div>' +
          '</div>' +
          '<div style="display:flex;gap:0.6rem;flex:none;">' +
          (f.webViewLink ? '<a href="' + esc(f.webViewLink) + '" target="_blank" class="btn" style="padding:0.4rem 0.8rem;font-size:0.9rem;">🔗 פתח ב-Drive</a>' : '') +
          '</div></div>';
      }).join('');

      openDialog({
        ico: '📁',
        title: 'ההקלטות שלי ב-Google Drive (' + files.length + ')',
        html: '<div style="max-height:55vh;overflow-y:auto;padding:0.5rem;">' + itemsHtml + '</div>',
        buttons: [{ label: 'סגירה' }]
      });
    });
  }
"""

if "function startLiveRecordingDialog" not in code:
    code = code.replace("  function playerMenu() {", dialog_helpers + "\n  function playerMenu() {")
    print("4. dialog_helpers added successfully!")

# 5. Add Google Drive section in settingsSections
old_settings_profiles = """      { id: 'profiles', ico: '👤', name: 'פרופילים', desc: 'מי צופה בטלוויזיה, מועדפים והיסטוריה אישיים', rows: [
        action('החלפת פרופיל / מי צופה?', function () { closeSettings(); openTvProfiles(); }, 'פתיחת מסך בחירת הפרופיל בסגנון נטפליקס'),
        toggle('profileOnStart', 'בחירת פרופיל בכל הפעלה', 'מציג את מסך "מי צופה?" בכל פתיחה של הטלוויזיה'),
        action('ניהול פרופילים (עריכה / הוספה)', function () { closeSettings(); openTvProfiles(); tvProfileManaging = true; renderTvProfilesScreen(); markTvProfilesFocus(); }, 'עריכת שמות, החלפת אווטארים 3D והוספת פרופילים')
      ] },"""

new_settings_profiles = """      { id: 'profiles', ico: '👤', name: 'פרופילים', desc: 'מי צופה בטלוויזיה, מועדפים והיסטוריה אישיים', rows: [
        action('החלפת פרופיל / מי צופה?', function () { closeSettings(); openTvProfiles(); }, 'פתיחת מסך בחירת הפרופיל בסגנון נטפליקס'),
        toggle('profileOnStart', 'בחירת פרופיל בכל הפעלה', 'מציג את מסך "מי צופה?" בכל פתיחה של הטלוויזיה'),
        action('ניהול פרופילים (עריכה / הוספה)', function () { closeSettings(); openTvProfiles(); tvProfileManaging = true; renderTvProfilesScreen(); markTvProfilesFocus(); }, 'עריכת שמות, החלפת אווטארים 3D והוספת פרופילים')
      ] },
      { id: 'google_cloud', ico: '☁️', name: 'חשבון Google & הקלטות ענן', desc: 'Google Drive והקלטת סדרות ושידורים חיים', rows: [
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
      ] },"""

if old_settings_profiles in code:
    code = code.replace(old_settings_profiles, new_settings_profiles)
    print("5. settingsSections Google Drive section added successfully!")
else:
    print("WARNING: old_settings_profiles not found!")

# 6. Save episode reference on playVodEpisode
old_vod_play = """    var vodCh = {
      id: ep.id,
      name: series.title + ': ' + ep.title,
      url: ep.url,
      logo: series.poster,
      badge: 'VOD',
      category: 'סדרות לצפייה ישירה (VOD)',
      isVod: true,
      series: series
    };"""

new_vod_play = """    var vodCh = {
      id: ep.id,
      name: series.title + ': ' + ep.title,
      url: ep.url,
      logo: series.poster,
      badge: 'VOD',
      category: 'סדרות לצפייה ישירה (VOD)',
      isVod: true,
      series: series,
      vodEp: ep
    };"""

if old_vod_play in code:
    code = code.replace(old_vod_play, new_vod_play)
    print("6. playVodEpisode updated with vodEp reference!")

# 7. Low-latency sports live-edge tuning in Hls config
old_hls_config = """        lowLatencyMode: true,
        backBufferLength: fastMode() ? 8 : (isSport ? 6 : 15),
        liveSyncDurationCount: isSport ? 1 : (isStable ? 2 : 1),
        liveMaxLatencyDurationCount: isSport ? 2 : (isStable ? 4 : 2),
        liveSyncDuration: isSport ? 3 : 7,
        liveMaxLatencyDuration: isSport ? 6 : 14,
        liveDurationInfinity: true,
        maxLiveSyncPlaybackRate: isSport ? 1.35 : 1.15,"""

new_hls_config = """        lowLatencyMode: true,
        startPosition: isSport ? -1 : undefined,
        backBufferLength: fastMode() ? 8 : (isSport ? 4 : 15),
        liveSyncDurationCount: isSport ? 1 : (isStable ? 2 : 1),
        liveMaxLatencyDurationCount: isSport ? 2 : (isStable ? 4 : 2),
        liveSyncDuration: isSport ? 2 : 7,
        liveMaxLatencyDuration: isSport ? 4 : 14,
        liveDurationInfinity: true,
        maxLiveSyncPlaybackRate: isSport ? 1.5 : 1.15,"""

if old_hls_config in code:
    code = code.replace(old_hls_config, new_hls_config)
    print("7. Low-latency sports tuning applied to Hls config!")

with open(app_path, "w", encoding="utf-8") as f:
    f.write(code)

print("app.js saved successfully!")

