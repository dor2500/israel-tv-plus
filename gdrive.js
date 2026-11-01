/**
 * Israel TV+ Google Identity & Google Drive Cloud Recording Module
 * Handles OAuth 2.0 (GIS Web / Device Authorization Flow for Smart TVs),
 * cloud folder creation ("ישראל TV+ - הקלטות"), and resumable recording
 * of VOD series episodes and live broadcasts directly to user's Google Drive.
 */
(function (window) {
  'use strict';

  // OAuth clients of the project in Google Cloud Console (docs/GOOGLE_SIGNIN.md). They are public by design:
  // Google treats the secret of a "TVs and Limited Input" client as non-confidential.
  // While empty, sign-in explains that it is unavailable instead of sending people to a Google error page.
  var WEB_CLIENT_ID = '233862122434-psa4t9s1vehqgss7ocqm2bitbq546t4n.apps.googleusercontent.com';
  var TV_CLIENT_ID = '233862122434-saudlfl3pnicmr2ap94mo4900bhn0neu.apps.googleusercontent.com';
  var TV_CLIENT_SECRET = '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';
  var WEB_ORIGINS = ['https://dor2500.github.io', 'http://localhost:8080'];
  var SCOPE = 'openid email profile https://www.googleapis.com/auth/drive.file';
  var FOLDER_NAME = 'ישראל TV+ - הקלטות';
  var AUTH_KEY = 'itv_google_auth';
  var FOLDER_KEY = 'itv_gdrive_folder_id';

  var activeTokenClient = null;
  var devicePollTimer = null;
  var activeRecording = null;

  function jsonOk(r) {
    return r.json().then(function (body) {
      if (!r.ok) throw new Error((body && body.error && (body.error.message || body.error)) || ('HTTP ' + r.status));
      return body;
    });
  }

  var GDrive = {
    // ------------------------------------------------------------- Auth State
    getUser: function () {
      try {
        var raw = localStorage.getItem(AUTH_KEY);
        if (!raw) return null;
        var data = JSON.parse(raw);
        if (data && data.access_token) {
          // If expired, return data but mark expired
          var isExpired = data.expires_at && (Date.now() > data.expires_at);
          data.isExpired = isExpired;
          return data;
        }
      } catch (e) {}
      return null;
    },

    saveUser: function (user) {
      try {
        if (!user) {
          localStorage.removeItem(AUTH_KEY);
        } else {
          localStorage.setItem(AUTH_KEY, JSON.stringify(user));
        }
      } catch (e) {}
      if (typeof window.onGoogleAuthChange === 'function') {
        window.onGoogleAuthChange(user);
      }
    },

    isSignedIn: function () {
      var u = GDrive.getUser();
      return !!(u && u.access_token && (!u.isExpired || u.refresh_token));
    },

    // Passes through when nobody is signed in: the callers start the sign-in themselves.
    ensureToken: function (callback) {
      var u = GDrive.getUser();
      if (!u || !u.isExpired) { callback(null); return; }
      if (!u.refresh_token) { callback(new Error('פג תוקף ההתחברות – התנתקו והתחברו שוב בהגדרות')); return; }
      var params = new URLSearchParams();
      params.append('client_id', TV_CLIENT_ID);
      params.append('client_secret', TV_CLIENT_SECRET);
      params.append('refresh_token', u.refresh_token);
      params.append('grant_type', 'refresh_token');
      fetch('https://oauth2.googleapis.com/token', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
      })
      .then(jsonOk)
      .then(function (t) {
        u.access_token = t.access_token;
        u.expires_at = Date.now() + ((t.expires_in || 3600) * 1000);
        delete u.isExpired;
        GDrive.saveUser(u);
        callback(null);
      })
      .catch(function () {
        callback(new Error('פג תוקף ההתחברות – התנתקו והתחברו שוב בהגדרות'));
      });
    },

    reportAuthError: function (err, onError) {
      console.warn('Google sign-in:', err);
      if (onError) { onError(err); return; }
      if (window.openDialog) {
        window.openDialog({
          ico: '⚠️',
          title: 'ההתחברות ל-Google נכשלה',
          html: '<p>' + String((err && err.message) || err) + '</p>'
        });
      }
    },

    reportUnavailable: function (onError) {
      GDrive.reportAuthError(new Error('התחברות עם Google עדיין לא זמינה בגרסה הזו. הצפייה וכל שאר התכונות עובדות כרגיל.'), onError);
    },

    isTv: function () {
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
    },

    // ------------------------------------------------------------- Smart TV Device Code Flow
    stopDevicePoll: function () {
      if (devicePollTimer) {
        clearTimeout(devicePollTimer);
        devicePollTimer = null;
      }
    },

    startDeviceFlow: function (onSuccess, onError) {
      GDrive.stopDevicePoll();
      if (!TV_CLIENT_ID) {
        GDrive.reportUnavailable(onError);
        return;
      }

      // Request device code
      var params = new URLSearchParams();
      params.append('client_id', TV_CLIENT_ID);
      params.append('scope', SCOPE);

      fetch('https://oauth2.googleapis.com/device/code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
      })
      .then(jsonOk)
      .then(function (data) {
        var deviceCode = data.device_code;
        var interval = (data.interval || 5) * 1000;
        var deadline = Date.now() + (data.expires_in || 1800) * 1000;

        // Display TV dialog
        GDrive.showTvDeviceModal(data.user_code, data.verification_url || 'https://www.google.com/device');

        var fail = function (message) {
          GDrive.stopDevicePoll();
          GDrive.closeTvDeviceModal();
          GDrive.reportAuthError(new Error(message), onError);
        };

        // Poll for completion
        var poll = function () {
          if (Date.now() > deadline) {
            fail('פג תוקף הקוד. אנא נסו שוב.');
            return;
          }
          var pollParams = new URLSearchParams();
          pollParams.append('client_id', TV_CLIENT_ID);
          pollParams.append('client_secret', TV_CLIENT_SECRET);
          pollParams.append('device_code', deviceCode);
          pollParams.append('grant_type', 'urn:ietf:params:oauth:grant-type:device_code');

          fetch('https://oauth2.googleapis.com/token', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: pollParams.toString()
          })
          .then(function (tr) { return tr.json(); })
          .then(function (tData) {
            if (!devicePollTimer) return;   // cancelled meanwhile
            if (tData.access_token) {
              GDrive.stopDevicePoll();
              GDrive.closeTvDeviceModal();
              GDrive.fetchUserProfile(tData.access_token, tData.expires_in, onSuccess, onError, tData.refresh_token);
            } else if (tData.error === 'authorization_pending') {
              devicePollTimer = setTimeout(poll, interval);
            } else if (tData.error === 'slow_down') {
              interval += 5000;
              devicePollTimer = setTimeout(poll, interval);
            } else if (tData.error === 'access_denied') {
              fail('ההתחברות נדחתה בחשבון Google.');
            } else {
              fail('שגיאה מ-Google: ' + (tData.error || 'לא ידועה'));
            }
          })
          .catch(function () {
            // a network hiccup on a TV is not the end of the flow
            if (devicePollTimer) devicePollTimer = setTimeout(poll, interval);
          });
        };
        devicePollTimer = setTimeout(poll, interval);
      })
      .catch(function (err) {
        GDrive.reportAuthError(new Error('לא ניתן להתחיל התחברות ל-Google (' + err.message + ')'), onError);
      });
    },

    showTvDeviceModal: function (userCode, verifyUrl) {
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
    },

    closeTvDeviceModal: function () {
      if (window.closeDialog) window.closeDialog();
    },

    // ------------------------------------------------------------- Profile Retrieval
    fetchUserProfile: function (accessToken, expiresIn, onSuccess, onError, refreshToken) {
      fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
        headers: { 'Authorization': 'Bearer ' + accessToken }
      })
      .then(function (r) {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      })
      .then(function (info) {
        var user = {
          email: info.email,
          name: info.name || info.email.split('@')[0],
          picture: info.picture || '',
          access_token: accessToken,
          refresh_token: refreshToken || '',
          expires_at: Date.now() + ((expiresIn || 3600) * 1000)
        };
        GDrive.saveUser(user);
        // Pre-create/verify Israel TV+ Recordings folder
        GDrive.ensureFolder(accessToken, function () {});
        if (window.toast) {
          window.toast('✅ מחובר ל-Google: ' + user.name);
        }
        if (onSuccess) onSuccess(user);
      })
      .catch(function (err) {
        GDrive.reportAuthError(new Error('לא ניתן לקרוא את פרטי חשבון Google (' + err.message + ')'), onError);
      });
    },

    signOut: function (onComplete) {
      var u = GDrive.getUser();
      if (u && u.access_token) {
        fetch('https://oauth2.googleapis.com/revoke?token=' + encodeURIComponent(u.refresh_token || u.access_token), {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).catch(function () {});
      }
      GDrive.saveUser(null);
      try { localStorage.removeItem(FOLDER_KEY); } catch (e) {}
      if (window.toast) window.toast('🚪 התנתקת מחשבון Google');
      if (onComplete) onComplete();
    },

    // ------------------------------------------------------------- Drive Folder Management
    ensureFolder: function (accessToken, callback) {
      var cachedId = localStorage.getItem(FOLDER_KEY);
      if (cachedId) {
        callback(null, cachedId);
        return;
      }

      var q = encodeURIComponent("name = '" + FOLDER_NAME + "' and mimeType = 'application/vnd.google-apps.folder' and trashed = false");
      fetch('https://www.googleapis.com/drive/v3/files?q=' + q + '&fields=files(id,name)', {
        headers: { 'Authorization': 'Bearer ' + accessToken }
      })
      .then(jsonOk)
      .then(function (data) {
        if (data.files && data.files.length) {
          var folderId = data.files[0].id;
          localStorage.setItem(FOLDER_KEY, folderId);
          callback(null, folderId);
        } else {
          // Create folder
          fetch('https://www.googleapis.com/drive/v3/files', {
            method: 'POST',
            headers: {
              'Authorization': 'Bearer ' + accessToken,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify({
              name: FOLDER_NAME,
              mimeType: 'application/vnd.google-apps.folder',
              description: 'תיקיית הקלטות ענן של ישראל TV+ עבור סדרות ושידורים חיים'
            })
          })
          .then(jsonOk)
          .then(function (newFolder) {
            var fId = newFolder.id;
            localStorage.setItem(FOLDER_KEY, fId);
            callback(null, fId);
          })
          .catch(function (err) { callback(err, null); });
        }
      })
      .catch(function (err) { callback(err, null); });
    },

    // ------------------------------------------------------------- List Recordings
    listRecordings: function (callback) {
      var user = GDrive.getUser();
      if (!user || !user.access_token) {
        callback(new Error('משתמש לא מחובר'), []);
        return;
      }
      GDrive.ensureFolder(user.access_token, function (err, folderId) {
        if (err || !folderId) {
          callback(err || new Error('תיקייה לא נמצאה'), []);
          return;
        }
        var q = encodeURIComponent("'" + folderId + "' in parents and trashed = false");
        fetch('https://www.googleapis.com/drive/v3/files?q=' + q + '&fields=files(id,name,size,createdTime,webViewLink,webContentLink,thumbnailLink,videoMediaMetadata)&orderBy=createdTime+desc', {
          headers: { 'Authorization': 'Bearer ' + user.access_token }
        })
        .then(jsonOk)
        .then(function (data) {
          callback(null, data.files || []);
        })
        .catch(function (e) { callback(e, []); });
      });
    },

    // ------------------------------------------------------------- Record VOD Series Episode
    recordVodEpisode: function (series, episode, onProgress, onDone, onError) {
      var user = GDrive.getUser();
      if (!user || !user.access_token) {
        GDrive.signInWeb(function () {
          GDrive.recordVodEpisode(series, episode, onProgress, onDone, onError);
        }, onError);
        return;
      }

      var fileName = series.title + ' - ' + episode.title + '.mp4';
      var streamUrl = episode.url;

      if (window.toast) {
        window.toast('🔴 מתחיל הקלטה ל-Google Drive: ' + series.title + ' ' + episode.title);
      }

      GDrive.ensureFolder(user.access_token, function (fErr, folderId) {
        if (fErr || !folderId) {
          if (onError) onError(fErr || new Error('שגיאה ביצירת תיקיית הקלטות'));
          return;
        }

        // Fetch stream content (via Blob or proxy stream)
        var xhr = new XMLHttpRequest();
        xhr.open('GET', streamUrl, true);
        xhr.responseType = 'blob';

        xhr.onprogress = function (e) {
          if (e.lengthComputable && onProgress) {
            var pct = Math.round((e.loaded / e.total) * 50);
            onProgress(pct, 'מוריד שידור: ' + pct + '%');
          }
        };

        xhr.onload = function () {
          if (xhr.status >= 200 && xhr.status < 300) {
            var blob = xhr.response;
            GDrive.uploadFileToDrive(user.access_token, folderId, fileName, blob, function (upPct) {
              var totalPct = 50 + Math.round(upPct * 0.5);
              if (onProgress) onProgress(totalPct, 'מעלה ל-Google Drive: ' + totalPct + '%');
            }, function (result) {
              if (window.toast) {
                window.toast('✅ ' + fileName + ' נשמר בהצלחה ב-Google Drive!');
              }
              if (onDone) onDone(result);
            }, onError);
          } else {
            // If direct blob download is restricted by CORS/manifest, record stream directly via segment upload
            GDrive.createStreamReferenceRecording(user.access_token, folderId, series, episode, onDone, onError);
          }
        };

        xhr.onerror = function () {
          // Fallback to stream descriptor in Google Drive folder
          GDrive.createStreamReferenceRecording(user.access_token, folderId, series, episode, onDone, onError);
        };

        xhr.send();
      });
    },

    createStreamReferenceRecording: function (token, folderId, series, ep, onDone, onError) {
      var fileName = series.title + ' - ' + ep.title + '.m3u8';
      var content = '#EXTM3U\n#EXT-X-VERSION:3\n#EXTINF:-1,' + series.title + ' - ' + ep.title + '\n' + ep.url + '\n';
      var blob = new Blob([content], { type: 'application/vnd.apple.mpegurl' });

      GDrive.uploadFileToDrive(token, folderId, fileName, blob, function () {}, function (res) {
        if (window.toast) {
          window.toast('🔗 נשמר קישור לפרק ב-Google Drive (לא קובץ וידאו): ' + series.title);
        }
        if (onDone) onDone(res);
      }, onError);
    },

    // ------------------------------------------------------------- Live Broadcast Recording
    recordLiveStream: function (channel, durationSec, onProgress, onDone, onError) {
      var user = GDrive.getUser();
      if (!user || !user.access_token) {
        GDrive.signInWeb(function () {
          GDrive.recordLiveStream(channel, durationSec, onProgress, onDone, onError);
        }, onError);
        return;
      }

      var videoEl = document.getElementById('video');
      if (!videoEl) {
        if (onError) onError(new Error('נגן הווידאו אינו פעיל'));
        return;
      }

      var stream = null;
      try {
        if (videoEl.captureStream) {
          stream = videoEl.captureStream();
        } else if (videoEl.mozCaptureStream) {
          stream = videoEl.mozCaptureStream();
        }
      } catch (e) {}

      var d = new Date();
      var dateStr = d.toLocaleDateString('he-IL').replace(/\//g, '-') + '_' + d.getHours() + '-' + d.getMinutes();
      var fileName = channel.name + ' - ' + dateStr + '.webm';

      if (window.toast) {
        window.toast('🔴 התחילה הקלטת ענן ל-Google Drive: ' + channel.name + ' (' + Math.round(durationSec / 60) + ' דקות)');
      }

      if (stream && typeof MediaRecorder !== 'undefined') {
        var recordedChunks = [];
        var recorder = new MediaRecorder(stream, { mimeType: 'video/webm;codecs=vp8,opus' });

        recorder.ondataavailable = function (e) {
          if (e.data && e.data.size > 0) recordedChunks.push(e.data);
        };

        recorder.onstop = function () {
          var blob = new Blob(recordedChunks, { type: 'video/webm' });
          if (window.toast) window.toast('☁️ מעלה הקלטה ל-Google Drive...');
          GDrive.ensureFolder(user.access_token, function (fErr, folderId) {
            if (fErr || !folderId) {
              if (onError) onError(fErr);
              return;
            }
            GDrive.uploadFileToDrive(user.access_token, folderId, fileName, blob, onProgress, onDone, onError);
          });
        };

        recorder.start(1000);
        activeRecording = recorder;

        var elapsed = 0;
        var intv = setInterval(function () {
          elapsed++;
          if (onProgress) {
            var pct = Math.round((elapsed / durationSec) * 100);
            onProgress(pct, 'מקליט: ' + elapsed + ' / ' + durationSec + ' שניות');
          }
          if (elapsed >= durationSec) {
            clearInterval(intv);
            if (recorder.state === 'recording') recorder.stop();
          }
        }, 1000);
      } else {
        // Fallback: direct stream capture & metadata saving
        GDrive.ensureFolder(user.access_token, function (fErr, folderId) {
          if (fErr || !folderId) return;
          var playlistContent = '#EXTM3U\n#EXTINF:-1,' + channel.name + ' - שידור ישיר\n' + (channel.url || '');
          var blob = new Blob([playlistContent], { type: 'application/vnd.apple.mpegurl' });
          if (window.toast) window.toast('🔗 המכשיר לא תומך בהקלטת וידאו – נשמר קישור לשידור ב-Drive');
          GDrive.uploadFileToDrive(user.access_token, folderId, channel.name + ' - ' + dateStr + '.m3u8', blob, onProgress, onDone, onError);
        });
      }
    },

    stopActiveRecording: function () {
      if (activeRecording && activeRecording.state === 'recording') {
        activeRecording.stop();
        activeRecording = null;
        if (window.toast) window.toast('⏹️ ההקלטה הופסקה ונשמרת כעת ב-Google Drive');
      }
    },

    // ------------------------------------------------------------- Resumable Google Drive Upload
    
    // ------------------------------------------------------------- Cloud Data & Settings Sync
    getLastSyncFormatted: function () {
      try {
        var t = parseInt(localStorage.getItem('itv_last_sync_time') || '0', 10);
        if (!t) return 'טרם בוצע סנכרון';
        var diff = Math.floor((Date.now() - t) / 1000);
        if (diff < 60) return 'זה עתה';
        if (diff < 3600) return 'לפני ' + Math.floor(diff / 60) + ' דקות';
        if (diff < 86400) return 'לפני ' + Math.floor(diff / 3600) + ' שעות';
        return new Date(t).toLocaleDateString('he-IL');
      } catch (e) {
        return 'טרם בוצע סנכרון';
      }
    },

    syncData: function (localPayload, onComplete, onError) {
      var user = GDrive.getUser();
      if (!user || !user.access_token) {
        if (onError) onError(new Error('משתמש אינו מחובר'));
        return;
      }

      GDrive.ensureFolder(user.access_token, function (fErr, folderId) {
        if (fErr || !folderId) {
          if (onError) onError(fErr || new Error('תיקייה לא נמצאה'));
          return;
        }

        var fileName = 'israel_tv_backup.json';
        var q = encodeURIComponent("name = '" + fileName + "' and '" + folderId + "' in parents and trashed = false");

        fetch('https://www.googleapis.com/drive/v3/files?q=' + q + '&fields=files(id,name,modifiedTime)', {
          headers: { 'Authorization': 'Bearer ' + user.access_token }
        })
        .then(jsonOk)
        .then(function (data) {
          if (data.files && data.files.length) {
            var fileId = data.files[0].id;
            // Fetch cloud data to compare timestamps
            fetch('https://www.googleapis.com/drive/v3/files/' + fileId + '?alt=media', {
              headers: { 'Authorization': 'Bearer ' + user.access_token }
            })
            .then(jsonOk)
            .then(function (cloudJson) {
              var cloudTime = (cloudJson && cloudJson.updatedAt) || 0;
              var localTime = (localPayload && localPayload.updatedAt) || 0;

              if (cloudTime > localTime) {
                // Cloud has newer data, apply to local
                localStorage.setItem('itv_last_sync_time', String(Date.now()));
                if (onComplete) onComplete(cloudJson);
              } else {
                // Local is newer or equal, update cloud file (the phone app keeps its profiles in the same file)
                if (cloudJson && cloudJson.phoneProfiles) localPayload.phoneProfiles = cloudJson.phoneProfiles;
                fetch('https://www.googleapis.com/upload/drive/v3/files/' + fileId + '?uploadType=media', {
                  method: 'PATCH',
                  headers: {
                    'Authorization': 'Bearer ' + user.access_token,
                    'Content-Type': 'application/json; charset=UTF-8'
                  },
                  body: JSON.stringify(localPayload)
                })
                .then(jsonOk)
                .then(function () {
                  localStorage.setItem('itv_last_sync_time', String(Date.now()));
                  if (onComplete) onComplete(null);
                })
                .catch(function (upErr) { if (onError) onError(upErr); });
              }
            })
            .catch(function (readErr) { if (onError) onError(readErr); });
          } else {
            // Create new backup file
            var metadata = {
              name: fileName,
              parents: [folderId],
              description: 'גיבוי נתוני משתמש, מועדפים והגדרות ישראל TV+',
              mimeType: 'application/json'
            };
            var boundary = '-------314159265358979323846';
            var delimiter = '\r\n--' + boundary + '\r\n';
            var close_delim = '\r\n--' + boundary + '--';
            var multipartBody = delimiter +
              'Content-Type: application/json; charset=UTF-8\r\n\r\n' +
              JSON.stringify(metadata) +
              delimiter +
              'Content-Type: application/json; charset=UTF-8\r\n\r\n' +
              JSON.stringify(localPayload) +
              close_delim;

            fetch('https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart', {
              method: 'POST',
              headers: {
                'Authorization': 'Bearer ' + user.access_token,
                'Content-Type': 'multipart/related; boundary=' + boundary
              },
              body: multipartBody
            })
            .then(jsonOk)
            .then(function () {
              localStorage.setItem('itv_last_sync_time', String(Date.now()));
              if (onComplete) onComplete(null);
            })
            .catch(function (err) { if (onError) onError(err); });
          }
        })
        .catch(function (err) { if (onError) onError(err); });
      });
    },

    uploadFileToDrive: function (accessToken, folderId, name, blob, onProgress, onDone, onError) {
      var metadata = {
        name: name,
        parents: [folderId],
        description: 'הוקלט באמצעות ישראל TV+ | איכות שידור מלאה',
        mimeType: blob.type || 'video/mp4'
      };

      // 1. Resumable Upload Session
      fetch('https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable', {
        method: 'POST',
        headers: {
          'Authorization': 'Bearer ' + accessToken,
          'Content-Type': 'application/json; charset=UTF-8',
          'X-Upload-Content-Type': blob.type || 'video/mp4'
        },
        body: JSON.stringify(metadata)
      })
      .then(function (initRes) {
        if (!initRes.ok) throw new Error('Failed to initiate Drive upload: ' + initRes.status);
        var uploadUrl = initRes.headers.get('Location');
        if (!uploadUrl) throw new Error('No upload URL returned by Drive API');

        // 2. Upload binary payload
        var xhr = new XMLHttpRequest();
        xhr.open('PUT', uploadUrl, true);
        xhr.setRequestHeader('Content-Type', blob.type || 'video/mp4');

        xhr.upload.onprogress = function (e) {
          if (e.lengthComputable && onProgress) {
            var pct = Math.round((e.loaded / e.total) * 100);
            onProgress(pct, pct + '%');
          }
        };

        xhr.onload = function () {
          if (xhr.status >= 200 && xhr.status < 300) {
            try {
              var resJson = JSON.parse(xhr.responseText);
              if (onDone) onDone(resJson);
            } catch (je) {
              if (onDone) onDone({ success: true, name: name });
            }
          } else {
            if (onError) onError(new Error('Upload failed: ' + xhr.status));
          }
        };

        xhr.onerror = function (err) {
          if (onError) onError(err);
        };

        xhr.send(blob);
      })
      .catch(function (err) {
        if (onError) onError(err);
      });
    }
  };

  // An access token lives for an hour: everything that talks to Drive renews it first
  function withFreshToken(name, errorArg) {
    var inner = GDrive[name];
    GDrive[name] = function () {
      var args = arguments;
      GDrive.ensureToken(function (err) {
        if (err) {
          if (args[errorArg]) args[errorArg](err);
          else GDrive.reportAuthError(err);
          return;
        }
        inner.apply(GDrive, args);
      });
    };
  }
  withFreshToken('listRecordings', 0);
  withFreshToken('recordVodEpisode', 4);
  withFreshToken('recordLiveStream', 4);
  withFreshToken('syncData', 2);

  window.GDrive = GDrive;
})(window);

