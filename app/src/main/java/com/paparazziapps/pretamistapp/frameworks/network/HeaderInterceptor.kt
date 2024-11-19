package com.paparazziapps.pretamistapp.frameworks.network

import android.content.Context
import com.paparazziapps.pretamistapp.BuildConfig
import com.paparazziapps.pretamistapp.application.MyPreferences
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
    private val context: Context,
    private val preferences: MyPreferences,
    ) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.RESEND_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }

    private fun getToken(): String {
        return preferences.getToken()
    }
}