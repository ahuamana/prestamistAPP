package com.paparazziapps.pretamistapp.frameworks.network

import android.content.Context
import com.paparazziapps.pretamistapp.application.MyPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModule(
    private val context:Context,
    private val preferences: MyPreferences
    ) {

    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ResendConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(provideHeaderInterceptor())
            .build()
    }

    private fun provideHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor(context,preferences)
    }
}