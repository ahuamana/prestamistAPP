package com.paparazziapps.pretamistapp.application

import androidx.multidex.MultiDexApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.di.PAFirebaseAnalytics
import com.paparazziapps.pretamistapp.data.providers.BranchesProvider
import com.paparazziapps.pretamistapp.data.providers.ClientsProvider
import com.paparazziapps.pretamistapp.data.providers.DetailLoanProvider
import com.paparazziapps.pretamistapp.data.providers.FirebaseProvider
import com.paparazziapps.pretamistapp.data.providers.LoanProvider
import com.paparazziapps.pretamistapp.data.providers.LoginProvider
import com.paparazziapps.pretamistapp.data.providers.RegisterProvider
import com.paparazziapps.pretamistapp.data.providers.UserProvider
import com.paparazziapps.pretamistapp.data.services.ServiceProviderImpl
import com.paparazziapps.pretamistapp.data.remote.RemoteAnalyticsDataSource
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
import com.paparazziapps.pretamistapp.data.remote.RemoteEmailDataSourceImpl
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.data.repository.PARepositoryImpl
import com.paparazziapps.pretamistapp.data.repository.PAClientsRepositoryImpl
import com.paparazziapps.pretamistapp.data.repository.PAAnalyticsRepositoryImpl
import com.paparazziapps.pretamistapp.data.remote.RemoteAnalyticsDataSourceImpl
import com.paparazziapps.pretamistapp.data.remote.RemoteEmailDataSource
import com.paparazziapps.pretamistapp.data.remote.clients.RemoteClientsDataSourceImpl
import com.paparazziapps.pretamistapp.data.remote.clients.RemoteClientsDataSource
import com.paparazziapps.pretamistapp.data.repository.PAEmailRepositoryImpl
import com.paparazziapps.pretamistapp.data.repository.PAAnalyticsRepository
import com.paparazziapps.pretamistapp.data.repository.PAClientsRepository
import com.paparazziapps.pretamistapp.data.repository.PAEmailRepository
import com.paparazziapps.pretamistapp.data.services.ServiceProvider
import com.paparazziapps.pretamistapp.data.utils.ReceiptHtmlTemplate
import com.paparazziapps.pretamistapp.frameworks.network.NetworkModule
import com.paparazziapps.pretamistapp.frameworks.network.NetworkModuleImpl
import com.paparazziapps.pretamistapp.helper.turnOffDarkModeInAllApp
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelLogin
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelRegisterUser
import com.paparazziapps.pretamistapp.presentation.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.presentation.registro.viewmodels.ViewModelRegister
import com.paparazziapps.pretamistapp.presentation.tesoreria.viewmodels.ViewModelFinance
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDetailReceipt
import com.paparazziapps.pretamistapp.presentation.profile.viewmodels.ProfileViewModel
import com.paparazziapps.pretamistapp.presentation.clients.ClientsAddViewModel
import com.paparazziapps.pretamistapp.presentation.clients.ClientsParentViewModel
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
        single { RegisterProvider(get()) } // Provide RegisterProvider
        single { UserProvider(get()) } // Provide UserProvider
        single { ClientsProvider(get())}
        //DB
        single { MyPreferences(androidContext())} // Provide MyPreferences

        single { LoginProvider(get(), get(), get(), get()) } // Provide LoginProvider

        //Firebase Analytics
        single { PAFirebaseAnalytics(androidContext())}
        single { FirebaseProvider(get())}

        //Data
        singleOf(::RemoteBranchDataSourceImpl) { bind<RemoteBranchDataSource>() }
        singleOf(::RemoteDetailLoanDataSourceImpl) { bind<RemoteDetailLoanDataSource>() }
        singleOf(::RemoteLoanDataSourceImpl) { bind<RemoteLoanDataSource>() }
        singleOf(::RemoteLoginDataSourceImpl) { bind<RemoteLoginDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::RemoteUserDataSourceImpl) { bind<RemoteUserDataSource>() } // single<RemoteDataSource> { RemoteDataSourceImpl() }
        singleOf(::PARepositoryImpl) { bind<PARepository>() } // single<PARepository> { PARepositoryImpl() }

        //Data Firebase
        singleOf(::RemoteAnalyticsDataSourceImpl) { bind<RemoteAnalyticsDataSource>() }
        singleOf(::PAAnalyticsRepositoryImpl) { bind<PAAnalyticsRepository>() }

        //New Module notification with email
        singleOf(::ReceiptHtmlTemplate) { bind<ReceiptHtmlTemplate>() }
        singleOf(::NetworkModuleImpl) { bind<NetworkModule>() }
        singleOf(::ServiceProviderImpl) { bind<ServiceProvider>() }
        singleOf(::RemoteEmailDataSourceImpl) { bind<RemoteEmailDataSource>() }
        singleOf(::PAEmailRepositoryImpl) { bind<PAEmailRepository>() }

        //New Clients Module
        singleOf(::ClientsProvider) { bind<ClientsProvider>() }
        singleOf(::RemoteClientsDataSourceImpl) { bind<RemoteClientsDataSource>() }
        singleOf(::PAClientsRepositoryImpl) { bind<PAClientsRepository>() }

    }

    private val uiModule = module {
        viewModelOf(::ViewModelFinance)
        viewModelOf(::ViewModelDashboard)
        viewModelOf(::ViewModelRegisterUser)
        viewModelOf(::ViewModelBranches)
        viewModelOf(::ViewModelPrincipal)
        viewModelOf(::ViewModelRegister)// viewModel { ViewModelRegister(get(), get()) } -> new version dsl
        viewModelOf(::ViewModelLogin)// viewModel { ViewModelLogin(get()) }
        viewModelOf(::ViewModelDetailReceipt)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::ClientsAddViewModel)
        viewModelOf(::ClientsParentViewModel)
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