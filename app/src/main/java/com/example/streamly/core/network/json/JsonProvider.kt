package com.example.streamly.core.network.json

import kotlinx.serialization.json.Json

fun jsonProvider(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}