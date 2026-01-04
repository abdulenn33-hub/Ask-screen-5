# Screenshot Answer App - Workflow Documentation

## Overview

This document describes the complete workflow of the Screenshot Answer app, from user interaction to speaking the answers aloud.

---

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              USER INTERACTION                                │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              MAIN ACTIVITY                                   │
│  • Enter API Key                                                            │
│  • Request Permissions (Overlay, Screen Capture, Notifications)             │
│  • Start/Stop Services                                                      │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                         ┌────────────┴────────────┐
                         ▼                         ▼
┌────────────────────────────────┐   ┌────────────────────────────────────────┐
│   SCREEN CAPTURE SERVICE       │   │      FLOATING BUTTON SERVICE           │
│  • Holds MediaProjection       │   │  • Displays floating button            │
│  • Captures screen on demand   │   │  • Handles user tap                    │
│  • Returns Bitmap              │   │  • Orchestrates the workflow           │
└────────────────────────────────┘   └────────────────────────────────────────┘
                                                   │
                         ┌─────────────────────────┼─────────────────────────┐
                         ▼                         ▼                         ▼
              ┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
              │   ML KIT OCR     │     │   GEMINI API     │     │ TEXT-TO-SPEECH   │
              │  Extract Text    │────▶│  Answer MCQs     │────▶│  Speak Answers   │
              └──────────────────┘     └──────────────────┘     └──────────────────┘
```

---

## Detailed Workflow Steps

### Phase 1: App Initialization

```
┌─────────────────────────────────────────────────────────────────┐
│                     1. USER LAUNCHES APP                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   2. MainActivity.onCreate()                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  • Inflate activity_main.xml layout                       │  │
│  │  • Initialize MediaProjectionManager                      │  │
│  │  • Load saved API key (or use default)                    │  │
│  │  • Setup button click listeners                           │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              3. UI DISPLAYED TO USER                             │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  [API Key Input Field] - Pre-filled with saved key        │  │
│  │  [Start Service Button]                                   │  │
│  │  [Stop Service Button] - Disabled                         │  │
│  │  [Help Button]                                            │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Phase 2: Starting the Service

```
┌─────────────────────────────────────────────────────────────────┐
│              4. USER TAPS "START SERVICE"                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              5. PERMISSION CHECKS                                │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Check 1: Overlay Permission (Draw over other apps)       │  │
│  │     └─▶ If not granted: Open system settings              │  │
│  │                                                           │  │
│  │  Check 2: Notification Permission (Android 13+)           │  │
│  │     └─▶ If not granted: Request permission                │  │
│  │                                                           │  │
│  │  Check 3: Screen Capture Permission                       │  │
│  │     └─▶ Show system dialog to allow screen recording      │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              6. ALL PERMISSIONS GRANTED                          │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌─────────────────────────┐     ┌─────────────────────────────────┐
│  7a. START SCREEN       │     │  7b. START FLOATING BUTTON      │
│      CAPTURE SERVICE    │     │      SERVICE                    │
│  ┌───────────────────┐  │     │  ┌───────────────────────────┐  │
│  │ • Create Media    │  │     │  │ • Create floating view    │  │
│  │   Projection      │  │     │  │ • Add to WindowManager    │  │
│  │ • Setup Virtual   │  │     │  │ • Initialize TTS engine   │  │
│  │   Display         │  │     │  │ • Show notification       │  │
│  │ • Create Image    │  │     │  └───────────────────────────┘  │
│  │   Reader          │  │     │                                 │
│  └───────────────────┘  │     │                                 │
└─────────────────────────┘     └─────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              8. APP MOVES TO BACKGROUND                          │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  • Floating button visible on screen                      │  │
│  │  • User can navigate to any other app                     │  │
│  │  • Services running in foreground                         │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Phase 3: Capturing & Processing Screenshot

```
┌─────────────────────────────────────────────────────────────────┐
│              9. USER TAPS FLOATING BUTTON                        │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Location: FloatingButtonService.onFloatingButtonClick()  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              10. HIDE FLOATING BUTTON                            │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  • Set floatingView.visibility = INVISIBLE                │  │
│  │  • Show loading indicator                                 │  │
│  │  • Wait 300ms for UI to update                            │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              11. CAPTURE SCREENSHOT                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Location: ScreenCaptureService.captureScreen()           │  │
│  │                                                           │  │
│  │  • Acquire latest image from ImageReader                  │  │
│  │  • Convert Image to Bitmap                                │  │
│  │  • Return Bitmap via callback                             │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              12. SHOW FLOATING BUTTON AGAIN                      │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  • Set floatingView.visibility = VISIBLE                  │  │
│  │  • Continue processing in background                      │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              13. OCR - EXTRACT TEXT                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Location: FloatingButtonService.performOCR()             │  │
│  │                                                           │  │
│  │  • Create InputImage from Bitmap                          │  │
│  │  • Get ML Kit TextRecognizer client                       │  │
│  │  • Process image                                          │  │
│  │  • Return extracted text string                           │  │
│  │                                                           │  │
│  │  Example Output:                                          │  │
│  │  "What is the capital of France?                          │  │
│  │   A) London                                               │  │
│  │   B) Paris                                                │  │
│  │   C) Berlin                                               │  │
│  │   D) Madrid"                                              │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              14. SEND TO GEMINI AI                               │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Location: GeminiService.answerQuestions()                │  │
│  │                                                           │  │
│  │  Step 1: Build Prompt                                     │  │
│  │  ┌─────────────────────────────────────────────────────┐  │  │
│  │  │ "You are a helpful assistant that answers MCQs...  │  │  │
│  │  │  Here is the extracted text: [OCR TEXT]            │  │  │
│  │  │  Please provide the answers..."                    │  │  │
│  │  └─────────────────────────────────────────────────────┘  │  │
│  │                                                           │  │
│  │  Step 2: Call Gemini API                                  │  │
│  │  ┌─────────────────────────────────────────────────────┐  │  │
│  │  │ POST https://generativelanguage.googleapis.com/    │  │  │
│  │  │      v1beta/models/gemini-1.5-flash:generateContent│  │  │
│  │  │ Headers: Content-Type: application/json            │  │  │
│  │  │ Body: { contents: [...], generationConfig: {...} } │  │  │
│  │  └─────────────────────────────────────────────────────┘  │  │
│  │                                                           │  │
│  │  Step 3: Parse Response                                   │  │
│  │  ┌─────────────────────────────────────────────────────┐  │  │
│  │  │ Extract: candidates[0].content.parts[0].text       │  │  │
│  │  │ Example: "Question 1: Capital of France.           │  │  │
│  │  │          The correct answer is B: Paris."          │  │  │
│  │  └─────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              15. SPEAK ANSWER ALOUD                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  Location: FloatingButtonService.speakAnswer()            │  │
│  │                                                           │  │
│  │  • Use Android TextToSpeech engine                        │  │
│  │  • Speak: "Question 1: Capital of France.                 │  │
│  │            The correct answer is B: Paris."               │  │
│  │  • Also show Toast notification for visual confirmation   │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              16. READY FOR NEXT CAPTURE                          │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │  • Hide loading indicator                                 │  │
│  │  • Reset isProcessing flag                                │  │
│  │  • User can tap floating button again                     │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Data Flow Diagram

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    SCREEN    │     │   BITMAP     │     │    TEXT      │     │   ANSWER     │
│   (Pixels)   │────▶│   (Image)    │────▶│   (String)   │────▶│   (Speech)   │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
       │                    │                    │                    │
       │                    │                    │                    │
       ▼                    ▼                    ▼                    ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ ImageReader  │     │   ML Kit     │     │  Gemini AI   │     │     TTS      │
│ VirtualDisp  │     │    OCR       │     │    API       │     │   Engine     │
└──────────────┘     └──────────────┘     └──────────────┘     └──────────────┘
```

---

## Component Responsibilities

### 1. MainActivity.kt
```
┌─────────────────────────────────────────────────────────────────┐
│                        MAIN ACTIVITY                             │
├─────────────────────────────────────────────────────────────────┤
│  RESPONSIBILITIES:                                               │
│  • Display main UI                                               │
│  • Manage API key input/storage                                  │
│  • Handle permission requests                                    │
│  • Start/Stop foreground services                                │
│  • Show help dialog                                              │
├─────────────────────────────────────────────────────────────────┤
│  KEY METHODS:                                                    │
│  • onCreate() - Initialize UI                                    │
│  • checkAndRequestPermissions() - Handle permissions             │
│  • requestMediaProjection() - Get screen capture permission      │
│  • startFloatingService() - Launch both services                 │
│  • stopFloatingService() - Stop both services                    │
└─────────────────────────────────────────────────────────────────┘
```

### 2. ScreenCaptureService.kt
```
┌─────────────────────────────────────────────────────────────────┐
│                    SCREEN CAPTURE SERVICE                        │
├─────────────────────────────────────────────────────────────────┤
│  RESPONSIBILITIES:                                               │
│  • Hold MediaProjection instance                                 │
│  • Manage VirtualDisplay for screen mirroring                    │
│  • Capture screen as Bitmap on demand                            │
│  • Run as foreground service                                     │
├─────────────────────────────────────────────────────────────────┤
│  KEY METHODS:                                                    │
│  • setupMediaProjection() - Initialize screen capture            │
│  • captureScreen(callback) - Capture and return Bitmap           │
│  • imageToBitmap() - Convert Image to Bitmap                     │
├─────────────────────────────────────────────────────────────────┤
│  SINGLETON ACCESS:                                               │
│  • getInstance() - Get running service instance                  │
└─────────────────────────────────────────────────────────────────┘
```

### 3. FloatingButtonService.kt
```
┌─────────────────────────────────────────────────────────────────┐
│                   FLOATING BUTTON SERVICE                        │
├─────────────────────────────────────────────────────────────────┤
│  RESPONSIBILITIES:                                               │
│  • Display floating button overlay                               │
│  • Handle button tap and drag                                    │
│  • Orchestrate the capture → OCR → AI → TTS workflow             │
│  • Manage TextToSpeech engine                                    │
│  • Show loading states and error messages                        │
├─────────────────────────────────────────────────────────────────┤
│  KEY METHODS:                                                    │
│  • setupFloatingButton() - Create overlay UI                     │
│  • onFloatingButtonClick() - Start capture workflow              │
│  • captureAndProcess() - Capture screenshot                      │
│  • processScreenshot() - Run OCR + AI + TTS                      │
│  • performOCR() - Extract text using ML Kit                      │
│  • speakAnswer() - Speak using TTS                               │
└─────────────────────────────────────────────────────────────────┘
```

### 4. GeminiService.kt
```
┌─────────────────────────────────────────────────────────────────┐
│                       GEMINI SERVICE                             │
├─────────────────────────────────────────────────────────────────┤
│  RESPONSIBILITIES:                                               │
│  • Build prompts for Gemini AI                                   │
│  • Make HTTP requests to Gemini API                              │
│  • Parse JSON responses                                          │
│  • Return formatted answers                                      │
├─────────────────────────────────────────────────────────────────┤
│  KEY METHODS:                                                    │
│  • answerQuestions(text) - Main entry point                      │
│  • buildPrompt() - Create AI prompt                              │
│  • callGeminiAPI() - HTTP POST request                           │
│  • parseResponse() - Extract answer from JSON                    │
├─────────────────────────────────────────────────────────────────┤
│  API ENDPOINT:                                                   │
│  POST /v1beta/models/gemini-1.5-flash:generateContent            │
└─────────────────────────────────────────────────────────────────┘
```

### 5. ApiKeyManager.kt
```
┌─────────────────────────────────────────────────────────────────┐
│                      API KEY MANAGER                             │
├─────────────────────────────────────────────────────────────────┤
│  RESPONSIBILITIES:                                               │
│  • Securely store API key                                        │
│  • Retrieve API key                                              │
│  • Provide default API key                                       │
├─────────────────────────────────────────────────────────────────┤
│  KEY METHODS:                                                    │
│  • saveApiKey() - Store key in encrypted prefs                   │
│  • getApiKey() - Retrieve key (or default)                       │
│  • clearApiKey() - Remove stored key                             │
└─────────────────────────────────────────────────────────────────┘
```

---

## Sequence Diagram

```
User          MainActivity     ScreenCapture    FloatingButton    ML Kit    Gemini    TTS
  │                │           Service          Service             │         │        │
  │  Launch App    │                │                │              │         │        │
  │───────────────▶│                │                │              │         │        │
  │                │                │                │              │         │        │
  │  Tap Start     │                │                │              │         │        │
  │───────────────▶│                │                │              │         │        │
  │                │                │                │              │         │        │
  │                │  Start Service │                │              │         │        │
  │                │───────────────▶│                │              │         │        │
  │                │                │                │              │         │        │
  │                │  Start Service │                │              │         │        │
  │                │────────────────────────────────▶│              │         │        │
  │                │                │                │              │         │        │
  │                │                │                │  Init TTS    │         │        │
  │                │                │                │─────────────────────────────────▶│
  │                │                │                │              │         │        │
  │  Navigate to   │                │                │              │         │        │
  │  Quiz App      │                │                │              │         │        │
  │                │                │                │              │         │        │
  │  Tap Floating  │                │                │              │         │        │
  │  Button        │                │                │              │         │        │
  │────────────────────────────────────────────────▶│              │         │        │
  │                │                │                │              │         │        │
  │                │                │  captureScreen │              │         │        │
  │                │                │◀───────────────│              │         │        │
  │                │                │                │              │         │        │
  │                │                │  Return Bitmap │              │         │        │
  │                │                │───────────────▶│              │         │        │
  │                │                │                │              │         │        │
  │                │                │                │  OCR Image   │         │        │
  │                │                │                │─────────────▶│         │        │
  │                │                │                │              │         │        │
  │                │                │                │  Return Text │         │        │
  │                │                │                │◀─────────────│         │        │
  │                │                │                │              │         │        │
  │                │                │                │  Send Text   │         │        │
  │                │                │                │─────────────────────▶│        │
  │                │                │                │              │         │        │
  │                │                │                │  Return Answer        │        │
  │                │                │                │◀─────────────────────│        │
  │                │                │                │              │         │        │
  │                │                │                │  Speak Answer│         │        │
  │                │                │                │─────────────────────────────────▶│
  │                │                │                │              │         │        │
  │◀─────────────────────────────────────────────────────────────────────────────────────│
  │  Hear Answer   │                │                │              │         │        │
  │                │                │                │              │         │        │
```

---

## Error Handling Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                      ERROR SCENARIOS                             │
└─────────────────────────────────────────────────────────────────┘

1. Screenshot Capture Failed
   └─▶ Show Toast: "Failed to capture screenshot"
   └─▶ Reset loading state
   └─▶ Ready for next attempt

2. No Text Found in Screenshot
   └─▶ Show Toast: "No text found in screenshot"
   └─▶ Reset loading state

3. API Key Not Configured
   └─▶ Show Toast: "API key not configured"
   └─▶ User should enter key in MainActivity

4. Gemini API Error
   └─▶ Show Toast: "Error getting answer: [message]"
   └─▶ Log error for debugging

5. TTS Not Available
   └─▶ Show Toast with answer text instead
   └─▶ User can read the answer

6. Network Error
   └─▶ Show Toast: "Network error"
   └─▶ User should check internet connection
```

---

## State Diagram

```
                    ┌─────────────┐
                    │    IDLE     │
                    │  (Ready)    │
                    └──────┬──────┘
                           │
                    User taps button
                           │
                           ▼
                    ┌─────────────┐
                    │  CAPTURING  │
                    │ (Screenshot)│
                    └──────┬──────┘
                           │
                    Screenshot captured
                           │
                           ▼
                    ┌─────────────┐
                    │ PROCESSING  │
                    │   (OCR)     │
                    └──────┬──────┘
                           │
                    Text extracted
                           │
                           ▼
                    ┌─────────────┐
                    │  QUERYING   │
                    │ (Gemini AI) │
                    └──────┬──────┘
                           │
                    Answer received
                           │
                           ▼
                    ┌─────────────┐
                    │  SPEAKING   │
                    │   (TTS)     │
                    └──────┬──────┘
                           │
                    Speech complete
                           │
                           ▼
                    ┌─────────────┐
                    │    IDLE     │
                    │  (Ready)    │
                    └─────────────┘
```

---

## Threading Model

```
┌─────────────────────────────────────────────────────────────────┐
│                       MAIN THREAD (UI)                           │
│  • FloatingButtonService UI updates                              │
│  • Toast messages                                                │
│  • View visibility changes                                       │
│  • TTS speak calls                                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ Coroutines (Dispatchers.IO)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BACKGROUND THREADS                            │
│  • OCR processing (ML Kit)                                       │
│  • Network requests (Gemini API)                                 │
│  • Image processing                                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## Summary

The app follows a simple but effective pipeline:

1. **Capture** → Take screenshot using MediaProjection API
2. **Extract** → Use ML Kit OCR to get text from image  
3. **Analyze** → Send text to Gemini AI to identify and answer MCQs
4. **Speak** → Use Android TTS to read the answers aloud

All heavy processing happens on background threads, keeping the UI responsive. The floating button overlay allows the app to work over any other app, making it versatile for different quiz/exam scenarios.
