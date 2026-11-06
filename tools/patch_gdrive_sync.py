import os

ROOT = r"C:\Users\Dorp\Documents\deepseek-harness\default-workspace\repo"

# 1. Update gdrive.js with cloud sync methods
gdrive_path = os.path.join(ROOT, "gdrive.js")
with open(gdrive_path, "r", encoding="utf-8") as f:
    gcode = f.read()

sync_methods = """
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
            var delimiter = '\\r\\n--' + boundary + '\\r\\n';
            var close_delim = '\\r\\n--' + boundary + '--';
            var multipartBody = delimiter +
              'Content-Type: application/json; charset=UTF-8\\r\\n\\r\\n' +
              JSON.stringify(metadata) +
              delimiter +
              'Content-Type: application/json; charset=UTF-8\\r\\n\\r\\n' +
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
"""

if "getLastSyncFormatted:" not in gcode:
    gcode = gcode.replace("uploadFileToDrive: function", sync_methods + "\n    uploadFileToDrive: function")
    with open(gdrive_path, "w", encoding="utf-8") as f:
        f.write(gcode)
    print("gdrive.js: added Cloud Data Sync methods successfully!")
else:
    print("gdrive.js already has sync methods")
