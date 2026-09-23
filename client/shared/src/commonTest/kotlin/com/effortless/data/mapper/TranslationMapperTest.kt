package com.effortless.data.mapper

import com.effortless.data.remote.dto.TranslationResponseDto
import kotlin.test.Test
import kotlin.test.assertEquals

class TranslationMapperTest {

    @Test
    fun toDtoCorrectlyMapsFields() {
        val dto = TranslationMapper.toDto(
            text = "नमस्ते",
            sourceLanguage = "hi-IN",
            targetLanguage = "en-IN"
        )

        assertEquals("नमस्ते", dto.input)
        assertEquals("hi-IN", dto.sourceLanguageCode)
        assertEquals("en-IN", dto.targetLanguageCode)
    }

    @Test
    fun toDomainCorrectlyMapsFields() {
        val dto = TranslationResponseDto(
            translatedText = "Hello",
            sourceLanguageCode = "hi-IN",
            targetLanguageCode = "en-IN",
            requestId = "req-123"
        )

        val domain = TranslationMapper.toDomain(dto, originalText = "नमस्ते", latencyMs = 120L)

        assertEquals("नमस्ते", domain.originalText)
        assertEquals("Hello", domain.translatedText)
        assertEquals("hi-IN", domain.sourceLanguage)
        assertEquals("en-IN", domain.targetLanguage)
        assertEquals(120L, domain.latencyMs)
    }
}
