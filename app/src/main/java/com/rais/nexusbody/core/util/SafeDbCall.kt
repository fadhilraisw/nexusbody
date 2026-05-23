package com.rais.nexusbody.core.util

import android.database.SQLException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> safeDbCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    dbCall: suspend () -> T
): Result<T> = withContext(dispatcher) {
    try {
        Result.Success(dbCall.invoke())
    } catch (e: SQLException) {
        Result.Error(e, "Database transaction failed.")
    } catch (e: Exception) {
        Result.Error(e, "An unexpected error occurred.")
    }
}