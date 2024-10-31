package com.paparazziapps.pretamistapp.data.providers

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.data.PADataConstants

class LoanProvider(private val preferences: MyPreferences) {

    private val mCollectionLoan: CollectionReference by lazy { FirebaseFirestore.getInstance().collection(PADataConstants.LOAN_COLLECTION) }
    private val tag = LoginProvider::class.java.simpleName

    //Super admin -- implemented
    fun create(loanDomain: LoanDomain, idSucursal:Int): Task<Void> {
        loanDomain.sucursalId = if(preferences.isSuperAdmin) idSucursal else  preferences.sucursalId
        loanDomain.id = mCollectionLoan.document().id
        return mCollectionLoan.document(loanDomain.id!!).set(loanDomain)
    }

    //Super admin -- implemented
    fun getLoans(): Task<QuerySnapshot> {
        if(preferences.isSuperAdmin){
            Log.d(tag,"Super admin -- getPrestamos")
            return  mCollectionLoan
                .whereEqualTo("state", "ABIERTO")
                .get()
        }else{
            Log.d(tag,"Sucursal -- getPrestamos")
            return  mCollectionLoan
                .whereEqualTo("state", "ABIERTO")
                .whereEqualTo("sucursalId",preferences.sucursalId)
                .get()
        }

    }

    //No need superAdmin - or Adming to update
    fun setLastPayment(id:String, fecha:String,diasRestantesPorPagar:Int, diasPagados:Int): Task<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)
        map.put("dias_restantes_por_pagar",diasRestantesPorPagar)
        map.put("diasPagados",diasPagados)
        return mCollectionLoan.document(id).update(map)
    }

    fun setLastPaymentForQuota(
        id:String,
        fecha:String,
        diasRestantesPorPagar:Int,
        diasPagados:Int,
        quotesPaid:Int): Task<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("fechaUltimoPago",fecha)
        map.put("quotasPending",diasRestantesPorPagar)
        map.put("quotasPaid",quotesPaid)
        map.put("diasPagados",diasPagados)
        return mCollectionLoan.document(id).update(map)
    }

    //No need superAdmin - or Adming to update
    fun closeLoan(id: String):Task<Void> {
        val map = mutableMapOf<String,Any?>()
        map.put("state","CERRADO")
        return mCollectionLoan.document(id).update(map)
    }
}