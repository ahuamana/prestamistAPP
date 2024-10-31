package com.paparazziapps.pretamistapp.helper

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

lateinit var ctx      : Context

class AppApplication : MultiDexApplication() {

    private val appModule = module {
        //TODO: Add your modules here
    }

    override fun onCreate() {
        super.onCreate()
        turnOffDarkModeInAllApp(resources)
        ctx = this
        FirebaseAnalytics.getInstance(this)
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@AppApplication)
            modules(appModule)
        }
    }
}