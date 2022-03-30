package com.paparazziapps.pretamistapp.modulos.dashboard.interfaces

import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo

interface setOnClickedPrestamo {

    fun actualizarPagoPrestamo(prestamo:Prestamo, needUpdate: Boolean, montoTotalAPagar:String, adapterPosition:Int, diasRestrasado:String)
    fun openDialogoActualizarPrestamo(prestamo:Prestamo, montoTotalAPagar:String, adapterPosition:Int)
}