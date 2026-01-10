package com.quizanswerer.app

class QuestionParser {

    fun parseQuestions(text: String): List<Question> {
        val questions = mutableListOf<Question>()
        val lines = text.lines().filter { it.isNotBlank() }
        
        var currentQuestion: String? = null
        val currentOptions = mutableListOf<String>()
        
        for (i in lines.indices) {
            val line = lines[i].trim()
            
            // Check if this line is a question
            if (isQuestion(line)) {
                // Save previous question if exists
                if (currentQuestion != null && currentOptions.isNotEmpty()) {
                    questions.add(Question(currentQuestion, currentOptions.toList()))
                    currentOptions.clear()
                }
                
                currentQuestion = cleanQuestionText(line)
            }
            // Check if this line is an option (A., B., C., D. or a), b), c), d))
            else if (isOption(line)) {
                currentOptions.add(line)
            }
            // If line looks like it could be part of previous question or option
            else if (currentQuestion != null && !line.matches(Regex("^\\d+\\.?.*"))) {
                // Might be continuation of question or option
                if (currentOptions.isEmpty()) {
                    currentQuestion += " $line"
                } else if (currentOptions.isNotEmpty() && !isOption(line)) {
                    // Might be continuation of last option
                    val lastIndex = currentOptions.lastIndex
                    if (lastIndex >= 0) {
                        currentOptions[lastIndex] = currentOptions[lastIndex] + " " + line
                    }
                }
            }
        }
        
        // Add last question
        if (currentQuestion != null && currentOptions.isNotEmpty()) {
            questions.add(Question(currentQuestion, currentOptions.toList()))
        }
        
        return questions
    }

    private fun isQuestion(line: String): Boolean {
        // Check for common question patterns
        return line.endsWith("?") ||
                line.matches(Regex("^\\d+\\.?\\s+.*")) || // Numbered question
                line.matches(Regex("^Question\\s+\\d+.*", RegexOption.IGNORE_CASE)) ||
                line.matches(Regex("^Q\\d+.*", RegexOption.IGNORE_CASE)) ||
                (line.length > 20 && !isOption(line)) // Long enough to be a question
    }

    private fun isOption(line: String): Boolean {
        // Check for option patterns: A., B., C., D. or A), B), C), D) or a., b., c., d.
        return line.matches(Regex("^[A-Da-d][.)\\s].*")) ||
                line.matches(Regex("^\\([A-Da-d]\\).*"))
    }

    private fun cleanQuestionText(text: String): String {
        // Remove question number prefixes like "1.", "Q1:", "Question 1:"
        var cleaned = text.replace(Regex("^\\d+\\.?\\s+"), "")
        cleaned = cleaned.replace(Regex("^Question\\s+\\d+[:.\\s]+", RegexOption.IGNORE_CASE), "")
        cleaned = cleaned.replace(Regex("^Q\\d+[:.\\s]+", RegexOption.IGNORE_CASE), "")
        return cleaned.trim()
    }
}
