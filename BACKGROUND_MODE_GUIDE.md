# Background Mode Guide

## 🎯 Overview

The Quiz Answerer app now supports **Background Mode**, allowing you to capture and answer quiz questions from any app without switching back to the Quiz Answerer app!

## ✨ Key Features

### 1. Floating Button
- **Always visible** camera button overlay
- **Draggable** - move it anywhere on screen
- **One-tap capture** - tap to instantly process screenshots
- Works **on top of other apps**

### 2. Background Processing
- App runs as a **foreground service**
- Captures screenshots from **any app**
- Processes questions **in the background**
- Shows results in **notifications**
- Reads answers **out loud** automatically

### 3. Notification Controls
- Quick **Capture** button in notification
- **Stop** button to end background mode
- Real-time **status updates**
- **Result notifications** with answers

## 🚀 How to Use Background Mode

### Step 1: Enable Background Mode

1. Open Quiz Answerer app
2. Tap **"START BACKGROUND MODE"** button
3. Grant overlay permission when prompted
4. Grant screen capture permission
5. The app minimizes and shows a floating button

### Step 2: Using the Floating Button

The floating purple camera button will appear on your screen:

```
┌─────────────────────────────┐
│                          📷 │ ← Floating button
│                             │
│    Your quiz app or         │
│    website content          │
│                             │
│                             │
└─────────────────────────────┘
```

**To capture and answer:**
- Tap the floating button
- Wait 10-20 seconds
- Check notification for results
- Listen to answers read aloud

**To move the button:**
- Press and drag to reposition
- Place it wherever is convenient

### Step 3: View Results

Results appear in two ways:

**1. Notification:**
```
┌─────────────────────────────────────┐
│ 🔔 Found 3 question(s)              │
│                                      │
│ Q1: B. Paris                        │
│ Q2: C. Python                       │
│ Q3: A. Artificial Intelligence      │
│                                      │
│ Tap to expand                       │
└─────────────────────────────────────┘
```

**2. Audio (Text-to-Speech):**
- Reads each question aloud
- Announces the correct answer
- Sequential for multiple questions

### Step 4: Stop Background Mode

**Option A: From App**
1. Open Quiz Answerer app
2. Tap "STOP BACKGROUND MODE"

**Option B: From Notification**
1. Expand notification
2. Tap "Stop" button

## 📱 Permissions Required

### Overlay Permission (Draw over other apps)
**Why:** Displays the floating button on top of other apps
**When:** First time you enable background mode
**How to grant:**
1. App opens Settings automatically
2. Enable "Display over other apps"
3. Return to app

### Screen Capture Permission
**Why:** Takes screenshots in background
**When:** When starting background mode
**How to grant:**
1. Tap "Start now" in permission dialog
2. Permission saved for future use

## 🎮 Use Cases

### 1. Online Quizzes
```
Scenario: Taking a quiz on a website
1. Open quiz website in browser
2. Enable background mode
3. Read question on screen
4. Tap floating button
5. Hear answer immediately
6. Continue to next question
```

### 2. Learning Apps
```
Scenario: Using educational app
1. Start background mode
2. Navigate to practice questions
3. Tap floating button for each question
4. Get instant help
5. Learn from answers
```

### 3. Study Sessions
```
Scenario: Multiple apps/PDFs
1. Background mode stays active
2. Switch between different sources
3. Capture from any app
4. All answers read aloud
5. Focus on learning
```

## ⚙️ How It Works

### Architecture
```
┌─────────────────────────────────────────┐
│  Any App (Quiz, Browser, PDF, etc.)    │
│                                         │
│         [Floating Button] 📷           │
└─────────────────────────────────────────┘
                  ↓ Tap
┌─────────────────────────────────────────┐
│  Background Quiz Service                │
│  ├─ Capture Screenshot                  │
│  ├─ OCR Text Extraction                 │
│  ├─ Parse Questions                     │
│  ├─ Query Gemini AI                     │
│  └─ Speak Results                       │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│  Notification with Answers              │
│  + Text-to-Speech Audio                 │
└─────────────────────────────────────────┘
```

### Processing Flow
1. **Tap floating button** → Triggers capture
2. **Screenshot taken** → 2 seconds
3. **OCR processing** → 2-4 seconds
4. **Question parsing** → <1 second
5. **AI answering** → 2-5 seconds per question
6. **TTS output** → Speaks answers
7. **Notification** → Shows results

## 🔋 Battery & Performance

### Battery Usage
- **Low impact** - uses standard foreground service
- **Efficient** - only processes when you tap button
- **No constant scanning** - waits for your input

### Performance
- **Lightweight** - ~50-100 MB RAM
- **Fast** - Results in 10-20 seconds
- **Reliable** - Runs until you stop it

### Data Usage
- **Minimal** - Only text sent to Gemini API
- **No image upload** - OCR done locally
- **Per question** - ~5-10 KB per API call

## 📋 Status Messages

The notification shows these statuses:

| Status | Meaning |
|--------|---------|
| **Ready** | Waiting for you to tap button |
| **Capturing** | Taking screenshot now |
| **Processing** | Extracting text with OCR |
| **Getting answers** | Querying Gemini AI |
| **Found X questions** | Results ready, check notification |
| **Error: ...** | Something went wrong |

## ⚠️ Important Notes

### Privacy
- ✅ Screenshots processed locally
- ✅ Only text sent to Gemini (not images)
- ✅ No data stored or logged
- ✅ Service stops when you stop it

### Limitations
- Works best with **clear, printed text**
- Requires **internet connection**
- **API rate limits** apply (1,500/day free tier)
- Only **multiple-choice questions** supported

### Best Practices
1. **Position button** out of quiz content area
2. **Wait for status** before next capture
3. **Monitor volume** for TTS output
4. **Check notifications** for written results
5. **Stop service** when done to save battery

## 🔧 Troubleshooting

### Floating button doesn't appear
- ✅ Check overlay permission in Settings
- ✅ Restart background mode
- ✅ Try restarting the app

### No results in notification
- ✅ Wait full 15-30 seconds
- ✅ Check internet connection
- ✅ Ensure question is visible and clear
- ✅ Check notification permissions

### Can't hear answers
- ✅ Turn up **media volume** (not ringer)
- ✅ Check Do Not Disturb settings
- ✅ Test TTS in Settings → Accessibility

### Button keeps returning to same position
- **This is normal** - button resets on each capture
- Move it again after processing completes

### High battery usage
- Stop background mode when not needed
- Reduce number of captures
- Check for other battery-draining apps

## 🎯 Tips & Tricks

### Efficient Usage
1. **Batch questions** - Keep background mode running
2. **Position strategically** - Bottom corner usually works best
3. **Use audio** - No need to check notifications constantly
4. **Clear screenshots** - Zoom in on questions for better OCR

### Quick Workflow
```
1. Start background mode (once)
2. Open quiz/study material
3. Tap → Listen → Answer → Next
4. Repeat steps 3 for all questions
5. Stop background mode when done
```

### Multi-app Usage
The floating button works across:
- ✅ Web browsers (Chrome, Firefox, etc.)
- ✅ PDF readers
- ✅ Learning apps (Khan Academy, Duolingo, etc.)
- ✅ Social media (if quiz shared)
- ✅ Screenshot galleries
- ✅ Any app showing questions

## 🆚 Background Mode vs Regular Mode

| Feature | Regular Mode | Background Mode |
|---------|--------------|-----------------|
| App switching | ❌ Required | ✅ Not needed |
| Floating button | ❌ No | ✅ Yes |
| Works in other apps | ❌ No | ✅ Yes |
| Results display | App screen | Notification |
| Audio answers | ✅ Yes | ✅ Yes |
| Battery usage | Lower | Slightly higher |
| Convenience | Good | Excellent |

## 📱 Supported Android Versions

- **Android 8.0+** (API 26+) - Background mode available
- **Android 10+** - Best performance
- **Android 13+** - Enhanced notifications

## 🔐 Security & Privacy

### What's monitored
- ❌ No screen recording
- ❌ No continuous screenshots
- ✅ Only captures when YOU tap button

### Data handling
- Screenshots stored **temporarily** in app folder
- Deleted automatically after processing
- Text sent to Gemini AI only
- No analytics or tracking

### Permissions
All permissions explained:
- **Overlay** - Show floating button
- **Screen capture** - Take screenshots on demand
- **Internet** - Gemini AI API
- **Notifications** - Show results

## 💡 Example Scenarios

### Scenario 1: Web Quiz
```
1. Open quiz website
2. Start background mode
3. Minimize Quiz Answerer
4. Website shows: "What is the capital of France?"
5. Tap floating button
6. Hear: "The answer is B. Paris"
7. Select B and continue
```

### Scenario 2: Study with PDF
```
1. Open PDF with practice questions
2. Enable background mode
3. Read question 1
4. Tap floating button
5. Notification shows answer
6. Check your work
7. Move to question 2
```

### Scenario 3: Rapid Fire Quiz
```
1. Background mode active
2. Questions appear every 30 seconds
3. Quick tap → Answer → Next
4. Audio answers while answering
5. Complete quiz faster
```

## 🎓 Educational Use

**Recommended:**
- ✅ Study aids and practice
- ✅ Checking your work
- ✅ Learning from AI explanations
- ✅ Understanding concepts

**Not recommended:**
- ❌ Graded exams or tests
- ❌ Academic dishonesty
- ❌ Professional certifications
- ❌ Situations where it's prohibited

## 🚀 Future Enhancements

Possible future features:
- Multiple floating button sizes
- Custom button positions (saved)
- Faster processing modes
- Offline question bank
- History of answered questions
- Export answers to notes

## 📞 Support

Having issues with background mode?

1. Check this guide's troubleshooting section
2. Verify all permissions are granted
3. Test with simple questions first
4. Check main README.md for more help

---

**Background Mode Status**: ✅ Fully Functional

**Perfect for**: Active learners, quiz takers, students

**Best feature**: Seamless experience without app switching!

🎉 **Enjoy hands-free quiz answering!**
