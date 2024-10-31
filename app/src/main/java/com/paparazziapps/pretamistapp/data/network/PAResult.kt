package com.paparazziapps.pretamistapp.data.network

sealed class PAResult<out T> {
    data class Success<out T>(val data: T) : PAResult<T>()
    data class Error(val exception: Exception) : PAResult<Nothing>()
}