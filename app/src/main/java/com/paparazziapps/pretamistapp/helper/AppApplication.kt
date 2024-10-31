package com.paparazziapps.pretamistapp.helper

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.paparazziapps.pretamistapp.data.remote.RemoteDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteDataSourceImpl
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.data.repository.PARepositoryImpl
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelLogin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

lateinit var ctx      : Context

class AppApplication : MultiDexApplication() {

    private val appModule = module {
        viewModelOf(::ViewModelLogin)
        singleOf(::RemoteDataSourceImpl) { bind<RemoteDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::PARepositoryImpl) { bind<PARepository>() } // single<PARepository> { PARepositoryImpl() }
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