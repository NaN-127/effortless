package com.effortless.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.effortless.core.result.Result
import com.effortless.domain.model.Language
import com.effortless.domain.model.Translation
import com.effortless.domain.usecase.GetSupportedLanguagesUseCase
import com.effortless.domain.usecase.TranslateTextUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val inputText: String = "मुझे कल ऑफिस जाना है",
    val sourceLanguage: Language = Language.DefaultSource,
    val targetLanguage: Language = Language.DefaultTarget,
    val availableLanguages: List<Language> = Language.DefaultList,
    val isTranslating: Boolean = false,
    val translationResult: Translation? = null,
    val errorMessage: String? = null
)

sealed interface HomeUiEvent {
    data class InputTextChanged(val text: String) : HomeUiEvent
    data class SourceLanguageChanged(val language: Language) : HomeUiEvent
    data class TargetLanguageChanged(val language: Language) : HomeUiEvent
    data object SwapLanguages : HomeUiEvent
    data object TranslateClicked : HomeUiEvent
    data object ClearError : HomeUiEvent
}

class HomeViewModel(
    private val translateTextUseCase: TranslateTextUseCase,
    private val getSupportedLanguagesUseCase: GetSupportedLanguagesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            val result = getSupportedLanguagesUseCase()
            if (result is Result.Success) {
                _uiState.update { it.copy(availableLanguages = result.data) }
            }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.InputTextChanged -> {
                _uiState.update { it.copy(inputText = event.text, errorMessage = null) }
            }
            is HomeUiEvent.SourceLanguageChanged -> {
                _uiState.update { it.copy(sourceLanguage = event.language) }
            }
            is HomeUiEvent.TargetLanguageChanged -> {
                _uiState.update { it.copy(targetLanguage = event.language) }
            }
            is HomeUiEvent.SwapLanguages -> {
                _uiState.update {
                    it.copy(
                        sourceLanguage = it.targetLanguage,
                        targetLanguage = it.sourceLanguage,
                        translationResult = null
                    )
                }
            }
            is HomeUiEvent.TranslateClicked -> {
                translate()
            }
            is HomeUiEvent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun translate() {
        val currentState = _uiState.value
        if (currentState.inputText.isBlank()) return

        _uiState.update { it.copy(isTranslating = true, errorMessage = null) }

        viewModelScope.launch {
            val result = translateTextUseCase(
                text = currentState.inputText,
                sourceLanguage = currentState.sourceLanguage.code,
                targetLanguage = currentState.targetLanguage.code
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isTranslating = false,
                            translationResult = result.data,
                            errorMessage = null
                        )
                    }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            isTranslating = false,
                            errorMessage = "Translation failed: ${result.error}"
                        )
                    }
                }
            }
        }
    }
}
