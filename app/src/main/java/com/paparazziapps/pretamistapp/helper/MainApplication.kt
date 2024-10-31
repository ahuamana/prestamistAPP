package com.paparazziapps.pretamistapp.helper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDexApplication
import com.google.android.gms.ads.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.paparazziapps.pretamistapp.helper.views.AppOpenAd
import java.util.*

lateinit var ctx      : Context

class MainApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        turnOffDarkModeInAllApp(resources)
        ctx = this
        FirebaseAnalytics.getInstance(this)
    }
}