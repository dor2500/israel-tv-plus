param([string]$Version = '', [int]$Code = 0, [string]$Out = '')
# Android app (phones, tablets, Android TV / Google TV / Fire TV): platforms\android + the web app as assets.
# No Gradle: aapt2 (resources + manifest), javac + d8 (code), zipalign + apksigner.
# Signing key: %USERPROFILE%\.android\israel-tv-plus.jks, password in israel-tv-plus.pass next to it
# (made once - keep both: updates must be signed with the same key).
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot
if (-not $Out) { $Out = "$root\dist" }
if (-not $Version) { $Version = (Get-Content "$root\VERSION" -Raw).Trim() }
if (-not $Code) { $p = $Version.Split('.'); $Code = [int]$p[0] * 100 + [int]$p[1] * 10 + [int]$p[2] }  # 3.0.0 -> 300
$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$bt = Get-ChildItem "$sdk\build-tools" | Sort-Object { [version]$_.Name } | Select-Object -Last 1 -ExpandProperty FullName
# newest stable platform (android-37.2 > android-36); previews (letters) are skipped
$plat = Get-ChildItem "$sdk\platforms" | Where-Object Name -match '^android-\d+(\.\d+)?$' | Sort-Object { [double]($_.Name -replace '^android-', '') } | Select-Object -Last 1 -ExpandProperty FullName
$target = [int](((Split-Path $plat -Leaf) -replace '^android-', '').Split('.')[0])
$jar = "$plat\android.jar"
$and = "$root\platforms\android"
$b = "$root\build\apk"
if (Test-Path $b) { Remove-Item $b -Recurse -Force }
$dirs = 'mipmap-mdpi', 'mipmap-hdpi', 'mipmap-xhdpi', 'mipmap-xxhdpi', 'mipmap-xxxhdpi', 'drawable-xhdpi'
foreach ($d in $dirs) { New-Item -ItemType Directory -Force "$b\res\$d" | Out-Null }
New-Item -ItemType Directory -Force "$b\assets\www", "$b\cls", "$b\dex", $Out | Out-Null

# launcher icons + the Android TV banner (tools\make_icons.py)
$app = "$root\assets\app"
foreach ($p in @(@('mdpi', 48), @('hdpi', 72), @('xhdpi', 96), @('xxhdpi', 144), @('xxxhdpi', 192))) {
  Copy-Item "$app\icon-$($p[1]).png" "$b\res\mipmap-$($p[0])\ic_launcher.png"
}
Copy-Item "$app\banner-320x180.png" "$b\res\drawable-xhdpi\banner.png"
# theme (edge-to-edge, Android 12+ splash screen)
Copy-Item "$and\res\*" "$b\res" -Recurse -Force

# the web app
foreach ($f in (Get-Content "$PSScriptRoot\webfiles.txt" | Where-Object { $_ -and $_ -notmatch '^#' })) {
  Copy-Item "$root\$f" "$b\assets\www\$f"
}
Copy-Item "$root\assets" "$b\assets\www\assets" -Recurse
Get-ChildItem "$b\assets\www\assets" -Recurse -File | Where-Object { $_.Name -match '&|\.map$|banner-1280|icon-1024' } | Remove-Item

& "$bt\aapt2.exe" compile --dir "$b\res" -o "$b\res.zip"
if ($LASTEXITCODE) { throw 'aapt2 compile failed' }
& "$bt\aapt2.exe" link -I $jar --manifest "$and\AndroidManifest.xml" -A "$b\assets" -o "$b\base.apk" `
  --min-sdk-version 21 --target-sdk-version $target --version-code $Code --version-name $Version "$b\res.zip" -0 png
if ($LASTEXITCODE) { throw 'aapt2 link failed' }

$src = Get-ChildItem "$and\src" -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 --release 11 -classpath $jar -d "$b\cls" $src 2>&1 | Where-Object { $_ -notmatch 'warning|^Note' }
if ($LASTEXITCODE) { throw 'javac failed' }
$classes = Get-ChildItem "$b\cls" -Recurse -Filter *.class | ForEach-Object FullName
& "$bt\d8.bat" --release --min-api 21 --lib $jar --output "$b\dex" $classes
if ($LASTEXITCODE) { throw 'd8 failed' }
# rewrite the zip with classes.dex added (appending in place leaves mismatched local headers)
python -I -c @"
import sys, zipfile
src, dex, dst = sys.argv[1:4]
with zipfile.ZipFile(src) as zi, zipfile.ZipFile(dst, 'w') as zo:
    for it in zi.infolist():
        zo.writestr(zipfile.ZipInfo(it.filename, it.date_time), zi.read(it), compress_type=it.compress_type)
    zo.write(dex, 'classes.dex', compress_type=zipfile.ZIP_DEFLATED)
"@ "$b\base.apk" "$b\dex\classes.dex" "$b\full.apk"
if ($LASTEXITCODE) { throw 'zip failed' }

& "$bt\zipalign.exe" -f -p 4 "$b\full.apk" "$b\aligned.apk"
if ($LASTEXITCODE) { throw 'zipalign failed' }
$ks = "$env:USERPROFILE\.android\israel-tv-plus.jks"
$pf = "$env:USERPROFILE\.android\israel-tv-plus.pass"
if (-not (Test-Path $ks)) {
  New-Item -ItemType Directory -Force (Split-Path $ks) | Out-Null
  $pw = -join ((48..57) + (65..90) + (97..122) | Get-Random -Count 24 | ForEach-Object { [char]$_ })
  [IO.File]::WriteAllText($pf, $pw)
  keytool -genkeypair -keystore $ks -alias itv -keyalg RSA -keysize 2048 -validity 36500 `
    -storepass $pw -keypass $pw -dname "CN=Israel TV Plus, O=dor2500" 2>&1 | Out-Null
  if ($LASTEXITCODE) { throw 'keytool failed' }
}
$pw = (Get-Content $pf -Raw).Trim()
$apk = "$Out\IsraelTVPlus-android.apk"
& "$bt\apksigner.bat" sign --ks $ks --ks-pass "pass:$pw" --key-pass "pass:$pw" --out $apk "$b\aligned.apk"
if ($LASTEXITCODE) { throw 'apksigner failed' }
& "$bt\apksigner.bat" verify $apk
if ($LASTEXITCODE) { throw 'verify failed' }
# the same APK under a TV name, so Android TV users find it in the release
Copy-Item $apk "$Out\IsraelTVPlus-androidtv.apk" -Force
"apk: $apk ($([math]::Round((Get-Item $apk).Length / 1MB, 1)) MB) + IsraelTVPlus-androidtv.apk"
