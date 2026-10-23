param([string]$Out = '')
# Samsung Tizen TV project: platforms\tizen\config.xml + the web app -> dist\IsraelTVPlus-tizen-project.zip.
# Samsung TVs only install signed packages, so: Tizen Studio -> File > Import > Tizen > Tizen Project (this folder),
# Tools > Certificate Manager (Samsung certificate with your TV's DUID), then Run As > Tizen Web Application.
# With the Tizen CLI on PATH it also builds + signs the .wgt: tizen package -t wgt -s <profile> -- <dir>
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot
if (-not $Out) { $Out = "$root\dist" }
$p = "$root\build\tizen\IsraelTVPlus"
if (Test-Path $p) { Remove-Item $p -Recurse -Force }
New-Item -ItemType Directory -Force $p, $Out | Out-Null
Copy-Item "$root\platforms\tizen\config.xml" $p
foreach ($f in (Get-Content "$PSScriptRoot\webfiles.txt" | Where-Object { $_ -and $_ -notmatch '^#' -and $_ -ne 'icon.png' })) { Copy-Item "$root\$f" "$p\$f" }
Copy-Item "$root\assets\app\icon-512.png" "$p\icon.png"
Copy-Item "$root\assets" "$p\assets" -Recurse
$zip = "$Out\IsraelTVPlus-tizen-project.zip"
if (Test-Path $zip) { Remove-Item $zip }
Compress-Archive $p $zip
"tizen project: $zip"
if (Get-Command tizen -ErrorAction SilentlyContinue) {
  tizen package -t wgt -- $p
  Get-ChildItem $p -Filter *.wgt | Copy-Item -Destination "$Out\IsraelTVPlus-tizen.wgt"
}
