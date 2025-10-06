package com.paparazziapps.pretamistapp.data.providers

import com.google.firebase.firestore.CollectionReference
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.data.di.FirebaseService
import com.paparazziapps.pretamistapp.data.network.NetworkOperation
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.data.sources.route.RouteDataSource
import com.paparazziapps.pretamistapp.data.sources.route.RouteDomainSource
import kotlinx.coroutines.tasks.await

class RouteProvider(
    private val firebaseProvider: FirebaseService
) {
    private val mCollection: CollectionReference by lazy {
        firebaseProvider.firestore.collection(PADataConstants.ROUTES_COLLECTION)
    }

    suspend fun createRoute(
        routeDataSource: RouteDataSource
    ): PAResult<Void> {
        return NetworkOperation.safeApiCall {
            mCollection.document(routeDataSource.id).set(routeDataSource).await()
        }
    }

    suspend fun getAllRoutes(): PAResult<List<RouteDataSource>> {
        return NetworkOperation.safeApiCall {
            mCollection
                .get()
                .await()
                .toObjects(RouteDataSource::class.java)
        }
    }
}