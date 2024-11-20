package com.paparazziapps.pretamistapp.data.services

interface ServiceProvider {
    fun emailService(): EmailRetrofit
}