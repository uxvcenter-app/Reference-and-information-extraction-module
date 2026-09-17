# RefExtract User Guide

RefExtract is an Android app that extracts references, quotations, named entities,
metrics, and dates from any text you paste into it.

---

## Prerequisites

- **Java JDK 17 or higher** — required to run the build.
- **Android SDK** — required to build an Android app. The simplest way to get it is to
  install [Android Studio](https://developer.android.com/studio), which bundles the SDK.
  If you prefer a command-line-only setup, install the
  [Android command line tools](https://developer.android.com/studio#command-line-tools-only)
  and use `sdkmanager` to install `platforms;android-36` and a recent `build-tools`.
- **Git** — to clone this repository.
- **USB Debugging** — only needed if you want to install directly to a phone over USB.

You do **not** need to install Gradle. This project includes the Gradle wrapper
(`gradlew`), which downloads the correct Gradle version automatically on first build.

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/uxvcenter-app/Reference-and-information-extraction-module.git
cd Reference-and-information-extraction-module
```

Every command in this guide is run from inside that folder.

---

## Step 2: Point the Build at Your Android SDK

The build needs to know where your Android SDK lives. Pick either option:

**Option A — Open the project in Android Studio once.** Choose *File → Open*, select the
cloned folder, and let it sync. Android Studio writes a `local.properties` file for you
and you can skip to Step 3.

**Option B — Set an environment variable.**

Windows (PowerShell):
```powershell
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
```

macOS:
```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
```

Linux:
```bash
export ANDROID_HOME="$HOME/Android/Sdk"
```

Alternatively, create a file named `local.properties` in the project root containing a
single line pointing at your SDK, for example `sdk.dir=C\:\\Users\\you\\AppData\\Local\\Android\\Sdk`
on Windows, or `sdk.dir=/home/you/Android/Sdk` on Linux.

`local.properties` is intentionally not committed, since the path differs on every machine.

---

## Step 3 : Set Up a Gemini API Key

RefExtract runs with local extraction by default and works fine without a key. To enable
cloud AI extraction using Gemini:

1. Copy `.env.example` in the project root and rename the copy to `.env`.
2. Open `.env` in any text editor.
3. Replace `MY_GEMINI_API_KEY` with your actual Google Gemini API key:
   ```text
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```

The build bundles the key automatically. If no key is configured, the app falls back to
its built-in local engine rules. `.env` is not committed, so your key stays on your machine.

---

## Step 4: Build and Run

### Option 1: Automatic Setup via Scripts

**Windows (PowerShell)** — run from inside the project folder:
```powershell
.\run.ps1
```

**macOS and Linux (Bash)**:
```bash
chmod +x run.sh
./run.sh
```

Both scripts use the current directory by default. You can also pass the project folder
explicitly with `.\run.ps1 -ProjectDir "C:\path\to\project"` or `./run.sh /path/to/project`.

The scripts will:

1. Verify Java is installed.
2. Compile the app into an installable package (`app-debug.apk`).
3. Install and launch it if an Android device or emulator is connected with USB debugging
   active.
4. Otherwise, print the full path to the compiled package so you can transfer it manually.

### Option 2: Manual Build

**Windows:**
```cmd
gradlew.bat assembleDebug
```

**macOS or Linux:**
```bash
./gradlew assembleDebug
```

The first build takes several minutes while Gradle and the project dependencies download.

The compiled package is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Transfer this file to your phone via USB cable, cloud storage, or email, then tap it to
install. If prompted, allow installation from unknown sources in your phone settings.

---

## Troubleshooting

**`SDK location not found`** — you skipped Step 2. Set `ANDROID_HOME` or create
`local.properties`.

**`'gradlew' is not recognized`** — you are not in the project root, or you typed `gradlew`
instead of `.\gradlew.bat` on Windows.

**`Permission denied: ./gradlew`** on macOS or Linux — run `chmod +x gradlew` first.

**Build fails on a missing Android platform** — open the SDK Manager in Android Studio and
install the Android 36 SDK platform.

---

## How to Use the App

1. Open **RefExtract** on your phone or emulator.
2. Type or paste any text into the main text input box.
3. The app analyzes your text automatically as you type.
4. Detected items are highlighted in the interactive viewer by category:
   - **References and Citations**: DOIs, academic citations, bracketed numbers, web links.
   - **Quotations and Direct Speech**: Direct quotes with speaker attributions.
   - **Named Entities**: People, companies, institutions, locations, technologies.
   - **Metrics and Financials**: Currencies, percentages, quantities.
   - **Temporal Dates**: Calendar dates, numeric formats, fiscal quarters.
5. Tap any highlighted element to inspect its metadata.
6. Use the filter chips to narrow results by category.
7. Use the top menu to export results in JSON, CSV, or Markdown format.
