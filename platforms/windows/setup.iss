; Israel TV+ Windows installer (Inno Setup 6). Built by tools\build_windows.ps1, which passes
; AppVersion, SrcDir (the built app folder), OutDir and IconFile.
#ifndef AppVersion
  #define AppVersion "4.6.4"
#endif

[Setup]
AppId={{7C1E3B52-2D8E-4C71-9B0E-ISRAELTVPLUS}
AppName=ישראל TV+
AppVerName=ישראל TV+ {#AppVersion}
AppVersion={#AppVersion}
AppPublisher=dor2500
AppPublisherURL=https://github.com/dor2500/israel-tv-plus
DefaultDirName={autopf}\IsraelTVPlus
DefaultGroupName=ישראל TV+
UninstallDisplayIcon={app}\IsraelTVPlus.exe
SetupIconFile={#IconFile}
OutputDir={#OutDir}
OutputBaseFilename=IsraelTVPlus-windows-setup
Compression=lzma2/max
SolidCompression=yes
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog
WizardStyle=modern

[Languages]
Name: "hebrew"; MessagesFile: "compiler:Languages\Hebrew.isl"
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"

[Files]
Source: "{#SrcDir}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\ישראל TV+"; Filename: "{app}\IsraelTVPlus.exe"
Name: "{group}\{cm:UninstallProgram,ישראל TV+}"; Filename: "{uninstallexe}"
Name: "{autodesktop}\ישראל TV+"; Filename: "{app}\IsraelTVPlus.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\IsraelTVPlus.exe"; Description: "{cm:LaunchProgram,ישראל TV+}"; Flags: nowait postinstall skipifsilent
; in-app updates run the installer with /SILENT: start the new version afterwards
Filename: "{app}\IsraelTVPlus.exe"; Flags: nowait runasoriginaluser; Check: WizardSilent

