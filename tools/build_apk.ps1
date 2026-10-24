param([string]$Out = '')
# Android app (phones, tablets, Android TV / Google TV / Fire TV): the Gradle project in android\.
# Phones get the native app (Kotlin + Jetpack Compose, android\app\src\main\kotlin); TVs get the web app in a WebView
# (platforms\android\src\...\MainActivity.java + the web files as assets). Version from VERSION (3.4.0 -> code 340).
# Signing key: %USERPROFILE%\.android\israel-tv-plus.jks, password in israel-tv-plus.pass next to it
# (keep both: updates must be signed with the same key). android\app\build.gradle.kts reads them.
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot
if (-not $Out) { $Out = "$root\dist" }
New-Item -ItemType Directory -Force $Out | Out-Null
if (-not (Test-Path "$env:USERPROFILE\.android\israel-tv-plus.jks")) { throw 'signing key missing: %USERPROFILE%\.android\israel-tv-plus.jks' }

Push-Location "$root\android"
try {
  & .\gradlew.bat :app:assembleRelease --console=plain -q
  if ($LASTEXITCODE) { throw 'gradle build failed' }
} finally { Pop-Location }

$built = "$root\android\app\build\outputs\apk\release\app-release.apk"
$apk = "$Out\IsraelTVPlus-android.apk"
Copy-Item $built $apk -Force
$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { "$env:LOCALAPPDATA\Android\Sdk" }
$bt = Get-ChildItem "$sdk\build-tools" | Sort-Object { [version]$_.Name } | Select-Object -Last 1 -ExpandProperty FullName
& "$bt\apksigner.bat" verify $apk
if ($LASTEXITCODE) { throw 'verify failed' }
# the same APK under a TV name, so Android TV users find it in the release
Copy-Item $apk "$Out\IsraelTVPlus-androidtv.apk" -Force
"apk: $apk ($([math]::Round((Get-Item $apk).Length / 1MB, 1)) MB) + IsraelTVPlus-androidtv.apk"
