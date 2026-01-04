# Screenshot Answer App

An Android app that captures screenshots, extracts text using OCR, sends multiple choice questions to Google's Gemini AI, and speaks the correct answers aloud using text-to-speech.

## Features

- 📸 **One-tap screenshot capture** - Floating button works over any app
- 🔍 **AI-powered OCR** - Uses Google ML Kit for accurate text extraction
- 🤖 **Gemini AI Integration** - Answers multiple choice questions automatically
- 🔊 **Text-to-Speech** - Speaks answers aloud so you can hear them
- 🎨 **Modern Material Design UI** - Clean, intuitive interface

## Requirements

- Android 8.0 (API 26) or higher
- Internet connection for Gemini AI
- Permissions:
  - Screen capture
  - Overlay (draw over other apps)
  - Notifications (for foreground service)

## How It Works

1. **Start the Service** - Launch the app and tap "Start Service"
2. **Grant Permissions** - Allow screen capture and overlay permissions
3. **Capture Screenshots** - A floating button appears; tap it to capture any screen
4. **Get Answers** - The app:
   - Captures the current screen
   - Extracts text using OCR (ML Kit)
   - Identifies multiple choice questions
   - Sends them to Gemini AI for answers
   - Speaks the correct answers aloud

## Building the App

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or later
- JDK 17
- Android SDK 34

### Build Steps

1. Clone or download this repository
2. Open the project in Android Studio
3. Wait for Gradle sync to complete
4. Build the project: `Build > Make Project`
5. Run on device/emulator: `Run > Run 'app'`

### Command Line Build

```bash
cd ScreenshotAnswerApp
./gradlew assembleDebug
```

The APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

## API Key

The app comes pre-configured with a Gemini API key. If you want to use your own:

1. Get a free API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Enter it in the app's API key field before starting the service

## Project Structure

```
ScreenshotAnswerApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/screenshotanswer/app/
│   │   │   ├── MainActivity.kt           # Main UI activity
│   │   │   ├── services/
│   │   │   │   ├── FloatingButtonService.kt    # Floating button overlay
│   │   │   │   └── ScreenCaptureService.kt     # Screen capture handling
│   │   │   └── utils/
│   │   │       ├── ApiKeyManager.kt      # API key storage
│   │   │       └── GeminiService.kt      # Gemini AI integration
│   │   ├── res/
│   │   │   ├── layout/                   # UI layouts
│   │   │   ├── drawable/                 # Icons and graphics
│   │   │   └── values/                   # Strings, colors, themes
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

## Dependencies

- **AndroidX** - Core Android libraries
- **Material Components** - Modern UI components
- **ML Kit Text Recognition** - OCR for extracting text from screenshots
- **Google Generative AI** - Gemini AI SDK
- **OkHttp** - HTTP client for API calls
- **Kotlin Coroutines** - Async programming

## Usage Tips

- **Clear Screenshots**: Make sure questions are clearly visible on screen
- **Multiple Choice Format**: Works best with clearly formatted MCQ questions (A, B, C, D options)
- **Draggable Button**: You can drag the floating button to reposition it
- **Listen Carefully**: The app speaks answers aloud - make sure volume is up!

## Troubleshooting

### "Screen capture permission denied"
- Make sure you grant the screen capture permission when prompted
- If denied, restart the app and try again

### "Overlay permission required"
- Go to Settings > Apps > Screenshot Answer > Display over other apps > Allow

### "No text found in screenshot"
- Ensure the screen contains readable text
- Try with better lighting if text is unclear

### Answers not speaking
- Check that your device volume is up
- Make sure TTS (Text-to-Speech) is enabled in device settings

## License

This project is for educational purposes. Use responsibly.

## Credits

- Google ML Kit for OCR
- Google Gemini AI for question answering
- Material Design for UI components
