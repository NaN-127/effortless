package com.effortless.core.platform

/**
 * Text insertion and keyboard interaction abstraction.
 * Isolated from presentation and platform APIs:
 * - Android: InputConnection.commitText()
 * - iOS: textDocumentProxy.insertText()
 * - Desktop: Robot or OS-level input integration
 */
interface KeyboardInput {
    fun insertText(text: String)
    fun deleteBackward()
    fun insertNewline()
}
