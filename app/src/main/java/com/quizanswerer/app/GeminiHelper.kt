package com.quizanswerer.app

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiHelper {
    private var apiKey: String = ""
    private var model: GenerativeModel? = null

    fun setApiKey(key: String) {
        apiKey = key
        model = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.3f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 1024
            }
        )
    }

    suspend fun answerQuestion(question: Question): String = withContext(Dispatchers.IO) {
        if (model == null) {
            throw IllegalStateException("API key not set")
        }

        try {
            val prompt = buildPrompt(question)
            val response = model!!.generateContent(prompt)
            
            // Extract and clean the answer
            val answer = response.text?.trim() ?: "Unable to determine answer"
            return@withContext cleanAnswer(answer, question.options)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Error: ${e.message}"
        }
    }

    private fun buildPrompt(question: Question): String {
        val prompt = StringBuilder()
        prompt.append("You are an expert at answering multiple-choice questions. ")
        prompt.append("Please provide ONLY the letter (A, B, C, or D) and the complete text of the correct answer.\n\n")
        prompt.append("Question: ${question.questionText}\n\n")
        prompt.append("Options:\n")
        question.options.forEach { option ->
            prompt.append("$option\n")
        }
        prompt.append("\nProvide your answer in the format: 'X. [Complete answer text]' where X is the letter.")
        
        return prompt.toString()
    }

    private fun cleanAnswer(answer: String, options: List<String>): String {
        // Try to extract the option letter and text
        var cleaned = answer.trim()
        
        // If answer contains explanation, try to extract just the answer part
        val lines = cleaned.lines()
        if (lines.isNotEmpty()) {
            // Look for line that starts with A, B, C, or D
            val answerLine = lines.firstOrNull { line ->
                line.trim().matches(Regex("^[A-Da-d][.)\\s].*"))
            }
            if (answerLine != null) {
                cleaned = answerLine.trim()
            } else {
                // Use first line if no clear option found
                cleaned = lines[0].trim()
            }
        }
        
        // Try to match with actual options from the question
        val matchedOption = options.firstOrNull { option ->
            val optionLetter = option.substring(0, 1).uppercase()
            cleaned.startsWith(optionLetter, ignoreCase = true)
        }
        
        return matchedOption ?: cleaned
    }
}
