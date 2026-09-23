package com.effortless.domain.repository

import com.effortless.core.result.Result
import com.effortless.domain.model.Translation

/**
 * Pure domain contract for translation operations.
 * Decoupled from HTTP clients and serialization formats.
 */
interface TranslationRepository {
    suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<Translation>
}
