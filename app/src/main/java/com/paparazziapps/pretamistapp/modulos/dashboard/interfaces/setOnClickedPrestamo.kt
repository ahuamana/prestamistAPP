package com.paparazziapps.pretamistapp.modulos.dashboard.interfaces

import com.paparazziapps.pretamistapp.modulos.registro.pojo.LoanDomain

interface setOnClickedPrestamo {

    fun actualizarPagoPrestamo(loanDomain:LoanDomain, needUpdate: Boolean, montoTotalAPagar:Double, adapterPosition:Int, diasRestrasado:String)
    fun openDialogoActualizarPrestamo(loanDomain:LoanDomain, montoTotalAPagar:Double, adapterPosition:Int, diasRestantesPorPagar:Int, diasPagados:Int, isClosed:Boolean)
}