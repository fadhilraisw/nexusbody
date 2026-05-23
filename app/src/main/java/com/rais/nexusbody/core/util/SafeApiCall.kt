package com.rais.nexusbody.core.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import android.database.SQLException

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): Result<T> = withContext(dispatcher) {
    try {
        Result.Success(apiCall.invoke())
    } catch (throwable: Throwable) {
        when (throwable) {
            is IOException -> Result.Error(throwable, "network error. check your connection.")
            is HttpException -> Result.Error(throwable, "server error: ${throwable.code()}")
            is SQLException -> Result.Error(throwable, "database operation failed.")
            else -> Result.Error(throwable, "an unexpected error occurred.")
        }
    }
}