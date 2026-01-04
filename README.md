# Quiz Answerer - Android App

An Android application that captures screenshots, extracts multiple-choice questions using OCR, gets answers from Google Gemini AI, and reads the answers aloud using Text-to-Speech.

## Features

- 📸 **Screenshot Capture**: Take screenshots directly from the app using Media Projection API
- 🖼️ **Image Selection**: Select existing images from your gallery
- 🔍 **OCR Processing**: Extract text from images using Google ML Kit Text Recognition
- 🤖 **AI-Powered Answers**: Get correct answers using Google Gemini AI
- 🔊 **Text-to-Speech**: Hear questions and answers read aloud automatically
- 📱 **Multiple Choice Support**: Automatically detects and parses multiple-choice questions

## Prerequisites

Before building and running the app, you need:

1. **Android Studio** (latest version recommended)
2. **Android SDK** with minimum API level 26 (Android 8.0)
3. **Google Gemini API Key** - Get one from [Google AI Studio](https://makersuite.google.com/app/apikey)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd QuizAnswerer
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository folder
4. Wait for Gradle sync to complete

### 3. Get Gemini API Key

1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated API key

### 4. Build the Project

In Android Studio:
1. Go to `Build` → `Make Project` (or press Ctrl+F9)
2. Wait for the build to complete successfully

### 5. Run on Device/Emulator

1. Connect an Android device (API 26+) with USB debugging enabled, or start an Android emulator
2. Click the "Run" button (green play icon) in Android Studio
3. Select your device/emulator
4. The app will install and launch automatically

## How to Use

### First Time Setup

1. **Enter API Key**: 
   - On first launch, enter your Gemini API key in the text field at the top
   - Click "SAVE API KEY" to store it securely
   - The API key is saved locally on your device

### Capturing Screenshots

1. **Grant Permissions**: 
   - The app will request necessary permissions (Notifications, Storage, etc.)
   - Accept all required permissions

2. **Take Screenshot**:
   - Click "TAKE SCREENSHOT" button
   - Grant screen capture permission when prompted
   - The app will capture your screen after a 2-second delay
   - The screenshot will be automatically processed

### Using Existing Images

1. Click "SELECT IMAGE" button
2. Choose an image containing multiple-choice questions from your gallery
3. The app will automatically process it

### Results

After processing:
- The screenshot/image preview will appear
- Extracted questions will be displayed in the results area
- For each question:
  - The question text and options are shown
  - Gemini AI provides the correct answer
  - Text-to-Speech reads the question and answer aloud
- All results are displayed in the scrollable text area

## App Architecture

### Components

```
com.quizanswerer.app/
├── MainActivity.kt           # Main UI and coordination logic
├── ScreenshotService.kt      # Background service for screen capture
├── Question.kt               # Data model for questions
├── QuestionParser.kt         # Parses text into Question objects
└── GeminiHelper.kt          # Handles Gemini AI API calls
```

### Flow Diagram

```
Screenshot/Image
    ↓
ML Kit OCR (Text Extraction)
    ↓
Question Parser (Extracts Questions & Options)
    ↓
Gemini AI (Answers Questions)
    ↓
Text-to-Speech (Reads Results)
```

## Technologies Used

- **Kotlin** - Primary programming language
- **Android SDK** - Platform APIs
- **Google ML Kit** - Text Recognition (OCR)
- **Google Gemini AI** - Generative AI for answering questions
- **Media Projection API** - Screenshot capture
- **Text-to-Speech API** - Audio output
- **Material Design Components** - Modern UI
- **Coroutines** - Asynchronous programming

## Permissions Required

The app requires the following permissions:

- `INTERNET` - For Gemini AI API calls
- `READ_MEDIA_IMAGES` - To select images (Android 13+)
- `READ_EXTERNAL_STORAGE` - To select images (Android 12 and below)
- `FOREGROUND_SERVICE` - For screenshot service
- `FOREGROUND_SERVICE_MEDIA_PROJECTION` - For screen capture
- `POST_NOTIFICATIONS` - For service notifications (Android 13+)

## Build Configuration

- **Minimum SDK**: API 26 (Android 8.0 Oreo)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34
- **Build Tools**: Android Gradle Plugin 8.2.0
- **Kotlin Version**: 1.9.20

## Dependencies

Key dependencies:
```gradle
// ML Kit for OCR
implementation("com.google.mlkit:text-recognition:16.0.0")

// Gemini AI SDK
implementation("com.google.ai.client.generativeai:generativeai:0.1.2")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// AndroidX & Material Design
implementation("androidx.core:core-ktx:1.12.0")
implementation("com.google.android.material:material:1.11.0")
```

## Troubleshooting

### Common Issues

**1. OCR not detecting text**
- Ensure the image has clear, readable text
- Try adjusting image brightness/contrast
- Make sure text is in English (Latin script)

**2. Gemini API errors**
- Verify your API key is correct
- Check your internet connection
- Ensure you haven't exceeded API rate limits
- Verify API key has necessary permissions in Google AI Studio

**3. Screenshot service not working**
- Grant screen capture permission when prompted
- Ensure the service has proper foreground service permissions
- Check notification permissions on Android 13+

**4. Text-to-Speech not working**
- Check device volume
- Ensure TTS engine is installed on your device
- Go to Settings → Accessibility → Text-to-Speech to configure

**5. Build errors**
- Clean and rebuild: `Build → Clean Project`, then `Build → Rebuild Project`
- Invalidate caches: `File → Invalidate Caches / Restart`
- Ensure you have the latest Gradle and Android Studio updates

## Limitations

- Only supports multiple-choice questions (A, B, C, D format)
- OCR accuracy depends on image quality
- Requires internet connection for Gemini AI
- Text extraction works best with clear, printed text
- Screenshot capture requires Android 8.0+

## Security Notes

- API keys are stored locally in SharedPreferences
- Never commit your API key to version control
- The app uses secure storage practices
- API keys are excluded from backups

## Future Enhancements

Possible improvements:
- Support for more question formats (True/False, Fill-in-the-blank)
- Offline mode with cached ML models
- Custom quiz history and statistics
- Export results to PDF/Text
- Multi-language support
- Camera integration for real-time capture
- Handwriting recognition

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## License

This project is open source and available under the MIT License.

## Disclaimer

This app is for educational purposes. Always verify answers independently. The accuracy of answers depends on the Gemini AI model and image quality.

## Support

For issues or questions:
1. Check the Troubleshooting section above
2. Review [Google ML Kit documentation](https://developers.google.com/ml-kit/vision/text-recognition)
3. Review [Gemini AI documentation](https://ai.google.dev/docs)
4. Open an issue in this repository

## Acknowledgments

- Google ML Kit team for text recognition
- Google AI team for Gemini API
- Android developer community

---

**Happy Quiz Answering! 📚✨**
