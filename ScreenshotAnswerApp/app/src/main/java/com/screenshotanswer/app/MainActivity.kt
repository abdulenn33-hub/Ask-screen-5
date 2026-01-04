package com.screenshotanswer.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.screenshotanswer.app.databinding.ActivityMainBinding
import com.screenshotanswer.app.services.FloatingButtonService
import com.screenshotanswer.app.services.ScreenCaptureService
import com.screenshotanswer.app.utils.ApiKeyManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var resultCode: Int = 0
    private var resultData: Intent? = null

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Settings.canDrawOverlays(this)) {
            checkAndRequestPermissions()
        } else {
            Toast.makeText(this, "Overlay permission is required", Toast.LENGTH_LONG).show()
        }
    }

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            resultCode = result.resultCode
            resultData = result.data
            startFloatingService()
        } else {
            Toast.makeText(this, "Screen capture permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requestMediaProjection()
        } else {
            Toast.makeText(this, "Notification permission is required for the service", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        setupUI()
        loadSavedApiKey()
    }

    private fun setupUI() {
        binding.btnStart.setOnClickListener {
            val apiKey = binding.etApiKey.text.toString().trim()
            if (apiKey.isEmpty()) {
                Toast.makeText(this, "Please enter your Gemini API key", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Save the API key
            ApiKeyManager.saveApiKey(this, apiKey)
            
            checkAndRequestPermissions()
        }

        binding.btnStop.setOnClickListener {
            stopFloatingService()
        }

        binding.btnHelp.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun loadSavedApiKey() {
        val savedKey = ApiKeyManager.getApiKey(this)
        if (savedKey.isNotEmpty()) {
            binding.etApiKey.setText(savedKey)
        }
    }

    private fun checkAndRequestPermissions() {
        // Check overlay permission first
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
            return
        }

        // Check notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        requestMediaProjection()
    }

    private fun requestMediaProjection() {
        val intent = mediaProjectionManager.createScreenCaptureIntent()
        mediaProjectionLauncher.launch(intent)
    }

    private fun startFloatingService() {
        resultData?.let { data ->
            // Start the screen capture service first
            val captureIntent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, data)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(captureIntent)
            } else {
                startService(captureIntent)
            }

            // Start the floating button service
            val floatingIntent = Intent(this, FloatingButtonService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(floatingIntent)
            } else {
                startService(floatingIntent)
            }

            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true
            binding.etApiKey.isEnabled = false

            Toast.makeText(this, "Service started! Use the floating button to capture screenshots.", Toast.LENGTH_LONG).show()
            
            // Minimize the app
            moveTaskToBack(true)
        }
    }

    private fun stopFloatingService() {
        stopService(Intent(this, FloatingButtonService::class.java))
        stopService(Intent(this, ScreenCaptureService::class.java))
        
        binding.btnStart.isEnabled = true
        binding.btnStop.isEnabled = false
        binding.etApiKey.isEnabled = true

        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show()
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this)
            .setTitle("How to Use")
            .setMessage("""
                1. Get a Gemini API key from Google AI Studio (https://makersuite.google.com/app/apikey)
                
                2. Enter your API key in the text field
                
                3. Tap "Start Service" and grant all permissions
                
                4. A floating button will appear on your screen
                
                5. Navigate to any app with multiple choice questions
                
                6. Tap the floating button to capture the screen
                
                7. The app will:
                   • Extract text from the screenshot (OCR)
                   • Send questions to Gemini AI
                   • Speak the correct answers aloud
                
                Tips:
                • Make sure questions are clearly visible on screen
                • Works best with clearly formatted MCQ questions
                • You can drag the floating button to move it
            """.trimIndent())
            .setPositiveButton("Got it!", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up services if activity is destroyed
    }
}
