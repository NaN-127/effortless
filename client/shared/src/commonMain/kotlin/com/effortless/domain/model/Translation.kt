package com.effortless.domain.model

/**
 * Domain entity representing a completed text translation.
 */
data class Translation(
    val sourceLanguage: String,
    val targetLanguage: String,
    val originalText: String,
    val translatedText: String,
    val latencyMs: Long? = null
)
