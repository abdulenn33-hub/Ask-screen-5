package com.screenshotanswer.app.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.screenshotanswer.app.MainActivity
import com.screenshotanswer.app.R
import com.screenshotanswer.app.utils.ApiKeyManager
import com.screenshotanswer.app.utils.GeminiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class FloatingButtonService : Service(), TextToSpeech.OnInitListener {

    companion object {
        const val CHANNEL_ID = "floating_button_channel"
        const val NOTIFICATION_ID = 1002
        private const val TAG = "FloatingButtonService"
    }

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var floatingButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var params: WindowManager.LayoutParams
    
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())
    
    private var isProcessing = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        setupFloatingButton()
        initTextToSpeech()
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    private fun setupFloatingButton() {
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_button_layout, null)
        floatingButton = floatingView.findViewById(R.id.floating_button)
        progressBar = floatingView.findViewById(R.id.progress_bar)
        
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 300
        }
        
        windowManager.addView(floatingView, params)
        
        setupTouchListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isClick = true
        
        floatingButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isClick = true
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()
                    
                    if (kotlin.math.abs(deltaX) > 10 || kotlin.math.abs(deltaY) > 10) {
                        isClick = false
                    }
                    
                    params.x = initialX + deltaX
                    params.y = initialY + deltaY
                    windowManager.updateViewLayout(floatingView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick && !isProcessing) {
                        onFloatingButtonClick()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun onFloatingButtonClick() {
        if (isProcessing) {
            Toast.makeText(this, "Still processing...", Toast.LENGTH_SHORT).show()
            return
        }
        
        isProcessing = true
        showLoading(true)
        
        // Hide the floating button temporarily to capture clean screenshot
        floatingView.visibility = View.INVISIBLE
        
        handler.postDelayed({
            captureAndProcess()
        }, 300)
    }

    private fun captureAndProcess() {
        val captureService = ScreenCaptureService.getInstance()
        if (captureService == null) {
            showError("Screen capture service not running")
            floatingView.visibility = View.VISIBLE
            showLoading(false)
            isProcessing = false
            return
        }
        
        captureService.captureScreen { bitmap ->
            floatingView.visibility = View.VISIBLE
            
            if (bitmap != null) {
                processScreenshot(bitmap)
            } else {
                showError("Failed to capture screenshot")
                showLoading(false)
                isProcessing = false
            }
        }
    }

    private fun processScreenshot(bitmap: Bitmap) {
        serviceScope.launch {
            try {
                // Step 1: OCR - Extract text from screenshot
                val extractedText = performOCR(bitmap)
                
                if (extractedText.isBlank()) {
                    showError("No text found in screenshot")
                    showLoading(false)
                    isProcessing = false
                    return@launch
                }
                
                Log.d(TAG, "Extracted text: $extractedText")
                
                // Step 2: Send to Gemini to get answers
                val apiKey = ApiKeyManager.getApiKey(this@FloatingButtonService)
                if (apiKey.isEmpty()) {
                    showError("API key not configured")
                    showLoading(false)
                    isProcessing = false
                    return@launch
                }
                
                val geminiService = GeminiService(apiKey)
                val response = geminiService.answerQuestions(extractedText)
                
                if (response.isNotBlank()) {
                    Log.d(TAG, "Gemini response: $response")
                    
                    // Step 3: Speak the answer
                    speakAnswer(response)
                } else {
                    showError("Could not get answer from AI")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing screenshot", e)
                showError("Error: ${e.message}")
            } finally {
                showLoading(false)
                isProcessing = false
            }
        }
    }

    private suspend fun performOCR(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            
            var result = ""
            val task = recognizer.process(image)
            
            // Wait for the task to complete
            while (!task.isComplete) {
                Thread.sleep(50)
            }
            
            if (task.isSuccessful) {
                result = task.result?.text ?: ""
            }
            
            recognizer.close()
            result
        } catch (e: Exception) {
            Log.e(TAG, "OCR Error", e)
            ""
        }
    }

    private fun speakAnswer(answer: String) {
        if (!isTtsReady) {
            Toast.makeText(this, answer, Toast.LENGTH_LONG).show()
            showToastLong("Answer: $answer")
            return
        }
        
        // Also show as toast for visual confirmation
        showToastLong("Speaking answer...")
        
        textToSpeech?.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "answer_${System.currentTimeMillis()}")
    }

    private fun showToastLong(message: String) {
        handler.post {
            Toast.makeText(this@FloatingButtonService, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showError(message: String) {
        handler.post {
            Toast.makeText(this@FloatingButtonService, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(show: Boolean) {
        handler.post {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            floatingButton.alpha = if (show) 0.5f else 1.0f
        }
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.US)
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && 
                         result != TextToSpeech.LANG_NOT_SUPPORTED
            
            if (isTtsReady) {
                textToSpeech?.setSpeechRate(1.0f)
                textToSpeech?.setPitch(1.0f)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Floating Button",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Floating button service notification"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Screenshot Answer Active")
            .setContentText("Tap the floating button to capture and answer questions")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        
        try {
            windowManager.removeView(floatingView)
        } catch (e: Exception) {
            Log.e(TAG, "Error removing floating view", e)
        }
    }
}
