package com.paparazziapps.pretamistapp.helper

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.helper.MainApplication.Companion.ctx
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

    val DiaUnixtime = 86400000;

    fun getDoubleWithTwoDecimals (number:Double):String?
    {
        return DecimalFormat("###,###,###.00").format(number)
    }

    fun getDoubleWithTwoDecimalsReturnDouble (number:Double):Double?
    {
        return String.format("%.2f", number).toDouble()
    }

    fun getDiasRestantesFromStart(fecha_inicio:String, plazo_vto: Int): Int
    {
        var diasInUnixtime =  DiaUnixtime * plazo_vto!!.toLong()
        //println("Dias Unixtime $diasInUnixtime")
        var fechaFinalUnixtime= (convertFechaActualNormalToUnixtime(fecha_inicio) + diasInUnixtime )
        //println("fechadinal Unixtime $fechaFinalUnixtime")
        var diasEnCuantoTerminado = (fechaFinalUnixtime - getFechaActualNormalInUnixtime()).div(DiaUnixtime)
        //println("diasEnCuantoTerminado Unixtime $diasEnCuantoTerminado")

        return diasEnCuantoTerminado.toInt()
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getYesterdayFechaNormal():String
    {
       return convertUnixtimeToFechaNormal(getFechaActualNormalInUnixtime().minus(DiaUnixtime))
    }

    fun convertUnixtimeToFechaNormal(unixtime_date:Long): String
    {
        unixtime_date.also {
            SimpleDateFormat("dd/MM/yyyy").apply {
                timeZone = TimeZone.getTimeZone("GMT")
                format(it).toString().also {
                    return it
                }
            }
        }

    }

    fun getFechaActualNormalInUnixtime(): Long
    {
      return  convertFechaActualNormalToUnixtime(getFechaActualNormalCalendar())
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

    fun getDiasRestantesFromDateToNowMinusDiasPagados(fecha: String, diasPagados:Int):String
    {
        return ((convertFechaActualNormalToUnixtime(getFechaActualNormalCalendar()).minus(convertFechaActualNormalToUnixtime(fecha))).div(86400000).toInt().minus(diasPagados)).toString()
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


fun getValidateColorHex(color: Int = ctx().resources.getColor(R.color.primarycolordark_two)): Int{
    val hexColor = "#"+Integer.toHexString(color).substring(2)
    val validateColor = Color.parseColor(hexColor)
    return validateColor
}

fun AppCompatButton.standardSimpleButtonOutline(color: Int = resources.getColor(R.color.primarycolordark_two)){
    this.apply {
        background = getRippleDrawable(BUTTON_OUTLINE)
        setTextColor(getValidateColorHex(color))
    }
}

fun AppCompatButton.standardSimpleButtonOutlineDisable(){
    this.apply {
        background =  ContextCompat.getDrawable(this.context, R.drawable.button_corner) as Drawable
        setTextColor(ContextCompat.getColor(this.context, R.color.color_text_web))
    }
}

fun AppCompatButton.standardSimpleButton(){
    this.apply {
        background = getRippleDrawable(BUTTON)
        setTextColor(getValidateColorHex())
    }
}

fun getRippleDrawable(type: String): RippleDrawable {
    val drawable =
        if (type == BUTTON_OUTLINE) ContextCompat.getDrawable(ctx(), R.drawable.button_corner) as Drawable
        else ContextCompat.getDrawable(ctx(), R.drawable.button_corner_without_stroke) as Drawable

    val customDrawable = tintDrawable(drawable)
    val mask = ContextCompat.getDrawable(ctx(), R.drawable.border_mask) as Drawable

    return RippleDrawable(getPressedColorSelector2(), customDrawable, mask) //getRippleMask() try this
}

fun tintDrawable(drawable: Drawable, @ColorInt color: Int = ctx().resources.getColor(R.color.primarycolordark_two)): Drawable {
    (drawable as? VectorDrawableCompat)
        ?.apply { setTintList(ColorStateList.valueOf(color)) }
        ?.let { return it }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        (drawable as? VectorDrawable)
            ?.apply { setTintList(ColorStateList.valueOf(color)) }
            ?.let { return it }
    }

    val wrappedDrawable = DrawableCompat.wrap(drawable)
    DrawableCompat.setTint(wrappedDrawable, color)
    return DrawableCompat.unwrap(wrappedDrawable)
}

fun getPressedColorSelector2(
    pressedColor : Int      = getColorWithAlpha(ctx().resources.getColor(R.color.primarycolordark_two), 0.3f),
    normalColor  : Int      = getColorWithAlpha(ctx().resources.getColor(R.color.primarycolordark_two), 0.3f),
    isSelect     : Boolean  = false,
): ColorStateList {
    return ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_activated),
            intArrayOf()
        ),
        intArrayOf(
            pressedColor,
            pressedColor,
            pressedColor,
            if (isSelect) pressedColor else normalColor
        )
    )
}


fun getColorWithAlpha(color: Int, ratio: Float): Int {
    return Color.argb(
        Math.round(Color.alpha(color) * ratio),
        Color.red(color),
        Color.green(color),
        Color.blue(
            color
        )
    )
}




const val BUTTON = "BUTTON"
const val BUTTON_OUTLINE = "BUTTON_OUTLINE"

