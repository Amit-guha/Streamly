package com.example.streamly.feature.home.data.datasource.remote

import com.example.streamly.feature.home.data.dto.VideoDto
import com.example.streamly.feature.home.di.HomeMockHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class HomeRemoteDataSource @Inject constructor(
    @param:HomeMockHttpClient private val httpClient: HttpClient,
) {
    suspend fun getVideos(): List<VideoDto> = httpClient.get("videos").body()
}