package com.paparazziapps.pretamistapp.presentation.dashboard.views

import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.domain.TypePrestamo
import com.paparazziapps.pretamistapp.helper.fromJson

class LoanListHandler(private val preferences: MyPreferences) {

    fun processLoans(loans: List<LoanDomain>): List<LoanDomain> {
        return if (preferences.isSuperAdmin) {
            val localBranches = fromJson<List<Sucursales>>(preferences.branches)
            localBranches.flatMap { branch ->
                listOf(
                    LoanDomain(
                        type = TypePrestamo.TITLE.value,
                        title = branch.name
                    )
                ) + loans.filter { it.branchId == branch.id }.distinct() // Elimina duplicados si los hay
            }
        } else {
            loans
        }
    }
}