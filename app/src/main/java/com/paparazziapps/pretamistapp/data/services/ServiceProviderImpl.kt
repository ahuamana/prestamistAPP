package com.paparazziapps.pretamistapp.data.services

import com.paparazziapps.pretamistapp.frameworks.network.NetworkModule

class ServiceProviderImpl(
    private val networkModule: NetworkModule
):ServiceProvider {
    override fun emailService(): EmailRetrofit {
        return networkModule.provideRetrofit().create(EmailRetrofit::class.java)
    }
}