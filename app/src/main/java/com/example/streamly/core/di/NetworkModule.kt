package com.example.streamly.core.di

import com.example.streamly.BuildConfig
import com.example.streamly.core.common.constant.NetworkConstants
import com.example.streamly.core.network.api.ApiService
import com.example.streamly.core.network.json.jsonProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = jsonProvider()

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
        }

        install(HttpTimeout) {
            connectTimeoutMillis = NetworkConstants.CONNECT_TIMEOUT_MILLIS
            socketTimeoutMillis = NetworkConstants.SOCKET_TIMEOUT_MILLIS
        }

        defaultRequest {
            url(NetworkConstants.BASE_URL)
        }
    }

    @Provides
    @Singleton
    fun provideApiService(httpClient: HttpClient): ApiService = ApiService(httpClient)
}