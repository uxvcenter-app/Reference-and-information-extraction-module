RefExtract User Guide
Prerequisites
Install Java JDK 17 or higher on your computer before proceeding. If you plan to install directly onto a phone connected via USB, enable USB Debugging on the device.

Option 1: Automatic Setup via Scripts
Windows (PowerShell)
Run from inside the project folder (uses current directory by default):

powershell


cd C:\path\to\refextract
.\run.ps1
Or pass the project directory as a parameter:

powershell


.\run.ps1 -ProjectDir "C:\path\to\refextract"
macOS and Linux (Bash)
Run from inside the project folder (uses current directory by default):

bash


cd /path/to/refextract
chmod +x run.sh
./run.sh
Or pass the project directory as an argument:

bash


./run.sh /path/to/refextract
What the scripts do
Verify that Java is installed on your system.
Compile the application into an installable package file.
If an Android device or emulator is connected via USB, install and launch the app automatically.
If no device is connected, display the file path of the compiled package so you can transfer it to your phone manually.
Option 2: Manual Build
For Windows:

cmd


cd C:\path\to\refextract
gradle assembleDebug
For macOS or Linux:

bash


cd /path/to/refextract
./gradlew assembleDebug
The compiled package is generated at: app/build/outputs/apk/debug/app-debug.apk

Transfer this file to your phone via USB cable, cloud storage, or email. Tap the file on your phone to install it. If prompted, allow installation from unknown sources in your phone settings.

How to Use the App
Open RefExtract on your phone or emulator.
Type or paste any text into the main input box. The app analyzes your text automatically as you type.
Detected items are highlighted in the interactive viewer by category:
References and Citations (DOIs, academic citations, bracketed numbers, web links)
Quotations and Direct Speech with speaker attributions
Named Entities (People, Companies, Institutions, Locations, Technologies)
Metrics and Financials (Currencies, Percentages, Quantities)
Temporal Dates (Calendar dates, Numeric formats, Fiscal quarters)
Tap any highlighted element to inspect its metadata.
Use the filter chips to narrow results by category.
Use the top menu to export results in JSON, CSV, or Markdown format.
