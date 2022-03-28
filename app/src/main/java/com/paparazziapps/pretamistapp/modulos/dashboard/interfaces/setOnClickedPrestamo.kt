package com.paparazziapps.pretamistapp.modulos.dashboard.interfaces

import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo

interface setOnClickedPrestamo {

    fun ActualizarPagoPrestamo(prestamo:Prestamo, needUpdate: Boolean, montoTotalAPagar:String, adapterPosition:Int)
}