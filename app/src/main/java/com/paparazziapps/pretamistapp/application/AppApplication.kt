package com.paparazziapps.pretamistapp.application

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.providers.BranchesProvider
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import com.paparazziapps.pretamistapp.data.providers.RegisterProvider
import com.paparazziapps.pretamistapp.data.providers.UserProvider
import com.paparazziapps.pretamistapp.data.remote.RemoteBranchDataSourceImpl
import com.paparazziapps.pretamistapp.data.remote.RemoteBranchDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteDetailLoanDataSourceImpl
import com.paparazziapps.pretamistapp.data.remote.RemoteDetailLoanDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteLoanDataSourceImpl
import com.paparazziapps.pretamistapp.data.remote.RemoteLoanDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteLoginDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteLoginDataSourceImpl
import com.paparazziapps.pretamistapp.data.remote.RemoteUserDataSource
import com.paparazziapps.pretamistapp.data.remote.RemoteUserDataSourceImpl
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
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

lateinit var ctx      : Context

class AppApplication : MultiDexApplication() {

    private val dataModule = module {
        single { FirebaseService() } // Provide FirebaseFirestore
        single { LoanProvider(get(), get()) } // Provide LoanProvider
        single { BranchesProvider() } // Provide BranchesProvider
        single { DetailLoanProvider(get(), get()) } // Provide DetailLoanProvider
        single { LoanProvider(get(),get())} // Provide LoanProvider
        single { LoginProvider(get())} // Provide LoginProvider
        single { RegisterProvider(get()) } // Provide RegisterProvider
        single { UserProvider(get()) } // Provide UserProvider
        //DB
        singleOf(::MyPreferences) { bind() } // single<MyPreferences> { MyPreferences() }

        //Data

        singleOf(::RemoteBranchDataSourceImpl) { bind<RemoteBranchDataSource>() }
        singleOf(::RemoteDetailLoanDataSourceImpl) { bind<RemoteDetailLoanDataSource>() }
        singleOf(::RemoteLoanDataSourceImpl) { bind<RemoteLoanDataSource>() }
        singleOf(::RemoteLoginDataSourceImpl) { bind<RemoteLoginDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::RemoteUserDataSourceImpl) { bind<RemoteUserDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::PARepositoryImpl) { bind<PARepository>() } // single<PARepository> { PARepositoryImpl() }
    }

    private val uiModule = module {
        viewModel { ViewModelFinance(get(), get()) }
        viewModel { ViewModelLogin(get(), get()) }
        viewModel { ViewModelDashboard(get(),get()) }
        viewModel { ViewModelRegisterUser(get(), get(), get()) }
        viewModel { ViewModelBranches(get()) }
        viewModel { ViewModelPrincipal(get(),get()) }
        viewModelOf(::ViewModelRegister)// viewModel { ViewModelRegister(get(), get()) } -> new version dsl
        viewModelOf(::ViewModelLogin)// viewModel { ViewModelLogin(get()) }
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