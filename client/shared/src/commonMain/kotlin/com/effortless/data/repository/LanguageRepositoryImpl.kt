package com.effortless.data.repository

import com.effortless.core.result.Result
import com.effortless.domain.model.Language
import com.effortless.domain.repository.LanguageRepository

class LanguageRepositoryImpl : LanguageRepository {
    override suspend fun getSupportedLanguages(): Result<List<Language>> {
        return Result.Success(Language.DefaultList)
    }
}
