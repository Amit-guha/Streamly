package com.example.streamly.feature.home.di

import android.content.Context
import com.example.streamly.core.common.constant.AppConstants
import com.example.streamly.core.network.json.jsonProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeNetworkModule {

    @Provides
    @Singleton
    @HomeMockHttpClient
    fun provideHomeMockHttpClient(@ApplicationContext context: Context): HttpClient {
        val videosJson = context.assets.open(AppConstants.VIDEOS_ASSET_FILE_NAME)
            .bufferedReader()
            .use { it.readText() }

        val mockEngine = MockEngine { _ ->
            respond(
                content = videosJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(jsonProvider())
            }
        }
    }
}