package com.paparazziapps.pretamistapp.application

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.paparazziapps.pretamistapp.data.remote.RemoteDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteDataSourceImpl
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.data.repository.PARepositoryImpl
import com.paparazziapps.pretamistapp.helper.turnOffDarkModeInAllApp
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelLogin
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelRegisterUser
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister
import com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels.ViewModelFinance
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

lateinit var ctx      : Context

class AppApplication : MultiDexApplication() {

    private val appModule = module {
        singleOf(::MyPreferences) { bind() } // single<MyPreferences> { MyPreferences() }
        singleOf(::RemoteDataSourceImpl) { bind<RemoteDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::PARepositoryImpl) { bind<PARepository>() } // single<PARepository> { PARepositoryImpl() }

        viewModel { ViewModelFinance(get(), get()) }
        viewModel { ViewModelLogin(get()) }
        viewModel { ViewModelRegisterUser() }
        viewModel { ViewModelBranches() }
        viewModel { ViewModelPrincipal() }
        viewModel { ViewModelRegister(get()) }

        //viewModelOf(::ViewModelLogin)// viewModel { ViewModelLogin(get()) }
        //viewModelOf(::ViewModelFinance)// viewModel { ViewModelFinance(get(), get()) }
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