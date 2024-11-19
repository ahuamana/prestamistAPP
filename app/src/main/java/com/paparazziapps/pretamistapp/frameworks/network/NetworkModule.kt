package com.paparazziapps.pretamistapp.frameworks.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface NetworkModule {
    fun provideRetrofit(): Retrofit
    fun provideOkHttpClient(): OkHttpClient
    fun provideHeaderInterceptor(): HeaderInterceptor

}