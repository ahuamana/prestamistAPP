package com.paparazziapps.pretamistapp.data.sources.route

data class RouteDataSource (
    val id: String = "",
    val name: String = "",
) {
    companion object {
        fun fromDomain(source: RouteDomainSource) = RouteDataSource(
            id = source.id,
            name = source.name
        )

        fun toDomain(source: RouteDataSource) = RouteDomainSource(
            id = source.id,
            name = source.name
        )

        fun toList(source: List<RouteDataSource>) = source.map {
            toDomain(it)
        }
    }
}