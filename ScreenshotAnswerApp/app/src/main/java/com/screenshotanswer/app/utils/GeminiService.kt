package com.screenshotanswer.app.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class GeminiService(private val apiKey: String = DEFAULT_API_KEY) {

    companion object {
        private const val TAG = "GeminiService"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
        const val DEFAULT_API_KEY = "AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun answerQuestions(extractedText: String): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(extractedText)
            val response = callGeminiAPI(prompt)
            parseResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API", e)
            "Error getting answer: ${e.message}"
        }
    }

    private fun buildPrompt(text: String): String {
        return """
            You are a helpful assistant that answers multiple choice questions.
            
            I will give you text extracted from a screenshot that contains one or more multiple choice questions.
            
            Your task:
            1. Identify all multiple choice questions in the text
            2. For each question, determine the correct answer
            3. Respond in a clear, spoken format that will be read aloud
            
            Format your response like this:
            "Question 1: [brief summary of question]. The correct answer is [letter]: [answer text]."
            
            If there are multiple questions, answer each one.
            Keep your response concise and natural for text-to-speech.
            If you're not sure about an answer, give your best guess and mention it's uncertain.
            
            Here is the extracted text from the screenshot:
            
            $text
            
            Please provide the answers:
        """.trimIndent()
    }

    private fun callGeminiAPI(prompt: String): String {
        val requestBody = GeminiRequest(
            contents = listOf(
                Content(
                    parts = listOf(Part(text = prompt))
                )
            ),
            generationConfig = GenerationConfig(
                temperature = 0.3f,
                maxOutputTokens = 1024
            )
        )

        val jsonBody = gson.toJson(requestBody)
        Log.d(TAG, "Request body: $jsonBody")

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .header("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        
        Log.d(TAG, "Response code: ${response.code}")
        Log.d(TAG, "Response body: $responseBody")

        if (!response.isSuccessful) {
            throw Exception("API call failed: ${response.code} - $responseBody")
        }

        return responseBody
    }

    private fun parseResponse(jsonResponse: String): String {
        return try {
            val response = gson.fromJson(jsonResponse, GeminiResponse::class.java)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            text ?: "Could not parse response"
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response", e)
            "Error parsing response: ${e.message}"
        }
    }

    // Data classes for API request/response
    data class GeminiRequest(
        val contents: List<Content>,
        val generationConfig: GenerationConfig? = null
    )

    data class Content(
        val parts: List<Part>,
        val role: String? = "user"
    )

    data class Part(
        val text: String
    )

    data class GenerationConfig(
        val temperature: Float = 0.7f,
        val maxOutputTokens: Int = 1024,
        val topP: Float = 0.95f,
        val topK: Int = 40
    )

    data class GeminiResponse(
        val candidates: List<Candidate>?
    )

    data class Candidate(
        val content: ContentResponse?,
        val finishReason: String?
    )

    data class ContentResponse(
        val parts: List<PartResponse>?,
        val role: String?
    )

    data class PartResponse(
        val text: String?
    )
}
