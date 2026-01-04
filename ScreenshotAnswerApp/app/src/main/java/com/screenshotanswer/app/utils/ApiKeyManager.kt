package com.screenshotanswer.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object ApiKeyManager {
    private const val PREFS_NAME = "screenshot_answer_prefs"
    private const val KEY_API_KEY = "gemini_api_key"
    private const val DEFAULT_API_KEY = "AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ"

    private fun getPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular SharedPreferences if encryption fails
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveApiKey(context: Context, apiKey: String) {
        getPrefs(context).edit().putString(KEY_API_KEY, apiKey).apply()
    }

    fun getApiKey(context: Context): String {
        val savedKey = getPrefs(context).getString(KEY_API_KEY, "") ?: ""
        return if (savedKey.isNotEmpty()) savedKey else DEFAULT_API_KEY
    }
    
    fun getDefaultApiKey(): String = DEFAULT_API_KEY

    fun clearApiKey(context: Context) {
        getPrefs(context).edit().remove(KEY_API_KEY).apply()
    }
}
