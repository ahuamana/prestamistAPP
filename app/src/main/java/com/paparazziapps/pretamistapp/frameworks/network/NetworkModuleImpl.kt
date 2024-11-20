package com.paparazziapps.pretamistapp.frameworks.network

import android.content.Context
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.services.EmailRetrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkModuleImpl(
    private val context:Context,
    private val preferences: MyPreferences
    ):NetworkModule {

    override fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ResendConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()
    }

    override fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(provideHeaderInterceptor())
            .build()
    }

    override fun provideHeaderInterceptor(): HeaderInterceptor {
        return HeaderInterceptor(context,preferences)
    }

}