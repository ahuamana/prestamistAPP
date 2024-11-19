package com.paparazziapps.pretamistapp.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

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

        suspend fun <T> safeApiCallV2(apiCall: suspend () -> Response<T>): PAResult<T> {
            return withContext(Dispatchers.IO) {
                try {
                    val response = apiCall()
                    if (response.isSuccessful) {
                        PAResult.Success(response.body()!!)
                    } else {
                        PAResult.Error(Exception("API call failed with response code ${response.code()}"))
                    }
                } catch (e: Exception) {
                    PAResult.Error(e)
                }
            }
        }

        suspend fun <T> safeApiCallWithResult(apiCall: suspend () -> PAResult<T>): PAResult<T> {
            return withContext(Dispatchers.IO) {
                try {
                    apiCall()
                } catch (e: Exception) {
                    PAResult.Error(e)
                }
            }
        }
    }
}