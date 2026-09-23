package com.effortless.domain.repository

import com.effortless.core.result.Result
import com.effortless.domain.model.Language

/**
 * Pure domain contract for language retrieval.
 */
interface LanguageRepository {
    suspend fun getSupportedLanguages(): Result<List<Language>>
}
