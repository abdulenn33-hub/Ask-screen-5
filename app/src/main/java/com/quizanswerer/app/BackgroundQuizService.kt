package com.quizanswerer.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class BackgroundQuizService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var tts: TextToSpeech? = null
    private lateinit var geminiHelper: GeminiHelper
    private lateinit var questionParser: QuestionParser
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var isProcessing = false
    private var mediaProjectionIntent: Intent? = null
    private var mediaProjectionResultCode: Int = 0

    companion object {
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "background_quiz_service"
        const val ACTION_CAPTURE = "com.quizanswerer.app.CAPTURE"
        const val ACTION_STOP = "com.quizanswerer.app.STOP"
        const val EXTRA_RESULT_CODE = "resultCode"
        const val EXTRA_DATA = "data"
    }

    override fun onCreate() {
        super.onCreate()
        
        geminiHelper = GeminiHelper()
        questionParser = QuestionParser()
        
        // Load API key from preferences
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val apiKey = prefs.getString("gemini_api_key", "AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ") 
            ?: "AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ"
        geminiHelper.setApiKey(apiKey)
        
        initializeTTS()
        createNotificationChannel()
    }

    private fun initializeTTS() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.9f)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CAPTURE -> {
                if (!isProcessing) {
                    captureAndProcess()
                }
            }
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                // Save media projection info if provided
                mediaProjectionResultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, -1) ?: -1
                mediaProjectionIntent = intent?.getParcelableExtra(EXTRA_DATA)
                
                startForeground(NOTIFICATION_ID, createNotification("Ready", "Tap floating button to capture"))
                showFloatingButton()
            }
        }
        
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Background Quiz Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Runs quiz answering in background"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, text: String): Notification {
        val captureIntent = Intent(this, BackgroundQuizService::class.java).apply {
            action = ACTION_CAPTURE
        }
        val capturePendingIntent = PendingIntent.getService(
            this, 0, captureIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, BackgroundQuizService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_camera, "Capture", capturePendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .build()
    }

    private fun updateNotification(title: String, text: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(title, text))
    }

    private fun showFloatingButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.canDrawOverlays(this)) {
                // Cannot show overlay, just use notification
                return
            }
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = inflater.inflate(R.layout.floating_button, null)

        floatingView?.let { view ->
            val captureButton = view.findViewById<ImageView>(R.id.floatingCaptureButton)
            
            // Make draggable
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f

            captureButton.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = layoutParams.x
                        initialY = layoutParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        layoutParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        layoutParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(floatingView, layoutParams)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val diffX = Math.abs(event.rawX - initialTouchX)
                        val diffY = Math.abs(event.rawY - initialTouchY)
                        if (diffX < 10 && diffY < 10) {
                            // It's a click
                            captureAndProcess()
                        }
                        true
                    }
                    else -> false
                }
            }

            windowManager?.addView(floatingView, layoutParams)
        }
    }

    private fun captureAndProcess() {
        if (isProcessing) {
            updateNotification("Busy", "Already processing a screenshot...")
            return
        }

        isProcessing = true
        updateNotification("Capturing", "Taking screenshot...")

        // Start screenshot service
        if (mediaProjectionIntent != null && mediaProjectionResultCode != -1) {
            val serviceIntent = Intent(this, ScreenshotService::class.java).apply {
                putExtra("resultCode", mediaProjectionResultCode)
                putExtra("data", mediaProjectionIntent)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

            // Wait for screenshot to be captured
            serviceScope.launch {
                delay(2500) // Wait for screenshot
                processLatestScreenshot()
            }
        } else {
            updateNotification("Error", "No screen capture permission")
            isProcessing = false
        }
    }

    private fun processLatestScreenshot() {
        serviceScope.launch {
            try {
                val screenshotPath = getExternalFilesDir(null)?.absolutePath + "/screenshot.png"
                val file = File(screenshotPath)
                
                if (!file.exists()) {
                    updateNotification("Error", "Screenshot not found")
                    isProcessing = false
                    return@launch
                }

                updateNotification("Processing", "Extracting text with OCR...")
                val bitmap = BitmapFactory.decodeFile(screenshotPath)
                
                // Step 1: OCR
                val text = extractText(bitmap)
                
                if (text.isEmpty()) {
                    updateNotification("Error", "No text found in image")
                    isProcessing = false
                    return@launch
                }

                // Step 2: Parse questions
                updateNotification("Processing", "Finding questions...")
                val questions = questionParser.parseQuestions(text)
                
                if (questions.isEmpty()) {
                    updateNotification("Error", "No questions found")
                    isProcessing = false
                    return@launch
                }

                updateNotification("Processing", "Getting answers from AI...")
                
                // Step 3: Answer questions
                val results = StringBuilder()
                for ((index, question) in questions.withIndex()) {
                    val answer = geminiHelper.answerQuestion(question)
                    results.append("Q${index + 1}: $answer\n")
                    
                    // Speak answer
                    val textToSpeak = "${question.questionText}. The answer is: $answer"
                    tts?.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, null)
                    
                    delay(500) // Small delay between questions
                }

                // Show results
                showResultNotification(questions.size, results.toString())
                
                delay(3000)
                updateNotification("Ready", "Tap to capture another screenshot")
                isProcessing = false

            } catch (e: Exception) {
                e.printStackTrace()
                updateNotification("Error", e.message ?: "Unknown error")
                isProcessing = false
            }
        }
    }

    private suspend fun extractText(bitmap: Bitmap): String {
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    continuation.resumeWith(Result.success(visionText.text))
                }
                .addOnFailureListener { e ->
                    continuation.resumeWith(Result.success(""))
                }
        }
    }

    private fun showResultNotification(questionCount: Int, results: String) {
        val resultChannel = "quiz_results"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                resultChannel,
                "Quiz Results",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, resultChannel)
            .setContentTitle("Found $questionCount question(s)")
            .setContentText("Answers ready - check your speaker")
            .setStyle(NotificationCompat.BigTextStyle().bigText(results))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun hideFloatingButton() {
        floatingView?.let {
            windowManager?.removeView(it)
            floatingView = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideFloatingButton()
        tts?.stop()
        tts?.shutdown()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
