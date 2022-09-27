package com.paparazziapps.pretamistapp.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.VectorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.text.*
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.snackbar.Snackbar
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.helper.MainApplication.Companion.ctx
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

//Shared Preferences
    private var appContext: Context? = null

    val application: Context
        get() = appContext ?: initAndGetAppCtxWithReflection()

    @SuppressLint("PrivateApi")
    private fun initAndGetAppCtxWithReflection(): Context {
        val activityThread = Class.forName("android.app.ActivityThread")
        val ctx = activityThread.getDeclaredMethod("currentApplication").invoke(null) as Context
        appContext = ctx
        return ctx
    }


    val DiaUnixtime = 86400000;

    fun getDoubleWithTwoDecimals (number:Double):String?
    {
        return DecimalFormat("###,###,###.00").format(number)
    }

    fun getDoubleWithTwoDecimalsReturnDouble (number:Double):Double?
    {
        return number.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun getDoubleWithOneDecimalsReturnDouble (number:Double):Double?
    {
        return number.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()
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


fun getValidateColorHex(color: Int = ctx().resources.getColor(R.color.colorPrimary)): Int{
    val hexColor = "#"+Integer.toHexString(color).substring(2)
    val validateColor = Color.parseColor(hexColor)
    return validateColor
}

fun AppCompatButton.standardSimpleButtonOutline(color: Int = resources.getColor(R.color.colorPrimary)){
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

fun tintDrawable(drawable: Drawable, @ColorInt color: Int = ctx().resources.getColor(R.color.colorPrimary)): Drawable {
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
    pressedColor : Int      = getColorWithAlpha(ctx().resources.getColor(R.color.colorPrimary), 0.3f),
    normalColor  : Int      = getColorWithAlpha(ctx().resources.getColor(R.color.colorPrimary), 0.3f),
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

//Global Methods

fun showMessageAboveMenuInferiorGlobal(message: String?, view:CoordinatorLayout) {
    Snackbar.make(view,"$message", Snackbar.LENGTH_SHORT).apply {
        view.elevation = 1000F
    }.show()
}

fun EditText.setMaxLength(maxLength: Int){
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
}


fun hideKeyboardActivity(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboardFrom(){
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun setColorToStatusBar(activity: Activity, color: Int = getColor(ctx(),R.color.colorPrimary)) {
    val window = activity.window
    val hsv = FloatArray(3)
    var darkColor: Int = color

    Color.colorToHSV(darkColor, hsv)
    hsv[2] *= 0.8f // value component
    darkColor = Color.HSVToColor(hsv)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = darkColor //Define color
    }
}

inline fun <reified T> toJson(value : T) = Json{
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
}.encodeToString(value)



inline fun <reified T> fromJson(json: String) : T {
    return Json{
        ignoreUnknownKeys = true
        isLenient = true
    }.decodeFromString(json)
}

fun isConnected(context:Context):Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    try {
        if (activeNetwork != null) {
            val caps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm.getNetworkCapabilities(cm.activeNetwork)
            } else {

            }
        }
    } catch (e: Exception) {
        Log.e("ERROR", "" + e.message)
    }
    return activeNetwork != null && activeNetwork.isConnected
}

fun isValidEmail(target: CharSequence?): Boolean {
    return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
}

fun getSucursales(text: String): List<Sucursales>{
    var sucursalList = mutableListOf<Sucursales>()
    var arrayAll = text.substring(0,text.lastIndex).split(",")
    println("Array all: $arrayAll")

    arrayAll.forEach {
        var foundRight = ("=\\w*\\s*\\w*\\w*\\s*\\w*\\s*\\w*\\s*\\w*\\s*".toRegex()).find(it)
        var foundLeft = ("\\w*=".toRegex()).find(it)
        //dataRight.add(foundRight?.value?.replace("=","")?:"")
        sucursalList.add(Sucursales(
            id = foundLeft?.value?.replace("=","")?.toInt(),
            name = foundRight?.value?.replace("=","")
        ))
    }

    return sucursalList
}

fun Context.getVersionName(): String {
    var versionName = ""
    try {
        versionName = applicationContext.packageManager.getPackageInfo(
            applicationContext.packageName,
            0
        ).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionName
}



const val BUTTON = "BUTTON"
const val BUTTON_OUTLINE = "BUTTON_OUTLINE"

