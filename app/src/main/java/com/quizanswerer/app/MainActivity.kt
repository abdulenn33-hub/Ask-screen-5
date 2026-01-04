package com.quizanswerer.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.quizanswerer.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var geminiHelper: GeminiHelper
    private lateinit var questionParser: QuestionParser
    private var tts: TextToSpeech? = null
    private var isProcessing = false

    private val screenshotLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                startScreenshotService(data)
            }
        }
    }

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { processImageUri(it) }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (!allGranted) {
            Toast.makeText(this, R.string.error_permission, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        geminiHelper = GeminiHelper()
        questionParser = QuestionParser()
        
        initializeTTS()
        loadApiKey()
        setupClickListeners()
        requestPermissions()
    }

    private fun initializeTTS() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.9f)
            }
        }
    }

    private fun loadApiKey() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        // Default API key - pre-configured
        val defaultApiKey = "AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ"
        val apiKey = prefs.getString("gemini_api_key", defaultApiKey) ?: defaultApiKey
        
        // If no saved key, save the default one
        if (prefs.getString("gemini_api_key", null) == null) {
            prefs.edit().putString("gemini_api_key", defaultApiKey).apply()
        }
        
        binding.etApiKey.setText(apiKey)
        if (apiKey.isNotEmpty()) {
            geminiHelper.setApiKey(apiKey)
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveApiKey.setOnClickListener {
            val apiKey = binding.etApiKey.text.toString().trim()
            if (apiKey.isNotEmpty()) {
                saveApiKey(apiKey)
                geminiHelper.setApiKey(apiKey)
                Toast.makeText(this, R.string.api_key_saved, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnTakeScreenshot.setOnClickListener {
            if (binding.etApiKey.text.toString().trim().isEmpty()) {
                Toast.makeText(this, R.string.api_key_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            requestScreenshotPermission()
        }

        binding.btnSelectImage.setOnClickListener {
            if (binding.etApiKey.text.toString().trim().isEmpty()) {
                Toast.makeText(this, R.string.api_key_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            imagePicker.launch("image/*")
        }
    }

    private fun saveApiKey(apiKey: String) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putString("gemini_api_key", apiKey).apply()
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun requestScreenshotPermission() {
        val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        screenshotLauncher.launch(projectionManager.createScreenCaptureIntent())
    }

    private fun startScreenshotService(data: Intent) {
        val serviceIntent = Intent(this, ScreenshotService::class.java).apply {
            putExtra("resultCode", Activity.RESULT_OK)
            putExtra("data", data)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // Wait a moment for the service to capture the screenshot
        binding.root.postDelayed({
            // The service will save the screenshot and we'll get it via broadcast or shared path
            // For simplicity, we'll use a shared file path
            checkForScreenshot()
        }, 2000)
    }

    private fun checkForScreenshot() {
        val screenshotPath = getExternalFilesDir(null)?.absolutePath + "/screenshot.png"
        val file = java.io.File(screenshotPath)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(screenshotPath)
            processBitmap(bitmap)
        }
    }

    private fun processImageUri(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            binding.ivPreview.setImageBitmap(bitmap)
            processBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processBitmap(bitmap: Bitmap) {
        if (isProcessing) return
        
        isProcessing = true
        binding.tvStatus.text = getString(R.string.processing)
        binding.ivPreview.setImageBitmap(bitmap)

        // Step 1: OCR
        extractTextFromImage(bitmap)
    }

    private fun extractTextFromImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extractedText = visionText.text
                if (extractedText.isEmpty()) {
                    binding.tvStatus.text = getString(R.string.error_ocr)
                    binding.tvResults.text = "No text found in image"
                    isProcessing = false
                    return@addOnSuccessListener
                }

                // Step 2: Parse questions
                parseAndAnswerQuestions(extractedText)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                binding.tvStatus.text = getString(R.string.error_ocr)
                binding.tvResults.text = "OCR Error: ${e.message}"
                isProcessing = false
            }
    }

    private fun parseAndAnswerQuestions(text: String) {
        lifecycleScope.launch {
            try {
                // Parse questions from text
                val questions = questionParser.parseQuestions(text)
                
                if (questions.isEmpty()) {
                    binding.tvStatus.text = "No questions found"
                    binding.tvResults.text = "Extracted text:\n\n$text\n\nNo multiple-choice questions detected."
                    isProcessing = false
                    return@launch
                }

                // Step 3: Get answers from Gemini
                val results = StringBuilder()
                results.append("Found ${questions.size} question(s):\n\n")

                for ((index, question) in questions.withIndex()) {
                    results.append("Question ${index + 1}:\n")
                    results.append("${question.questionText}\n")
                    question.options.forEach { option ->
                        results.append("  $option\n")
                    }
                    results.append("\n")

                    // Get answer from Gemini
                    val answer = geminiHelper.answerQuestion(question)
                    results.append("Answer: $answer\n")
                    results.append("-".repeat(50))
                    results.append("\n\n")

                    // Speak the question and answer
                    speakQuestionAndAnswer(question, answer)
                    
                    // Small delay between questions for better speech
                    kotlinx.coroutines.delay(1000)
                }

                binding.tvResults.text = results.toString()
                binding.tvStatus.text = "Completed!"
                isProcessing = false

            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvStatus.text = getString(R.string.error_gemini)
                binding.tvResults.text = "Error: ${e.message}"
                isProcessing = false
            }
        }
    }

    private fun speakQuestionAndAnswer(question: Question, answer: String) {
        val textToSpeak = "${question.questionText}. The correct answer is: $answer"
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
        
        // Stop screenshot service if running
        stopService(Intent(this, ScreenshotService::class.java))
    }
}
