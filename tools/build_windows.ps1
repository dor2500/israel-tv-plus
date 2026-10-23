param([string]$Version = '', [string]$Out = '')
# Windows app: platforms\windows\IsraelTVPlus.cs (WebView2) compiled with the .NET Framework csc.exe that ships
# with Windows, + the web app in www\. Outputs dist\IsraelTVPlus-windows-portable.zip and, when Inno Setup 6 is
# installed, dist\IsraelTVPlus-windows-setup.exe.
# WebView2 SDK: the Microsoft.Web.WebView2 NuGet package is downloaded once into build\webview2.
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot
if (-not $Version) { $Version = (Get-Content "$root\VERSION" -Raw).Trim() }
if (-not $Out) { $Out = "$root\dist" }
$b = "$root\build\windows"
$app = "$b\IsraelTVPlus"
if (Test-Path $app) { Remove-Item $app -Recurse -Force }
New-Item -ItemType Directory -Force "$app\www", $Out | Out-Null

$wv = "$root\build\webview2"
if (-not (Test-Path "$wv\lib\net462\Microsoft.Web.WebView2.WinForms.dll")) {
  $v = (Invoke-RestMethod https://api.nuget.org/v3-flatcontainer/microsoft.web.webview2/index.json).versions |
    Where-Object { $_ -notmatch '-' } | Select-Object -Last 1
  New-Item -ItemType Directory -Force $wv | Out-Null
  Invoke-WebRequest "https://api.nuget.org/v3-flatcontainer/microsoft.web.webview2/$v/microsoft.web.webview2.$v.nupkg" -OutFile "$wv\pkg.zip"
  Expand-Archive "$wv\pkg.zip" -DestinationPath $wv -Force
}
Copy-Item "$wv\lib\net462\Microsoft.Web.WebView2.Core.dll", "$wv\lib\net462\Microsoft.Web.WebView2.WinForms.dll", `
  "$wv\runtimes\win-x64\native\WebView2Loader.dll" $app

$csc = "$env:WINDIR\Microsoft.NET\Framework64\v4.0.30319\csc.exe"
$fx = "$env:WINDIR\Microsoft.NET\Framework64\v4.0.30319"
& $csc /nologo /target:winexe /platform:x64 /optimize+ /out:"$app\IsraelTVPlus.exe" /win32icon:"$root\assets\app\icon.ico" `
  /r:"$fx\System.Windows.Forms.dll" /r:"$fx\System.Drawing.dll" /r:"$app\Microsoft.Web.WebView2.Core.dll" `
  /r:"$app\Microsoft.Web.WebView2.WinForms.dll" "$root\platforms\windows\IsraelTVPlus.cs"
if ($LASTEXITCODE) { throw 'csc failed' }

foreach ($f in (Get-Content "$PSScriptRoot\webfiles.txt" | Where-Object { $_ -and $_ -notmatch '^#' })) {
  Copy-Item "$root\$f" "$app\www\$f"
}
Copy-Item "$root\assets" "$app\www\assets" -Recurse
Get-ChildItem "$app\www\assets" -Recurse -File | Where-Object { $_.Name -match '&|\.map$' } | Remove-Item

$zip = "$Out\IsraelTVPlus-windows-portable.zip"
if (Test-Path $zip) { Remove-Item $zip }
Compress-Archive "$app" $zip
"portable: $zip"

$iscc = @("$env:LOCALAPPDATA\Programs\Inno Setup 6\ISCC.exe", "${env:ProgramFiles(x86)}\Inno Setup 6\ISCC.exe") |
  Where-Object { Test-Path $_ } | Select-Object -First 1
if ($iscc) {
  & $iscc /Q "/DAppVersion=$Version" "/DSrcDir=$app" "/DOutDir=$Out" "/DIconFile=$root\assets\app\icon.ico" "$root\platforms\windows\setup.iss"
  if ($LASTEXITCODE) { throw 'Inno Setup failed' }
  "setup: $Out\IsraelTVPlus-windows-setup.exe"
} else {
  'Inno Setup 6 not found - skipped the installer'
}
