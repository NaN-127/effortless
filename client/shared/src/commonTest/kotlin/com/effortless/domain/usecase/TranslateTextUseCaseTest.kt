package com.effortless.domain.usecase

import com.effortless.core.result.AppError
import com.effortless.core.result.Result
import com.effortless.data.repository.FakeTranslationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TranslateTextUseCaseTest {

    private val fakeRepository = FakeTranslationRepository(simulatedDelayMs = 0L)
    private val useCase = TranslateTextUseCase(fakeRepository)

    @Test
    fun emptyInputReturnsValidationFailure() = runTest {
        val result = useCase(
            text = "   ",
            sourceLanguage = "hi-IN",
            targetLanguage = "en-IN"
        )

        assertTrue(result is Result.Failure)
        assertTrue((result as Result.Failure).error is AppError.Validation)
    }

    @Test
    fun identicalSourceAndTargetReturnsPassthrough() = runTest {
        val input = "Hello world"
        val result = useCase(
            text = input,
            sourceLanguage = "en-IN",
            targetLanguage = "en-IN"
        )

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(input, data.translatedText)
        assertEquals(0L, data.latencyMs)
    }

    @Test
    fun validTranslationDelegatesToRepository() = runTest {
        val input = "मुझे कल ऑफिस जाना है"
        val result = useCase(
            text = input,
            sourceLanguage = "hi-IN",
            targetLanguage = "en-IN"
        )

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals("I have to go to the office tomorrow.", data.translatedText)
    }
}
