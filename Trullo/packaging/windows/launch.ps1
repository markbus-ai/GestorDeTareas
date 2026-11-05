param([switch]$SetupOnly, [switch]$Repair)
$ErrorActionPreference = 'Stop'
$TrulloRoot = Split-Path $PSScriptRoot -Parent
$TrulloData = Join-Path $env:LOCALAPPDATA 'Trullo'
$env:UV_PYTHON_INSTALL_DIR = Join-Path $TrulloData 'python'
$env:UV_CACHE_DIR = Join-Path $TrulloData 'uv-cache'
$env:PLAYWRIGHT_BROWSERS_PATH = Join-Path $TrulloData 'browsers'
$arguments = @('run', '--no-project', '--python', '3.12', 'python',
    (Join-Path $PSScriptRoot 'bootstrap.py'), '--home', $TrulloRoot)
if ($SetupOnly) { $arguments += '--setup-only' }
if ($Repair) { $arguments += '--repair' }
try {
    & (Join-Path $TrulloRoot 'tools\uv.exe') @arguments
    if ($LASTEXITCODE -ne 0) { throw 'La preparacion o ejecucion de Trullo fallo.' }
} catch {
    Add-Type -AssemblyName System.Windows.Forms
    [System.Windows.Forms.MessageBox]::Show(
        "No se pudo iniciar Trullo. Revisa la consola de preparacion y $TrulloData\postgres.log. Tus datos no se borraron.",
        'Trullo') | Out-Null
    Write-Error $_
    exit 1
}
