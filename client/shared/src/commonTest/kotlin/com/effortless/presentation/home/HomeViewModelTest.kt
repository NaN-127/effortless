package com.effortless.presentation.home

import com.effortless.data.repository.FakeTranslationRepository
import com.effortless.data.repository.LanguageRepositoryImpl
import com.effortless.domain.model.Language
import com.effortless.domain.usecase.GetSupportedLanguagesUseCase
import com.effortless.domain.usecase.TranslateTextUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepo = FakeTranslationRepository(simulatedDelayMs = 0L)
    private val translateUseCase = TranslateTextUseCase(fakeRepo)
    private val languagesUseCase = GetSupportedLanguagesUseCase(LanguageRepositoryImpl())

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateLoadsDefaultLanguages() = runTest {
        val viewModel = HomeViewModel(translateUseCase, languagesUseCase)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("मुझे कल ऑफिस जाना है", state.inputText)
        assertEquals(Language.DefaultSource, state.sourceLanguage)
        assertEquals(Language.DefaultTarget, state.targetLanguage)
        assertEquals(Language.DefaultList.size, state.availableLanguages.size)
    }

    @Test
    fun swapLanguagesUpdatesSourceAndTarget() = runTest {
        val viewModel = HomeViewModel(translateUseCase, languagesUseCase)
        advanceUntilIdle()

        viewModel.onEvent(HomeUiEvent.SwapLanguages)

        val state = viewModel.uiState.value
        assertEquals(Language.DefaultTarget, state.sourceLanguage)
        assertEquals(Language.DefaultSource, state.targetLanguage)
    }

    @Test
    fun translateClickedExecutesTranslationAndUpdateState() = runTest {
        val viewModel = HomeViewModel(translateUseCase, languagesUseCase)
        advanceUntilIdle()

        viewModel.onEvent(HomeUiEvent.TranslateClicked)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isTranslating)
        assertNotNull(state.translationResult)
        assertEquals("I have to go to the office tomorrow.", state.translationResult?.translatedText)
    }
}
