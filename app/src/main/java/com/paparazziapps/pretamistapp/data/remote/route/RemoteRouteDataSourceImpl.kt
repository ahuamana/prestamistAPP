package com.paparazziapps.pretamistapp.data.remote.route

import com.paparazziapps.pretamistapp.data.providers.RouteProvider
import com.paparazziapps.pretamistapp.data.sources.route.RouteDataSource

class RemoteRouteDataSourceImpl(
    private val routerProvider: RouteProvider
): RemoteRouteDataSource {
    override suspend fun createRoute(routeDataSource: RouteDataSource) =
        routerProvider.createRoute(
            RouteDataSource(
                id = routeDataSource.id,
                name = routeDataSource.name
            )
        )

    override suspend fun getAllRoutes() = routerProvider.getAllRoutes()

}