package com.effortless.core.di

import com.effortless.core.network.HttpClientFactory
import com.effortless.data.remote.api.TranslationApiService
import com.effortless.data.remote.api.TranslationApiServiceImpl
import com.effortless.data.repository.FakeTranslationRepository
import com.effortless.data.repository.LanguageRepositoryImpl
import com.effortless.data.repository.TranslationRepositoryImpl
import com.effortless.domain.repository.LanguageRepository
import com.effortless.domain.repository.TranslationRepository
import com.effortless.domain.usecase.GetSupportedLanguagesUseCase
import com.effortless.domain.usecase.TranslateTextUseCase
import com.effortless.presentation.home.HomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { HttpClientFactory.create() }
    single<TranslationApiService> { TranslationApiServiceImpl(get()) }
}

val repositoryModule = module {
    // Uses FakeTranslationRepository for bootstrap & early client UI development
    single<TranslationRepository> { FakeTranslationRepository() }
    single<LanguageRepository> { LanguageRepositoryImpl() }
}

val useCaseModule = module {
    factory { TranslateTextUseCase(get()) }
    factory { GetSupportedLanguagesUseCase(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
}

val appModules: List<Module> = listOf(
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
