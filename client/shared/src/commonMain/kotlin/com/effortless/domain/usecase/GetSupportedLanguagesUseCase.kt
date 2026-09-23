package com.effortless.domain.usecase

import com.effortless.core.result.Result
import com.effortless.domain.model.Language
import com.effortless.domain.repository.LanguageRepository

/**
 * Use case to retrieve the list of available languages.
 */
class GetSupportedLanguagesUseCase(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(): Result<List<Language>> {
        return languageRepository.getSupportedLanguages()
    }
}
