package com.paparazziapps.pretamistapp.helper

import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
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

    fun getDiasRestantesFromDateToNow(fecha: String):String
    {
       return (convertFechaActualNormalToUnixtime(getFechaActualNormalCalendar()).minus(convertFechaActualNormalToUnixtime(fecha))).div(86400000).toString()
    }


    fun String?.fromHtml() : Spanned? {
        val html =  this
            ?.replace("<strong>", "<b>")
            ?.replace("</strong>", "</b>")
            ?.replace("<ul>", "")
            ?.replace("</ul>", "")
            ?.replace("<li>", "<p>•&nbsp;&nbsp;&nbsp;")
            ?.replace(" ", "&nbsp;")
            ?.replace("</li>", "</p>")

        return when {
            html == null -> SpannableString("")
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            else         -> Html.fromHtml(html, null, MyTagHandler())
        }
    }

