package com.paparazziapps.pretamistapp.data.sources.route

class RouteDomainSource(
    val id: String = "",
    val name: String = "",
) {
    companion object {
        fun fromDataSource(source: RouteDataSource) = RouteDomainSource(
            id = source.id,
            name = source.name
        )

        fun toDataSource(source: RouteDomainSource) = RouteDataSource(
            id = source.id,
            name = source.name
        )
    }
}

