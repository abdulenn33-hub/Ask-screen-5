# Quick Setup Guide

## 🚀 Getting Started in 5 Minutes

### Step 1: Get Your Gemini API Key (2 minutes)

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with your Google account
3. Click **"Create API Key"** button
4. Copy the API key (it looks like: `AIza...`)

### Step 2: Build the App (2 minutes)

#### Option A: Using Android Studio (Recommended)
1. Open Android Studio
2. Click "Open" and select this project folder
3. Wait for Gradle sync to complete
4. Connect your Android device or start an emulator
5. Click the green **Run** button (▶️)

#### Option B: Using Command Line
```bash
# On macOS/Linux
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug

# Install on connected device
./gradlew installDebug
```

### Step 3: Use the App (1 minute)

1. **First Launch**: Paste your Gemini API key and tap "SAVE API KEY"
2. **Grant Permissions**: Allow notifications and media access
3. **Start Using**:
   - Tap "TAKE SCREENSHOT" to capture your screen
   - OR tap "SELECT IMAGE" to pick an existing image
4. **Wait**: The app will automatically:
   - Extract text (OCR)
   - Find questions
   - Get answers from Gemini
   - Read them aloud!

## 📱 System Requirements

- Android device with API 26+ (Android 8.0 Oreo or newer)
- Internet connection (for Gemini AI)
- ~50MB storage space

## ✅ Permissions Explained

When you first use the app, it will ask for:

| Permission | Why We Need It |
|------------|----------------|
| 📸 Screen Capture | To take screenshots of your quiz questions |
| 🖼️ Photos/Media | To select existing images from gallery |
| 🔔 Notifications | To show when screenshot service is running |

## 🎯 Supported Question Formats

The app works best with multiple-choice questions in these formats:

### Format 1: Letter Options
```
What is the capital of France?
A. London
B. Paris
C. Berlin
D. Madrid
```

### Format 2: With Numbers
```
1. What is 2 + 2?
A) 3
B) 4
C) 5
D) 6
```

### Format 3: With Periods
```
Q1: Which is larger?
a. Kilobyte
b. Megabyte
c. Gigabyte
d. Terabyte
```

## 🔧 Troubleshooting

### "No text found in image"
- ✅ Make sure the image is clear and not blurry
- ✅ Ensure good lighting and contrast
- ✅ Text should be horizontal and readable
- ✅ Try zooming in on the question area

### "Failed to get answer from Gemini"
- ✅ Check your internet connection
- ✅ Verify your API key is correct
- ✅ Make sure you haven't exceeded free tier limits
- ✅ Try again in a few seconds

### "Permission denied"
- ✅ Go to Settings → Apps → Quiz Answerer → Permissions
- ✅ Enable all required permissions
- ✅ Restart the app

### Can't hear answers
- ✅ Turn up your device volume
- ✅ Ensure media volume (not just ringer) is up
- ✅ Check if Text-to-Speech is enabled in Accessibility settings

## 💡 Pro Tips

1. **Better OCR Results**: 
   - Use high-resolution screenshots
   - Avoid curved or skewed text
   - Remove unnecessary background elements

2. **Faster Processing**:
   - Crop images to show only the question area
   - Ensure questions are well-formatted
   - Use clear fonts (avoid handwriting)

3. **API Usage**:
   - The free tier has usage limits
   - Each question uses ~100-500 tokens
   - Monitor your usage at [Google AI Studio](https://makersuite.google.com/)

## 🔐 Privacy & Security

- Your API key is stored **only on your device**
- Images are processed **locally** for OCR
- Only text is sent to Gemini (not images)
- No data is collected or sent to third parties
- Screenshots are stored temporarily and can be deleted

## 📚 Example Workflow

```
1. Open quiz on another device/website
2. Take screenshot of questions
3. App extracts: "What is Python?"
   A. Snake
   B. Programming Language
   C. Type of Bread
   D. None
4. Gemini answers: "B. Programming Language"
5. Hear: "What is Python? The correct answer is B. Programming Language"
```

## 🆘 Still Need Help?

1. Check the main [README.md](README.md) for detailed documentation
2. Review error messages in the app
3. Test with the example questions below
4. Open an issue on GitHub

## 🧪 Test the App

Try with this sample question image content:

```
Question 1: What does AI stand for?
A. Artificial Intelligence
B. Automated Information
C. Advanced Integration
D. None of the above

Question 2: Which language is used for Android development?
A. Swift
B. Kotlin
C. Ruby
D. PHP
```

Expected results:
- Q1: A. Artificial Intelligence
- Q2: B. Kotlin

---

**Ready to ace your quizzes! 🎓✨**

For more details, see [README.md](README.md)
