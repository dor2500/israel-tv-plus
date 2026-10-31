with open('platforms/android/src/com/israeltv/plus/MainActivity.java', 'r', encoding='utf-8') as f:
    text = f.read()

# Add FileProvider import if needed
if "import androidx.core.content.FileProvider;" not in text:
    text = text.replace("import androidx.core.splashscreen.SplashScreen;", "import androidx.core.splashscreen.SplashScreen;\nimport androidx.core.content.FileProvider;")

# Replace download implementation
old_download = """            PackageInstaller.Session session = pi.openSession(id);
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
            session.close();"""

new_download = """            PackageInstaller.Session session = pi.openSession(id);
            InputStream in = c.getInputStream();
            OutputStream out = session.openWrite("update.apk", 0, len > 0 ? len : -1);
            java.io.File cacheApk = new java.io.File(getCacheDir(), "update.apk");
            java.io.FileOutputStream fileOut = new java.io.FileOutputStream(cacheApk);
            byte[] buf = new byte[65536];
            long done = 0;
            int n, last = -1;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
                fileOut.write(buf, 0, n);
                done += n;
                if (len > 0) {
                    int pct = (int) (done * 100 / len);
                    if (pct != last) { last = pct; report("progress", String.valueOf(pct)); }
                }
            }
            session.fsync(out);
            out.close();
            fileOut.close();
            in.close();
            report("installing", "");
            try {
                Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", cacheApk);
                Intent viewIntent = new Intent(Intent.ACTION_VIEW);
                viewIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(viewIntent);
            } catch (Exception ex) {
                Intent i = new Intent(this, MainActivity.class).setAction(ACTION_INSTALL_STATUS);
                int flags = PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= 31 ? PendingIntent.FLAG_MUTABLE : 0);
                PendingIntent pending = PendingIntent.getActivity(this, id, i, flags);
                session.commit(pending.getIntentSender());
            }
            session.close();"""

if old_download in text:
    text = text.replace(old_download, new_download)
    with open('platforms/android/src/com/israeltv/plus/MainActivity.java', 'w', encoding='utf-8') as f:
        f.write(text)
    print("Patched MainActivity.java download()")
else:
    print("Warning: old_download not found in MainActivity.java")

