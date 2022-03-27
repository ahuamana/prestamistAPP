package com.paparazziapps.pretamistapp.helper

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


    fun getDoubleWithTwoDecimals (number:Double):String?
    {
        return DecimalFormat("###,###,###.00").format(number)
    }

    fun getFechaActualNormalCalendar() : String
    {
        SimpleDateFormat("dd/MM/yyyy").apply {
            timeZone = TimeZone.getTimeZone("GMT-5")
            format(Date()).toString().also {
                return it
            }
        }
    }

    fun convertFechaActualNormalToUnixtime(fecha: String)  : Long
    {
      return SimpleDateFormat("dd/MM/yyyy").parse(fecha).time
    }

    fun replaceFirstCharInSequenceToUppercase(text: String): String
    {
        val words = text.split(' ');
        var subString = words.joinToString(" ") { word ->
            word.replaceFirstChar {
                it.uppercase()
            }

        }

        return subString

    }

