package com.paparazziapps.pretamistapp.data.remote

interface RemoteAnalyticsDataSource {
    fun logEvent(event: String, params: Map<String, String>)
    fun logEvent(event: String)
    fun setUserProperty(name: String, value: String)
    fun setUserId(userId: String)
}