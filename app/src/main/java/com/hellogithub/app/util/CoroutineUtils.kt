package com.hellogithub.app.util

import kotlinx.coroutines.CancellationException

/**
 * Like [runCatching] but does NOT catch [CancellationException].
 * Cancellation must always propagate in coroutines — catching it
 * causes stale coroutines to keep running and race with new ones,
 * leading to crashes.
 */
suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
