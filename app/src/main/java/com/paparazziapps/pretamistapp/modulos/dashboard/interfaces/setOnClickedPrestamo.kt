package com.paparazziapps.pretamistapp.modulos.dashboard.interfaces

import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo

interface setOnClickedPrestamo {

    fun actualizarPagoPrestamo(prestamo:Prestamo, needUpdate: Boolean, montoTotalAPagar:Double, adapterPosition:Int, diasRestrasado:String)
    fun openDialogoActualizarPrestamo(prestamo:Prestamo, montoTotalAPagar:Double, adapterPosition:Int,diasRestantesPorPagar:Int, diasPagados:Int)
}