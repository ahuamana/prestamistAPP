package com.paparazziapps.pretamistapp.presentation.dashboard.interfaces

import com.paparazziapps.pretamistapp.domain.LoanDomain

interface SetOnClickedLoan {
    fun updateLoanPaid(loanDomain: LoanDomain, needUpdate: Boolean, totalAmountToPay:Double, adapterPosition:Int, daysDelayed:String)
    fun openDialogUpdateLoan(loanDomain: LoanDomain, quotesToPay:Int)
}