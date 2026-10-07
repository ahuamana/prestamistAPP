package com.paparazziapps.pretamistapp.data.di

import android.content.Context


interface PAAnalytics {
    fun logEvent(event: String, params: Map<String, String>)
    fun logEvent(event: String)
    fun setUserProperty(name: String, value: String)
    fun setUserId(userId: String)
}

class PAAnalyticsImpl(val context: Context) : PAAnalytics {

    override fun logEvent(event: String, params: Map<String, String>) {
        val bundle = android.os.Bundle()
        for ((key, value) in params) {
            bundle.putString(key, value)
        }
        //TODO: Add invocation click
    }

    override fun logEvent(event: String) {
        //TODO: Add invocation click
    }

    override fun setUserProperty(name: String, value: String) {
        //TODO: Add invocation click
    }

    override fun setUserId(userId: String) {
        //TODO: Add invocation click
    }

}