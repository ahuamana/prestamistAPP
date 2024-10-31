package com.paparazziapps.pretamistapp.application

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.paparazziapps.pretamistapp.data.providers.BranchesProvider
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import com.paparazziapps.pretamistapp.data.providers.RegisterProvider
import com.paparazziapps.pretamistapp.data.providers.UserProvider
import com.paparazziapps.pretamistapp.data.remote.RemoteDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteDataSourceImpl
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.data.repository.PARepositoryImpl
import com.paparazziapps.pretamistapp.helper.turnOffDarkModeInAllApp
import com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelLogin
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelRegisterUser
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister
import com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels.ViewModelFinance
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

lateinit var ctx      : Context

class AppApplication : MultiDexApplication() {

    private val dataModule = module {
        single { LoanProvider(get()) } // Provide LoanProvider
        single { BranchesProvider() } // Provide BranchesProvider
        single { DetailLoanProvider(get()) } // Provide DetailLoanProvider
        single { LoanProvider(get())} // Provide LoanProvider
        single { LoginProvider()} // Provide LoginProvider
        single { RegisterProvider() } // Provide RegisterProvider
        single { UserProvider() } // Provide UserProvider
        singleOf(::MyPreferences) { bind() } // single<MyPreferences> { MyPreferences() }
        singleOf(::RemoteDataSourceImpl) { bind<RemoteDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::PARepositoryImpl) { bind<PARepository>() } // single<PARepository> { PARepositoryImpl() }
    }

    private val uiModule = module {
        viewModel { ViewModelFinance(get(), get()) }
        viewModel { ViewModelLogin(get(), get()) }
        viewModel { ViewModelDashboard(get(),get()) }
        viewModel { ViewModelRegisterUser() }
        viewModel { ViewModelBranches() }
        viewModel { ViewModelPrincipal() }
        viewModel { ViewModelRegister(get()) }
        //viewModelOf(::ViewModelLogin)// viewModel { ViewModelLogin(get()) }
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
            modules(dataModule, uiModule)
        }
    }
}