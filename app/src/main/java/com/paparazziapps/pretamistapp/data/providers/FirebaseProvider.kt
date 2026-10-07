package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.data.di.PAAnalyticsImpl

class FirebaseProvider(
    private val firebaseAnalytics: PAAnalyticsImpl
) {

    fun logEvent(event: String, params: Map<String, String>) {
        firebaseAnalytics.logEvent(event, params)
    }

    fun logEvent(event: String) {
        firebaseAnalytics.logEvent(event)
    }

    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }
}