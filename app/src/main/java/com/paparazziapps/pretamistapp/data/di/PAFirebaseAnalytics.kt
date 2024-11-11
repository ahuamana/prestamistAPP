package com.paparazziapps.pretamistapp.data.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

class PAFirebaseAnalytics(val context: Context) {

    private val instance by lazy {
        FirebaseAnalytics.getInstance(context)
    }

    fun logEvent(event: String, params: Map<String, String>) {
        val bundle = android.os.Bundle()
        for ((key, value) in params) {
            bundle.putString(key, value)
        }
        instance.logEvent(event, bundle)
    }

    fun logEvent(event: String) {
        instance.logEvent(event, null)
    }

    fun setUserProperty(name: String, value: String) {
        // Set user property
        instance.setUserProperty(name, value)
    }

    fun setUserId(userId: String) {
        // Set user ID
        instance.setUserId(userId)
    }
}