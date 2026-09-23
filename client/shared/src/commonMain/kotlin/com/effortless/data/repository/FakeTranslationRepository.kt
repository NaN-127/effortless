package com.effortless.data.repository

import com.effortless.core.result.Result
import com.effortless.domain.model.Translation
import com.effortless.domain.repository.TranslationRepository
import kotlinx.coroutines.delay

/**
 * In-memory test double allowing full offline UI development and testing.
 */
class FakeTranslationRepository(
    var simulatedDelayMs: Long = 300L,
    var shouldFail: Boolean = false
) : TranslationRepository {

    private val cannedTranslations = mapOf(
        "मुझे कल ऑफिस जाना है" to "I have to go to the office tomorrow.",
        "नमस्ते, आप कैसे हैं?" to "Hello, how are you?",
        "धन्यवाद" to "Thank you",
        "शुभ प्रभात" to "Good morning"
    )

    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<Translation> {
        if (simulatedDelayMs > 0) {
            delay(simulatedDelayMs)
        }

        if (shouldFail) {
            return Result.Failure(
                com.effortless.core.result.AppError.Network("Simulated fake repository failure")
            )
        }

        val translated = cannedTranslations[text]
            ?: "[Translated to $targetLanguage]: $text"

        return Result.Success(
            Translation(
                sourceLanguage = sourceLanguage,
                targetLanguage = targetLanguage,
                originalText = text,
                translatedText = translated,
                latencyMs = simulatedDelayMs
            )
        )
    }
}
