package com.effortless.core.platform

import kotlinx.coroutines.flow.Flow

/**
 * Recording state representation for speech capture.
 */
sealed interface RecordingState {
    data object Idle : RecordingState
    data object Recording : RecordingState
    data class Error(val message: String) : RecordingState
}

/**
 * Platform audio capture abstraction.
 * Concrete implementations will be implemented per platform in later phases:
 * - Android: AudioRecord
 * - iOS: AVAudioEngine
 * - Desktop: Java Sound API
 */
interface AudioRecorder {
    val state: Flow<RecordingState>
    suspend fun startRecording()
    suspend fun stopRecording(): ByteArray?
    fun cancel()
}
