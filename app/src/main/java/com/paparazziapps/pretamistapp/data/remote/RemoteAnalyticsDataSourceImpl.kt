package com.paparazziapps.pretamistapp.data.remote

import com.paparazziapps.pretamistapp.data.providers.FirebaseProvider

class RemoteAnalyticsDataSourceImpl(
    private val firebaseProvider: FirebaseProvider
) : RemoteAnalyticsDataSource {

    override fun logEvent(event: String, params: Map<String, String>) {
        firebaseProvider.logEvent(event, params)
    }

    override fun logEvent(event: String) {
        firebaseProvider.logEvent(event)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseProvider.setUserProperty(name, value)
    }

    override fun setUserId(userId: String) {
        firebaseProvider.setUserId(userId)
    }
}