package com.effortless.data.repository

import com.effortless.core.result.Result
import com.effortless.core.result.map
import com.effortless.data.mapper.TranslationMapper
import com.effortless.data.remote.api.TranslationApiService
import com.effortless.data.remote.dto.TranslationResponseDto
import com.effortless.domain.model.Translation
import com.effortless.domain.repository.TranslationRepository

class TranslationRepositoryImpl(
    private val apiService: TranslationApiService
) : TranslationRepository {
    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<Translation> {
        val dto = TranslationMapper.toDto(text, sourceLanguage, targetLanguage)
        return apiService.translate(dto).map { responseDto: TranslationResponseDto ->
            TranslationMapper.toDomain(responseDto, text)
        }
    }
}
