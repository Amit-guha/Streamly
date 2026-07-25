package com.example.streamly.core.network.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

/**
 * Thin, shared wrapper around [HttpClient]. Feature remote data sources inject this instead
 * of the raw [HttpClient] so request/serialization boilerplate isn't duplicated per feature.
 */
class ApiService @Inject constructor(
    @PublishedApi internal val httpClient: HttpClient,
) {
    suspend inline fun <reified T> get(url: String): T = httpClient.get(url).body()

    suspend inline fun <reified T> post(url: String, body: Any? = null): T =
        httpClient.post(url) {
            contentType(ContentType.Application.Json)
            body?.let { setBody(it) }
        }.body()

    suspend inline fun <reified T> put(url: String, body: Any? = null): T =
        httpClient.put(url) {
            contentType(ContentType.Application.Json)
            body?.let { setBody(it) }
        }.body()

    suspend inline fun <reified T> delete(url: String): T = httpClient.delete(url).body()
}