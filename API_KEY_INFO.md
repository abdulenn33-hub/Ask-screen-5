# API Key Configuration

## Current Configuration

This app is pre-configured with a Gemini API key:
```
AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ
```

The API key is automatically loaded when the app first launches, so users can start using the app immediately without manual configuration.

## Location in Code

The API key is set in:
- **File**: `app/src/main/java/com/quizanswerer/app/MainActivity.kt`
- **Method**: `loadApiKey()`
- **Line**: ~80-90

## Security Considerations

⚠️ **Important Security Notes:**

1. **Public Repository**: If this code is pushed to a public repository (GitHub, GitLab, etc.), the API key will be visible to anyone.

2. **API Key Exposure**: Anyone with access to the APK can decompile it and extract the API key.

3. **Rate Limits**: The Gemini free tier has usage limits:
   - 15 requests per minute
   - 1,500 requests per day
   
4. **Potential Abuse**: If many people use this key, you might hit rate limits or incur charges (if on paid tier).

## Recommendations

### For Personal Use Only:
✅ Current setup is fine - convenient and works out of the box

### For Public Distribution:
Consider these alternatives:

1. **Remove hardcoded key**: Have users enter their own API key
2. **Backend proxy**: Route requests through your own server
3. **Key rotation**: Regularly change the API key
4. **Usage monitoring**: Monitor the key usage in Google AI Studio

## Changing the API Key

### Method 1: In the App (User-Friendly)
1. Open the app
2. Clear the API key field at the top
3. Enter a new API key
4. Tap "SAVE API KEY"

### Method 2: In Code (For Developers)
Edit `MainActivity.kt`, line with `defaultApiKey`:
```kotlin
val defaultApiKey = "YOUR_NEW_API_KEY_HERE"
```

### Method 3: Remove Default Key
To require users to enter their own key:
```kotlin
val defaultApiKey = ""
```

## Monitoring Usage

Track your API usage at:
- [Google AI Studio](https://makersuite.google.com/)
- Check quota limits
- Monitor for unusual activity
- Set up alerts if available

## Best Practices

✅ **DO:**
- Monitor API usage regularly
- Keep code in private repository if using hardcoded key
- Consider backend proxy for production apps
- Educate users about responsible usage

❌ **DON'T:**
- Share the APK publicly with hardcoded key
- Publish code with API key to public GitHub
- Use paid tier keys without protection
- Ignore usage spikes or suspicious activity

## Alternative: Environment-Based Configuration

For production apps, consider:

```kotlin
// In build.gradle.kts
android {
    defaultConfig {
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
    }
}

// In MainActivity.kt
val defaultApiKey = BuildConfig.GEMINI_API_KEY
```

Then in `local.properties` (not committed to git):
```properties
GEMINI_API_KEY=AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ
```

---

**Current Status**: ✅ API key is configured and ready to use!

Users can start using the app immediately without any setup.
