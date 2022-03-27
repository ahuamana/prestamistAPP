package com.paparazziapps.pretamistapp.helper

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance:MainApplication?=null

        fun ctx(): Context
        {
            return instance!!.applicationContext
        }

    }

    override fun onCreate() {
        super.onCreate()

        val ctx = ctx()
    }
}