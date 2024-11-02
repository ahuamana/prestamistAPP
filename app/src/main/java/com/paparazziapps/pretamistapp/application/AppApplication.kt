package com.paparazziapps.pretamistapp.application

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
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelLogin
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelRegisterUser
import com.paparazziapps.pretamistapp.presentation.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.presentation.registro.viewmodels.ViewModelRegister
import com.paparazziapps.pretamistapp.presentation.tesoreria.viewmodels.ViewModelFinance
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

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
        single { MyPreferences(androidContext())} // Provide MyPreferences

        //Data
        singleOf(::RemoteBranchDataSourceImpl) { bind<RemoteBranchDataSource>() }
        singleOf(::RemoteDetailLoanDataSourceImpl) { bind<RemoteDetailLoanDataSource>() }
        singleOf(::RemoteLoanDataSourceImpl) { bind<RemoteLoanDataSource>() }
        singleOf(::RemoteLoginDataSourceImpl) { bind<RemoteLoginDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::RemoteUserDataSourceImpl) { bind<RemoteUserDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::PARepositoryImpl) { bind<PARepository>() } // single<PARepository> { PARepositoryImpl() }
    }

    private val uiModule = module {
        viewModelOf(::ViewModelFinance)
        viewModelOf(::ViewModelDashboard)
        viewModelOf(::ViewModelRegisterUser)
        viewModelOf(::ViewModelBranches)
        viewModelOf(::ViewModelPrincipal)
        viewModelOf(::ViewModelRegister)// viewModel { ViewModelRegister(get(), get()) } -> new version dsl
        viewModelOf(::ViewModelLogin)// viewModel { ViewModelLogin(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        turnOffDarkModeInAllApp(resources)
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