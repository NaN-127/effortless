package com.effortless.domain.usecase

import com.effortless.core.result.AppError
import com.effortless.core.result.Result
import com.effortless.domain.model.Translation
import com.effortless.domain.repository.TranslationRepository

/**
 * Use case orchestrating text translation with input validation.
 */
class TranslateTextUseCase(
    private val translationRepository: TranslationRepository
) {
    suspend operator fun invoke(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<Translation> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return Result.Failure(AppError.Validation("Input text cannot be empty"))
        }

        if (sourceLanguage.equals(targetLanguage, ignoreCase = true)) {
            // Passthrough if source and target languages are identical
            return Result.Success(
                Translation(
                    sourceLanguage = sourceLanguage,
                    targetLanguage = targetLanguage,
                    originalText = trimmed,
                    translatedText = trimmed,
                    latencyMs = 0L
                )
            )
        }

        return translationRepository.translate(
            text = trimmed,
            sourceLanguage = sourceLanguage,
            targetLanguage = targetLanguage
        )
    }
}
