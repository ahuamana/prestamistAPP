package com.paparazziapps.pretamistapp.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class NetworkOperation {

    companion object {
        suspend fun <T> safeApiCall(apiCall: suspend () -> T): PAResult<T> {
            return withContext(Dispatchers.IO) {
                try {
                    PAResult.Success(apiCall.invoke())
                } catch (e: Exception) {
                    PAResult.Error(e)
                }
            }
        }
    }
}