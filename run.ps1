# PowerShell script to build, install, and run RefExtract
#
# Usage:
#   .\run.ps1                              (uses current directory)
#   .\run.ps1 -ProjectDir "C:\path\to\refextract"

param(
    [string]$ProjectDir = (Get-Location).Path
)

if (-not (Test-Path $ProjectDir)) {
    Write-Host "Directory not found: $ProjectDir" -ForegroundColor Red
    exit 1
}

Set-Location $ProjectDir
Write-Host "Project directory: $ProjectDir" -ForegroundColor Cyan
Write-Host "Starting RefExtract setup..." -ForegroundColor Cyan

# Step 1: Check Java installation
try {
    $javaVersion = java -version 2>&1
    Write-Host "Java environment detected." -ForegroundColor Green
} catch {
    Write-Host "Java is missing. Please install Java 17 or higher to continue." -ForegroundColor Red
    exit 1
}

# Step 2: Build the APK
Write-Host "Building application package..." -ForegroundColor Cyan
if (Test-Path "$ProjectDir\gradlew.bat") {
    & "$ProjectDir\gradlew.bat" assembleDebug
} else {
    gradle assembleDebug
}

$apkPath = Join-Path $ProjectDir "app\build\outputs\apk\debug\app-debug.apk"

if (-not (Test-Path $apkPath)) {
    Write-Host "Build failed. Please check build logs." -ForegroundColor Red
    exit 1
}

Write-Host "Build successful! Package located at: $apkPath" -ForegroundColor Green

# Step 3: Check connected Android device or emulator via ADB
$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (-not (Test-Path $adbPath)) {
    try {
        $adbPath = (Get-Command adb -ErrorAction Stop).Source
    } catch {
        $adbPath = $null
    }
}

if ($adbPath) {
    $devices = & $adbPath devices | Select-String -Pattern "\tdevice$"
    if ($devices) {
        Write-Host "Connected Android device detected. Installing package..." -ForegroundColor Cyan
        & $adbPath install -r $apkPath
        Write-Host "Launching RefExtract on device..." -ForegroundColor Green
        & $adbPath shell am start -n com.aistudio.refextract.mxtqp/com.example.MainActivity
        Write-Host "Application launched successfully!" -ForegroundColor Green
        exit 0
    }
}

Write-Host "No connected Android device detected via ADB." -ForegroundColor Yellow
Write-Host "You can transfer the APK file to your phone or emulator to install:" -ForegroundColor Cyan
Write-Host "$apkPath" -ForegroundColor White
