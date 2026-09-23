package com.effortless.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request payload matching FastAPI backend POST /api/v1/translation.
 */
@Serializable
data class TranslationRequestDto(
    @SerialName("input")
    val input: String,

    @SerialName("source_language_code")
    val sourceLanguageCode: String,

    @SerialName("target_language_code")
    val targetLanguageCode: String
)

/**
 * Response payload matching FastAPI backend POST /api/v1/translation.
 */
@Serializable
data class TranslationResponseDto(
    @SerialName("translated_text")
    val translatedText: String,

    @SerialName("source_language_code")
    val sourceLanguageCode: String,

    @SerialName("target_language_code")
    val targetLanguageCode: String,

    @SerialName("request_id")
    val requestId: String? = null
)

/**
 * Standard error response structure from backend.
 */
@Serializable
data class ErrorResponseDto(
    @SerialName("error")
    val error: ErrorDetailDto
)

@Serializable
data class ErrorDetailDto(
    @SerialName("code")
    val code: String,

    @SerialName("message")
    val message: String
)
