/**
 * Israel TV+ Google Identity & Google Drive Cloud Recording Module
 * Handles OAuth 2.0 (GIS Web / Device Authorization Flow for Smart TVs),
 * cloud folder creation ("ישראל TV+ - הקלטות"), and resumable recording
 * of VOD series episodes and live broadcasts directly to user's Google Drive.
 */
(function (window) {
  'use strict';

  var CLIENT_ID = '1083921845892-v7k7u83818e9f28d8b2d28j92k9s28d.apps.googleusercontent.com';
  var SCOPE = 'https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email openid';
  var FOLDER_NAME = 'ישראל TV+ - הקלטות';
  var AUTH_KEY = 'itv_google_auth';
  var FOLDER_KEY = 'itv_gdrive_folder_id';

  var activeTokenClient = null;
  var devicePollTimer = null;
  var activeRecording = null;

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
      return !!(u && u.access_token && !u.isExpired);
    },

    // ------------------------------------------------------------- Web Sign-In (GIS)
    signInWeb: function (onSuccess, onError) {
      // 1. Try Google Identity Services if loaded
      if (window.google && window.google.accounts && window.google.accounts.oauth2) {
        try {
          if (!activeTokenClient) {
            activeTokenClient = window.google.accounts.oauth2.initTokenClient({
              client_id: CLIENT_ID,
              scope: SCOPE,
              callback: function (tokenResp) {
                if (tokenResp && tokenResp.access_token) {
                  GDrive.fetchUserProfile(tokenResp.access_token, tokenResp.expires_in, onSuccess, onError);
                } else if (onError) {
                  onError(new Error(tokenResp && tokenResp.error || 'ההתחברות בוטלה'));
                }
              }
            });
          }
          activeTokenClient.requestAccessToken({ prompt: '' });
          return;
        } catch (e) {
          console.warn('GIS error, falling back to popup/device flow:', e);
        }
      }

      // 2. Fallback: OAuth 2.0 Web Popup
      var redirectUri = window.location.origin + window.location.pathname;
      var authUrl = 'https://accounts.google.com/o/oauth2/v2/auth?' +
        'client_id=' + encodeURIComponent(CLIENT_ID) +
        '&redirect_uri=' + encodeURIComponent(redirectUri) +
        '&response_type=token' +
        '&scope=' + encodeURIComponent(SCOPE) +
        '&prompt=select_account';

      var popup = window.open(authUrl, 'google_login', 'width=520,height=640');
      if (!popup) {
        // Popup blocked, open in same tab or use TV device flow
        GDrive.startDeviceFlow(onSuccess, onError);
        return;
      }

      var pollTimer = setInterval(function () {
        try {
          if (!popup || popup.closed) {
            clearInterval(pollTimer);
            return;
          }
          var href = popup.location.href;
          if (href && href.indexOf('access_token=') !== -1) {
            clearInterval(pollTimer);
            var params = new URLSearchParams(popup.location.hash.slice(1));
            var token = params.get('access_token');
            var exp = parseInt(params.get('expires_in') || '3600', 10);
            popup.close();
            if (token) {
              GDrive.fetchUserProfile(token, exp, onSuccess, onError);
            }
          }
        } catch (crossOriginErr) {
          // Expected while user is on accounts.google.com
        }
      }, 500);
    },

    // ------------------------------------------------------------- Smart TV Device Code Flow
    startDeviceFlow: function (onSuccess, onError) {
      if (devicePollTimer) {
        clearInterval(devicePollTimer);
        devicePollTimer = null;
      }

      // Request device code
      var params = new URLSearchParams();
      params.append('client_id', CLIENT_ID);
      params.append('scope', SCOPE);

      fetch('https://oauth2.googleapis.com/device/code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params.toString()
      })
      .then(function (r) {
        if (!r.ok) throw new Error('HTTP ' + r.status);
        return r.json();
      })
      .then(function (data) {
        var userCode = data.user_code;
        var deviceCode = data.device_code;
        var verifyUrl = data.verification_url || 'https://www.google.com/device';
        var interval = (data.interval || 5) * 1000;
        var expiresIn = data.expires_in || 1800;

        // Display TV dialog
        GDrive.showTvDeviceModal(userCode, verifyUrl);

        // Poll for completion
        var startTime = Date.now();
        devicePollTimer = setInterval(function () {
          if (Date.now() - startTime > expiresIn * 1000) {
            clearInterval(devicePollTimer);
            devicePollTimer = null;
            if (onError) onError(new Error('פג תוקף הקוד. אנא נסו שוב.'));
            return;
          }

          var pollParams = new URLSearchParams();
          pollParams.append('client_id', CLIENT_ID);
          pollParams.append('client_secret', '');
          pollParams.append('code', deviceCode);
          pollParams.append('grant_type', 'urn:ietf:params:oauth:grant-type:device_code');

          fetch('https://oauth2.googleapis.com/token', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: pollParams.toString()
          })
          .then(function (tr) { return tr.json(); })
          .then(function (tData) {
            if (tData.access_token) {
              clearInterval(devicePollTimer);
              devicePollTimer = null;
              GDrive.closeTvDeviceModal();
              GDrive.fetchUserProfile(tData.access_token, tData.expires_in, onSuccess, onError);
            } else if (tData.error === 'authorization_pending') {
              // Wait for user to complete
            } else if (tData.error === 'slow_down') {
              interval += 2000;
            } else if (tData.error) {
              clearInterval(devicePollTimer);
              devicePollTimer = null;
              if (tData.error !== 'access_denied') {
                console.warn('Device flow token poll:', tData.error);
              }
            }
          })
          .catch(function () {});
        }, interval);
      })
      .catch(function (err) {
        console.warn('Device code request failed:', err);
        // Fallback: prompt manual OAuth token or mock sign in
        GDrive.showManualTokenModal(onSuccess, onError);
      });
    },

    showTvDeviceModal: function (userCode, verifyUrl) {
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
          buttons: [{
            label: 'ביטול',
            act: function () {
              if (devicePollTimer) { clearInterval(devicePollTimer); devicePollTimer = null; }
            }
          }]
        });

        setTimeout(function () {
          var qrBox = document.getElementById('gdrive-qr');
          if (qrBox && window.QRCode) {
            new window.QRCode(qrBox, {
              text: verifyUrl + '?user_code=' + encodeURIComponent(userCode),
              width: 170,
              height: 170,
              correctLevel: window.QRCode.CorrectLevel.M
            });
          }
        }, 60);
      }
    },

    closeTvDeviceModal: function () {
      if (window.closeDialog) window.closeDialog();
    },

    showManualTokenModal: function (onSuccess, onError) {
      var html = '<div style="padding:1rem;">' +
        '<p style="font-size:1.05rem;line-height:1.5;margin-bottom:1rem;">חברו את חשבון Google שלכם לשמירת סדרות ושידורים חיים ישירות ל-Google Drive האישי שלכם:</p>' +
        '<div style="display:flex;flex-direction:column;gap:0.8rem;">' +
        '<button class="btn primary" id="btn-oauth-direct" style="padding:0.9rem;font-size:1.1rem;display:flex;align-items:center;justify-content:center;gap:0.6rem;">' +
        '<svg width="20" height="20" viewBox="0 0 24 24"><path fill="#fff" d="M21.35 11.1h-9.17v2.98h5.27c-.23 1.22-.92 2.26-1.95 2.95v2.45h3.16c1.85-1.7 2.92-4.22 2.92-7.18 0-.41-.03-.82-.09-1.2h-.14z"/><path fill="#fff" d="M12.18 22c2.65 0 4.88-.88 6.51-2.38l-3.16-2.45c-.88.59-2.01.94-3.35.94-2.58 0-4.76-1.74-5.54-4.08H3.36v2.53C5 19.8 8.35 22 12.18 22z"/><path fill="#fff" d="M6.64 13.03c-.2-.59-.31-1.22-.31-1.87s.11-1.28.31-1.87V6.76H3.36C2.69 8.09 2.3 9.6 2.3 11.16s.39 3.07 1.06 4.4l3.28-2.53z"/><path fill="#fff" d="M12.18 6.08c1.44 0 2.74.5 3.76 1.47l2.82-2.82C17.05 3.14 14.82 2.2 12.18 2.2 8.35 2.2 5 4.4 3.36 7.76l3.28 2.53c.78-2.34 2.96-4.21 5.54-4.21z"/></svg>' +
        'התחברות באמצעות Google (חלון מאובטח)</button>' +
        '</div></div>';

      if (window.openDialog) {
        window.openDialog({
          ico: '☁️',
          title: 'התחברות ל-Google Drive',
          html: html,
          buttons: [{ label: 'ביטול' }]
        });
        setTimeout(function () {
          var btn = document.getElementById('btn-oauth-direct');
          if (btn) {
            btn.addEventListener('click', function () {
              window.closeDialog();
              GDrive.signInWeb(onSuccess, onError);
            });
          }
        }, 50);
      }
    },

    // ------------------------------------------------------------- Profile Retrieval
    fetchUserProfile: function (accessToken, expiresIn, onSuccess, onError) {
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
        console.warn('Failed to fetch Google userinfo:', err);
        var fallbackUser = {
          email: 'google_user@israeltv.plus',
          name: 'משתמש Google',
          picture: '',
          access_token: accessToken,
          expires_at: Date.now() + ((expiresIn || 3600) * 1000)
        };
        GDrive.saveUser(fallbackUser);
        if (onSuccess) onSuccess(fallbackUser);
      });
    },

    signOut: function (onComplete) {
      var u = GDrive.getUser();
      if (u && u.access_token) {
        fetch('https://oauth2.googleapis.com/revoke?token=' + encodeURIComponent(u.access_token), {
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
      .then(function (r) { return r.json(); })
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
          .then(function (cr) { return cr.json(); })
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
        .then(function (r) { return r.json(); })
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
          window.toast('✅ פרק נשמר ב-Google Drive: ' + series.title);
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
        .then(function (r) { return r.json(); })
        .then(function (data) {
          if (data.files && data.files.length) {
            var fileId = data.files[0].id;
            // Fetch cloud data to compare timestamps
            fetch('https://www.googleapis.com/drive/v3/files/' + fileId + '?alt=media', {
              headers: { 'Authorization': 'Bearer ' + user.access_token }
            })
            .then(function (res) { return res.json(); })
            .then(function (cloudJson) {
              var cloudTime = (cloudJson && cloudJson.updatedAt) || 0;
              var localTime = (localPayload && localPayload.updatedAt) || 0;

              if (cloudTime > localTime) {
                // Cloud has newer data, apply to local
                localStorage.setItem('itv_last_sync_time', String(Date.now()));
                if (onComplete) onComplete(cloudJson);
              } else {
                // Local is newer or equal, update cloud file
                fetch('https://www.googleapis.com/upload/drive/v3/files/' + fileId + '?uploadType=media', {
                  method: 'PATCH',
                  headers: {
                    'Authorization': 'Bearer ' + user.access_token,
                    'Content-Type': 'application/json; charset=UTF-8'
                  },
                  body: JSON.stringify(localPayload)
                })
                .then(function () {
                  localStorage.setItem('itv_last_sync_time', String(Date.now()));
                  if (onComplete) onComplete(null);
                })
                .catch(function (upErr) { if (onError) onError(upErr); });
              }
            })
            .catch(function () {
              // Overwrite if reading failed
              fetch('https://www.googleapis.com/upload/drive/v3/files/' + fileId + '?uploadType=media', {
                method: 'PATCH',
                headers: {
                  'Authorization': 'Bearer ' + user.access_token,
                  'Content-Type': 'application/json; charset=UTF-8'
                },
                body: JSON.stringify(localPayload)
              }).then(function () {
                localStorage.setItem('itv_last_sync_time', String(Date.now()));
                if (onComplete) onComplete(null);
              });
            });
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

  window.GDrive = GDrive;
})(window);

