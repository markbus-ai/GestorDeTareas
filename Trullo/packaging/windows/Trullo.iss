#ifndef PayloadRoot
  #error Falta /DPayloadRoot
#endif
#ifndef AppVersion
  #define AppVersion "1.0.0"
#endif
[Setup]
AppId={{E1DB9545-8CF6-4827-928C-A704E977B2CE}
AppName=Trullo
AppVersion={#AppVersion}
DefaultDirName={autopf}\Trullo
DefaultGroupName=Trullo
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
PrivilegesRequired=admin
OutputBaseFilename=TrulloSetup-{#AppVersion}-windows-x64
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
CloseApplications=yes
[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"
[Files]
Source: "{#PayloadRoot}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
[Icons]
Name: "{group}\Trullo"; Filename: "{sys}\WindowsPowerShell\v1.0\powershell.exe"; Parameters: "-NoProfile -ExecutionPolicy Bypass -File ""{app}\setup\launch.ps1"""
Name: "{group}\Reparar preparacion"; Filename: "{sys}\WindowsPowerShell\v1.0\powershell.exe"; Parameters: "-NoProfile -ExecutionPolicy Bypass -File ""{app}\setup\launch.ps1"" -SetupOnly -Repair"
[Run]
Filename: "{app}\tools\vc_redist.x64.exe"; Parameters: "/install /quiet /norestart"; StatusMsg: "Preparando componentes de Windows..."; Flags: waituntilterminated
Filename: "{sys}\WindowsPowerShell\v1.0\powershell.exe"; Parameters: "-NoProfile -ExecutionPolicy Bypass -File ""{app}\setup\launch.ps1"" -SetupOnly"; StatusMsg: "Preparando Trullo. Se necesita conexion a Internet..."; Flags: runasoriginaluser waituntilterminated
; No se borran datos de APPDATA/LOCALAPPDATA al desinstalar.
