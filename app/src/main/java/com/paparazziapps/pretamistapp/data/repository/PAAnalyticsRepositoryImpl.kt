package com.paparazziapps.pretamistapp.data.repository

import com.paparazziapps.pretamistapp.data.remote.RemoteAnalyticsDataSource

class PAAnalyticsRepositoryImpl(
    private val remoteAnalyticsDataSource: RemoteAnalyticsDataSource
) : PAAnalyticsRepository {

    override fun logEvent(event: String, params: Map<String, String>) {
        remoteAnalyticsDataSource.logEvent(event, params)
    }

    override fun logEvent(event: String) {
        remoteAnalyticsDataSource.logEvent(event)
    }

    override fun setUserProperty(name: String, value: String) {
        remoteAnalyticsDataSource.setUserProperty(name, value)
    }

    override fun setUserId(userId: String) {
        remoteAnalyticsDataSource.setUserId(userId)
    }
}