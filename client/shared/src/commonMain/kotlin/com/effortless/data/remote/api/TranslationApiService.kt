package com.effortless.data.remote.api

import com.effortless.core.result.AppError
import com.effortless.core.result.Result
import com.effortless.data.remote.dto.TranslationRequestDto
import com.effortless.data.remote.dto.TranslationResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess

interface TranslationApiService {
    suspend fun translate(request: TranslationRequestDto): Result<TranslationResponseDto>
}

class TranslationApiServiceImpl(
    private val httpClient: HttpClient
) : TranslationApiService {
    override suspend fun translate(request: TranslationRequestDto): Result<TranslationResponseDto> {
        return try {
            val response = httpClient.post("/api/v1/translation") {
                setBody(request)
            }

            if (response.status.isSuccess()) {
                Result.Success(response.body<TranslationResponseDto>())
            } else {
                Result.Failure(
                    AppError.Server(
                        statusCode = response.status.value,
                        message = "Backend returned HTTP ${response.status.value}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.Failure(AppError.Network(e.message))
        }
    }
}
