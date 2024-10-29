package com.paparazziapps.pretamistapp.modulos.dashboard.interfaces

import com.paparazziapps.pretamistapp.modulos.registro.pojo.LoanResponse

interface setOnClickedPrestamo {

    fun actualizarPagoPrestamo(loanResponse:LoanResponse, needUpdate: Boolean, montoTotalAPagar:Double, adapterPosition:Int, diasRestrasado:String)
    fun openDialogoActualizarPrestamo(loanResponse:LoanResponse, montoTotalAPagar:Double, adapterPosition:Int, diasRestantesPorPagar:Int, diasPagados:Int, isClosed:Boolean)
}