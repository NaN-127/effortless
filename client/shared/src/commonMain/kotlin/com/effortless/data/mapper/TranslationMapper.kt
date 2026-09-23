package com.effortless.data.mapper

import com.effortless.data.remote.dto.TranslationRequestDto
import com.effortless.data.remote.dto.TranslationResponseDto
import com.effortless.domain.model.Translation

object TranslationMapper {
    fun toDto(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): TranslationRequestDto {
        return TranslationRequestDto(
            input = text,
            sourceLanguageCode = sourceLanguage,
            targetLanguageCode = targetLanguage
        )
    }

    fun toDomain(
        dto: TranslationResponseDto,
        originalText: String,
        latencyMs: Long? = null
    ): Translation {
        return Translation(
            sourceLanguage = dto.sourceLanguageCode,
            targetLanguage = dto.targetLanguageCode,
            originalText = originalText,
            translatedText = dto.translatedText,
            latencyMs = latencyMs
        )
    }
}
