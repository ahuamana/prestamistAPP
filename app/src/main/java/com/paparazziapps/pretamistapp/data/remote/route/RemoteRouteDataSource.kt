package com.paparazziapps.pretamistapp.data.remote.route

import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.sources.route.RouteDataSource
import com.paparazziapps.pretamistapp.data.sources.route.RouteDomainSource

interface RemoteRouteDataSource {

    suspend fun createRoute(routeDataSource: RouteDataSource): PAResult<Void>
    suspend fun getAllRoutes(): PAResult<List<RouteDataSource>>

}