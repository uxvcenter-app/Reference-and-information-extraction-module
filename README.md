# RefExtract - Reference & Information Extraction Android App

RefExtract is a Kotlin Jetpack Compose Android application designed for real-time text analysis, citation tracking, reference extraction, named entity recognition, and metric categorization.

## Features

- **Real-Time Live Extraction**: Debounced text analysis as you type or paste documents.
- **Reference & Citation Parser**: Detects DOIs, arXiv preprints, APA/Harvard citations, bracketed numeric citations (`[1]`, `[12-15]`), patents, ISBNs, and web URLs.
- **Named Entity Recognition**: Identifies people, organizations (corporate & institutional), locations, and technology terms.
- **Metrics & Quantitative Analytics**: Extracts currencies with comma formatting (`$1,500.50`, `€50k`), percentages, and measurement units.
- **Temporal & Date Extractor**: Detects calendar dates, numeric dates (`YYYY-MM-DD`), fiscal quarters (`Q3 2024`), and contextual years.
- **Direct Speech & Quotes**: Captures quoted text with speaker attribution.
- **Custom Rules Engine**: User-definable regex extraction rules stored locally in Room Database.
- **Export Options**: Export structured results to JSON, CSV, and Markdown.

## Building and Running

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17 or Java 21
- Android SDK 24+

### Build Steps
1. Clone or download the repository.
2. Open the project in Android Studio.
3. Build and run on an Android Emulator or physical device:
   ```bash
   ./gradlew assembleDebug
   ```
