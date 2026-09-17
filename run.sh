#!/usr/bin/env bash

# Bash script to build, install, and run RefExtract

echo "Starting RefExtract setup..."

# Step 1: Check Java
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed. Please install Java 17 or higher."
    exit 1
fi

echo "Java environment detected."

# Step 2: Build APK
echo "Building application package..."
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    ./gradlew assembleDebug
else
    gradle assembleDebug
fi

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "Build failed. Please check build logs."
    exit 1
fi

echo "Build successful! Package located at: $APK_PATH"

# Step 3: Check ADB and device
if command -v adb &> /dev/null; then
    DEVICES=$(adb devices | grep -w "device")
    if [ -n "$DEVICES" ]; then
        echo "Connected Android device detected. Installing package..."
        adb install -r "$APK_PATH"
        echo "Launching RefExtract on device..."
        adb shell am start -n com.aistudio.refextract.mxtqp/com.example.MainActivity
        echo "Application launched successfully!"
        exit 0
    fi
fi

echo "No connected Android device detected via ADB."
echo "You can transfer the APK file to your phone or emulator to install:"
echo "$(pwd)/$APK_PATH"
