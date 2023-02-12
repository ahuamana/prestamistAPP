package com.paparazziapps.pretamistapp.helper

import android.app.Application
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics

lateinit var ctx      : Context
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        turnOffDarkModeInAllApp(resources)
        ctx = this
        FirebaseAnalytics.getInstance(this)
    }
}