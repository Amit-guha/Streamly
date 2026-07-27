package com.example.streamly.feature.shorts.data.datasource.remote

import com.example.streamly.feature.shorts.data.dto.ShortDto
import com.example.streamly.feature.shorts.di.ShortsMockHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class ShortsRemoteDataSource @Inject constructor(
    @param:ShortsMockHttpClient private val httpClient: HttpClient,
) {
    suspend fun getShorts(): List<ShortDto> = httpClient.get("shorts").body()
}