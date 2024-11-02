package com.paparazziapps.pretamistapp.data.remote

import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.DetallePrestamoSender

interface RemoteDetailLoanDataSource {
    suspend fun createDetail(detailLoan: DetallePrestamoSender): PAResult<Void>
    suspend fun getDetailLoanByDate(date:String):PAResult<QuerySnapshot>
    suspend fun getLoanByDate(timeStart:Long, timeEnd:Long, idBranch:Int): PAResult<QuerySnapshot>
}