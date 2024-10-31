package com.paparazziapps.pretamistapp.modulos.dashboard.interfaces

import com.paparazziapps.pretamistapp.domain.LoanDomain

interface SetOnClickedLoan {

    fun updateLoanPaid(loanDomain: LoanDomain, needUpdate: Boolean, totalAmountToPay:Double, adapterPosition:Int, daysDelayed:String)
    fun openDialogUpdateLoan(loanDomain: LoanDomain, totalAmountToPay:Double, adapterPosition:Int, daysMissingToPay:Int, paidDays:Int, isClosed:Boolean)
}