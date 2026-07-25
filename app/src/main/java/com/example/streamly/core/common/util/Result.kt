package com.example.streamly.core.common.util

import com.example.streamly.core.common.enum.Status

data class Result<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
) {
    companion object {
        fun <T> success(data: T?): Result<T> = Result(Status.SUCCESS, data, null)

        fun <T> error(message: String, data: T? = null): Result<T> =
            Result(Status.ERROR, data, message)

        fun <T> error(exception: Exception, data: T? = null): Result<T> =
            Result(Status.ERROR, data, exception.message)

        fun <T> loading(data: T? = null): Result<T> = Result(Status.LOADING, data, null)

        fun <T> nothing(): Result<T> = Result(Status.NOTHING, null, null)
    }
}