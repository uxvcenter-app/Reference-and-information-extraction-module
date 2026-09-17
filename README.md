Searched for "apiKey"
Viewed GeminiExtractionService.kt:1-194
Viewed build.gradle.kts:1-141
Viewed .env.example:1-4
Searched for "API Key"

# RefExtract User Guide

## Prerequisites

- **Java JDK 17 or higher**: Install on your computer before building the project.
- **USB Debugging**: Enable on your mobile device if installing directly over USB cable.

---

## Setting Up Your Gemini API Key (Optional AI Features)

RefExtract runs with local extraction by default. If you want to enable advanced cloud AI extraction using Gemini:

1. Copy `.env.example` in the root folder of the project and rename the copy to `.env`.
2. Open `.env` in any text editor.
3. Replace `MY_GEMINI_API_KEY` with your actual Google Gemini API Key:
   ```text
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
4. Build the application package. The build process automatically bundles the key. If no key is configured, the application smoothly uses built-in local engine rules.

---

## Option 1: Automatic Setup via Scripts

### Windows (PowerShell)

Run from inside the project folder (uses current directory by default):
```powershell
cd C:\path\to\refextract
.\run.ps1
```

Or pass the project directory as a parameter:
```powershell
.\run.ps1 -ProjectDir "C:\path\to\refextract"
```

### macOS and Linux (Bash)

Run from inside the project folder (uses current directory by default):
```bash
cd /path/to/refextract
chmod +x run.sh
./run.sh
```

Or pass the project directory as an argument:
```bash
./run.sh /path/to/refextract
```

### What the Scripts Do

1. Verify that Java is installed on your system.
2. Compile the application into an installable package file (`app-debug.apk`).
3. If an Android device or emulator is connected via USB with debugging active, install and launch the app automatically.
4. If no device is connected, display the full path of the compiled package file so you can transfer it to your phone manually.

---

## Option 2: Manual Build

### Windows

```cmd
cd C:\path\to\refextract
gradle assembleDebug
```

### macOS or Linux

```bash
cd /path/to/refextract
./gradlew assembleDebug
```

The compiled package is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

Transfer this file to your phone via USB cable, cloud storage, or email. Tap the file on your phone to install it. If prompted, allow installation from unknown sources in your phone settings.

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
