# Israel TV+ for Windows: irm https://dor2500.github.io/israel-tv-plus/install.ps1 | iex
# Downloads the latest installer from GitHub Releases, checks its SHA-256 and installs silently.
# ASCII only: Windows PowerShell 5.1 can misread UTF-8 scripts piped through iex.
# Set ITV_INSTALL_DRYRUN=1 to stop after the checksum check.

& {
    $ErrorActionPreference = 'Stop'
    $ProgressPreference = 'SilentlyContinue'   # the progress bar slows Invoke-WebRequest down badly on 5.1
    [Net.ServicePointManager]::SecurityProtocol = [Net.ServicePointManager]::SecurityProtocol -bor [Net.SecurityProtocolType]::Tls12

    if ($env:OS -ne 'Windows_NT') { throw 'This installer is for Windows. Other systems: https://github.com/dor2500/israel-tv-plus#readme' }

    $base = 'https://github.com/dor2500/israel-tv-plus/releases/latest/download'
    $name = 'IsraelTVPlus-windows-setup.exe'
    $setup = Join-Path ([IO.Path]::GetTempPath()) ("IsraelTVPlus-setup-" + [guid]::NewGuid().ToString('N') + '.exe')

    try {
        Write-Host 'Downloading Israel TV+ ...'
        Invoke-WebRequest "$base/$name" -OutFile $setup -UseBasicParsing

        $sums = [Text.Encoding]::UTF8.GetString((Invoke-WebRequest "$base/SHA256SUMS.txt" -UseBasicParsing).Content)
        $line = $sums -split "`n" | Where-Object { $_ -match ("\s\*?" + [regex]::Escape($name) + "\s*$") } | Select-Object -First 1
        if (-not $line) { throw "$name is not listed in SHA256SUMS.txt" }
        $expected = ($line -split '\s+')[0]
        $actual = (Get-FileHash $setup -Algorithm SHA256).Hash
        if ($actual -ne $expected.ToUpperInvariant()) { throw "Checksum mismatch: expected $expected, got $actual. Nothing was installed." }
        Write-Host 'Checksum OK.'

        if ($env:ITV_INSTALL_DRYRUN -eq '1') { Write-Host 'Dry run: not installing.'; return }

        Write-Host 'Installing ...'
        $p = Start-Process $setup -ArgumentList '/VERYSILENT', '/SUPPRESSMSGBOXES', '/NORESTART' -Wait -PassThru
        if ($p.ExitCode -ne 0) { throw "The installer stopped with exit code $($p.ExitCode)." }
        Write-Host 'Israel TV+ is installed.' -ForegroundColor Green
    }
    finally {
        Remove-Item $setup -Force -ErrorAction SilentlyContinue
    }
}
