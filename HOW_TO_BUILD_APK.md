# How to Build the APK

## 🎯 You have the complete source code - now let's build it!

The APK file needs to be built on a machine with Android development tools. Here are 3 methods:

---

## ✅ METHOD 1: Android Studio (EASIEST - Recommended)

### Step 1: Install Android Studio
- Download from: https://developer.android.com/studio
- Install and open it

### Step 2: Open the Project
1. Open Android Studio
2. Click "Open" (not "New Project")
3. Navigate to `/workspace` folder
4. Click "OK"
5. Wait 2-5 minutes for Gradle sync and dependency download

### Step 3: Build the APK
1. Click **Build** menu → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Wait 1-2 minutes for build to complete
3. Click "locate" in the popup notification

**APK Location:**
```
/workspace/app/build/outputs/apk/debug/app-debug.apk
```

### Step 4: Install on Your Phone
**Option A: Direct USB Install**
- Enable Developer Options and USB Debugging on your phone
- Connect phone to computer via USB
- Click **Run** button in Android Studio
- Select your device
- App installs automatically

**Option B: Transfer APK**
- Copy `app-debug.apk` to your phone
- Open file on phone
- Tap "Install"
- Grant "Install from unknown sources" if prompted

---

## ⚡ METHOD 2: Command Line (Fast)

### Prerequisites
- Install Android Studio first (for Android SDK)
- Or install Android SDK separately

### Build Commands

**On macOS/Linux:**
```bash
cd /workspace
chmod +x gradlew
./gradlew assembleDebug
```

**On Windows:**
```cmd
cd \workspace
gradlew.bat assembleDebug
```

**Build time:** 2-5 minutes (first time downloads dependencies)

**Output location:**
```
app/build/outputs/apk/debug/app-debug.apk
```

### Install on Phone
```bash
# Make sure phone is connected via USB with USB debugging enabled
./gradlew installDebug
```

---

## 🌐 METHOD 3: Online Build Services (No Setup Required)

If you don't want to install Android Studio, you can use online build services:

### Option A: GitHub Actions (Free)

1. **Push code to GitHub:**
   ```bash
   cd /workspace
   git add .
   git commit -m "Quiz Answerer app"
   git push origin main
   ```

2. **Create `.github/workflows/build.yml`:**
   ```yaml
   name: Build APK
   on: [push]
   jobs:
     build:
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v2
         - uses: actions/setup-java@v2
           with:
             java-version: '17'
             distribution: 'adopt'
         - name: Build APK
           run: |
             chmod +x gradlew
             ./gradlew assembleDebug
         - uses: actions/upload-artifact@v2
           with:
             name: app-debug
             path: app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Download APK:**
   - Go to Actions tab on GitHub
   - Click latest workflow run
   - Download artifact

### Option B: Appetize.io or Similar
- Upload project to cloud build service
- They build and provide download link

---

## 📱 Quick Build Guide (Step by Step)

### If you have Android Studio:

```
1. Download and install Android Studio
   └─ https://developer.android.com/studio
   └─ Takes ~5 minutes to install

2. Open /workspace in Android Studio
   └─ Click "Open"
   └─ Select the /workspace folder
   └─ Wait for Gradle sync (~3 minutes first time)

3. Build APK
   └─ Menu: Build → Build Bundle(s) / APK(s) → Build APK(s)
   └─ Wait ~2 minutes
   └─ Click "locate" to find APK

4. Get your APK!
   └─ Location: app/build/outputs/apk/debug/app-debug.apk
   └─ File size: ~5-8 MB
   └─ Ready to install!
```

---

## 🔍 Finding the Built APK

After building, your APK will be at:

**Full path:**
```
/workspace/app/build/outputs/apk/debug/app-debug.apk
```

**In file explorer:**
```
workspace/
└── app/
    └── build/
        └── outputs/
            └── apk/
                └── debug/
                    └── app-debug.apk  ← HERE!
```

**File info:**
- **Name:** app-debug.apk
- **Size:** ~5-8 MB
- **Type:** Debug build (for testing)

---

## 📲 Installing the APK on Your Phone

### Method A: USB Cable
1. Enable Developer Options on phone:
   - Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect phone to computer
4. Copy APK to phone or use `adb install app-debug.apk`
5. Open APK file on phone and install

### Method B: Cloud Transfer
1. Upload APK to Google Drive / Dropbox / OneDrive
2. Open link on phone
3. Download APK
4. Tap to install
5. Allow "Install from unknown sources" if prompted

### Method C: Direct Download
1. Host APK on any file server
2. Open link on phone browser
3. Download and install

---

## ⚙️ Build Configuration

Your project is already configured:
- **Min SDK:** API 26 (Android 8.0)
- **Target SDK:** API 34 (Android 14)
- **Build Tools:** 8.2.0
- **Kotlin:** 1.9.20
- **API Key:** Pre-configured ✅

All dependencies will be downloaded automatically during first build.

---

## 🐛 Troubleshooting

### "Gradle sync failed"
**Solution:**
- File → Invalidate Caches / Restart
- Wait for re-sync

### "Android SDK not found"
**Solution:**
- Android Studio → Settings → Android SDK
- Install SDK Platform for API 34
- Install Build Tools 34.0.0

### "Build failed"
**Solution:**
- Build → Clean Project
- Build → Rebuild Project

### "Permission denied: ./gradlew"
**Solution:**
```bash
chmod +x gradlew
```

### Build takes too long
**First build:** 5-10 minutes (downloading dependencies)
**Subsequent builds:** 1-2 minutes

---

## 💾 Build Output Sizes

| File | Size |
|------|------|
| app-debug.apk | ~5-8 MB |
| With dependencies | ~8-10 MB |
| Release APK (signed) | ~5-7 MB |

---

## 🚀 Advanced: Release Build (For Distribution)

If you want to publish the app:

### 1. Create Keystore
```bash
keytool -genkey -v -keystore quiz-answerer.keystore \
  -alias quiz-answerer -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configure signing in `app/build.gradle.kts`
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("quiz-answerer.keystore")
            storePassword = "your-password"
            keyAlias = "quiz-answerer"
            keyPassword = "your-password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
        }
    }
}
```

### 3. Build Release APK
```bash
./gradlew assembleRelease
```

**Output:** `app/build/outputs/apk/release/app-release.apk`

---

## 📦 What You'll Get

Your built APK includes:
- ✅ All app functionality
- ✅ Pre-configured Gemini API key
- ✅ Background mode with floating button
- ✅ OCR capabilities (ML Kit)
- ✅ Text-to-speech
- ✅ Material Design UI

**Ready to install and use immediately!**

---

## ⏱️ Time Estimates

| Task | Time |
|------|------|
| Install Android Studio | 5-10 minutes |
| Open project & sync | 3-5 minutes |
| First build | 5-10 minutes |
| Subsequent builds | 1-2 minutes |
| Install on phone | 1 minute |
| **Total (first time)** | **15-25 minutes** |
| **Total (after setup)** | **2-3 minutes** |

---

## 🎯 Quick Start (Absolute Fastest)

**If you already have Android Studio:**

1. Open Android Studio
2. Open `/workspace` folder
3. Wait for sync
4. Press Ctrl+F9 (Windows) or Cmd+F9 (Mac)
5. APK ready in ~2 minutes!

---

## 📞 Need Help?

**Build issues?**
- Check [Android Studio Troubleshooting](https://developer.android.com/studio/troubleshoot)
- Verify Android SDK is installed
- Check Java version (need JDK 17+)

**Installation issues?**
- Enable "Install unknown apps" in phone settings
- Check phone has Android 8.0+
- Verify APK is not corrupted (should be 5-8 MB)

---

## ✅ Verification

After installing, the app should:
- Show Quiz Answerer icon in app drawer
- Open to main screen with API key pre-filled
- Have "START BACKGROUND MODE" button
- Request permissions on first use

---

**Your APK will be ready in 2-5 minutes!** 🚀

Just open the project in Android Studio and click Build → Build APK(s)!
