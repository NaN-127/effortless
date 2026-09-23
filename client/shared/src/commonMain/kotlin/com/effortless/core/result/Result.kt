package com.effortless.core.result

/**
 * Domain-level error hierarchy decoupled from network/HTTP libraries.
 */
sealed interface AppError {
    data class Network(val message: String? = null) : AppError
    data object Timeout : AppError
    data object Unauthorized : AppError
    data class Server(val statusCode: Int, val message: String? = null) : AppError
    data class UnsupportedLanguagePair(val source: String, val target: String) : AppError
    data class Validation(val message: String) : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}

/**
 * Functional Result type for domain and application layers.
 */
sealed interface Result<out T> {
    data class Success<out T>(val data: T) : Result<T>
    data class Failure(val error: AppError) : Result<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure
}

fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Failure -> null
}

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Failure -> this
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onFailure(action: (AppError) -> Unit): Result<T> {
    if (this is Result.Failure) action(error)
    return this
}
