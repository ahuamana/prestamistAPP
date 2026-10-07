package com.paparazziapps.pretamistapp.data.providers

import com.paparazziapps.pretamistapp.data.network.GlobalResponse
import com.paparazziapps.pretamistapp.libraries.database.firebase.FDatabaseImpl
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.sources.route.RouteDataSource

class RouteProvider private constructor(
    private val firebaseProvider: FDatabaseImpl
) {

    suspend fun createRoute(
        routeDataSource: RouteDataSource
    ): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
           test
        }
    }

    suspend fun getAllRoutes(): PAResult<GlobalResponse> {
        return NetworkOperation.safeApiCall {
            test
        }
    }

    companion object {
        val test = GlobalResponse(true)

        fun instance(): RouteProvider{
            return RouteProvider(FDatabaseImpl.Companion.instance())
        }
    }
}