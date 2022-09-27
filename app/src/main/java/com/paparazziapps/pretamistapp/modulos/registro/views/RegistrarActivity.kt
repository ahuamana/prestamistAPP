package com.paparazziapps.pretamistapp.modulos.registro.views

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegistrarBinding
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelSucursales
import com.paparazziteam.yakulap.helper.applicacion.MyPreferences


class RegistrarActivity : AppCompatActivity() {

    val _viewModel = ViewModelRegister.getInstance()
    var _viewModelSucursales = ViewModelSucursales.getInstance()

    lateinit var binding: ActivityRegistrarBinding
    var prestamoReceived = Prestamo()
    lateinit var fecha:TextInputEditText
    lateinit var layoutFecha:TextInputLayout
    lateinit var nombres:TextInputEditText
    lateinit var layoutNombres:TextInputLayout
    lateinit var apellidos:TextInputEditText
    lateinit var layoutApellidos:TextInputLayout
    lateinit var dni:TextInputEditText
    lateinit var layoutDNI:TextInputLayout
    lateinit var celular:TextInputEditText
    lateinit var layoutCelular:TextInputLayout

    lateinit var registerButton:MaterialButton
    lateinit var toolbar: Toolbar
    var fechaSelectedUnixtime:Long? = null

    var preferences = MyPreferences()

    //Sucursales Supér Admin
    var listaSucursales = mutableListOf<Sucursales>()
    lateinit var sucursalTxt:AutoCompleteTextView
    lateinit var sucursalTxtLayout:TextInputLayout
    lateinit var viewProgressSucursal: View
    lateinit var viewCurtainSucursal: View
    lateinit var viewDotsSucursal: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fecha = binding.fecha
        nombres = binding.nombres
        apellidos = binding.apellidos
        dni = binding.dni
        celular = binding.celular
        registerButton = binding.registrarButton
        toolbar         = binding.tool.toolbar

        layoutFecha         = binding.fechaLayout
        layoutNombres       = binding.nombresLayout
        layoutApellidos     = binding.apellidosLayout
        layoutDNI           = binding.dniLayout
        layoutCelular       = binding.celularLayout

        //SuperAdmin
        sucursalTxt         = binding.edtSucursal
        sucursalTxtLayout   = binding.sucursalTxtInputLyt

        viewProgressSucursal = binding.progressSucursal
        viewDotsSucursal     = binding.dotsSucursal
        viewCurtainSucursal  = binding.curtainSucursal

        fieldsSuperAdmin()

        //Set max lengh Document
        dni.setMaxLength(resources.getInteger(R.integer.cantidad_documento_max))
        layoutDNI.counterMaxLength = resources.getInteger(R.integer.cantidad_documento_max)

        //get intent
        getExtras()
        showCalendar()
        validateFields()
        registerPrestamo()
        setUpToolbarInitialize()

        //Observers
        startObservers()
    }

    private fun fieldsSuperAdmin() {
        if(preferences.isSuperAdmin){
            sucursalTxtLayout.beVisible()
            viewProgressSucursal.beVisible()
            _viewModelSucursales.getSucursales()
        }
    }

    private fun startObservers() {
        _viewModel.getMessage().observe(this){message ->  showMessage(message)}

        _viewModelSucursales.showSucursales().observe(this){

            if(it.isNotEmpty()) {
                listaSucursales = it.toMutableList()
                var scrsales = mutableListOf<String>()
                it.forEach {
                    scrsales.add(it.name?:"")
                }

                val adapterSucursales= ArrayAdapter(this,R.layout.select_items, scrsales)
                sucursalTxt.setAdapter(adapterSucursales)
                sucursalTxt.setOnClickListener { sucursalTxt.showDropDown() }
                sucursalTxtLayout.setEndIconOnClickListener { sucursalTxt.showDropDown() }

                viewProgressSucursal.isVisible = false
                viewDotsSucursal.isVisible = false
                viewCurtainSucursal.isVisible = false

            }

        }
    }

    private fun showMessage(message: String?) {
        Snackbar.make(binding.root,"${message}",Snackbar.LENGTH_SHORT).show()
    }

    private fun setUpToolbarInitialize() {
        toolbar.title = "Registrar"
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun registerPrestamo() {

        registerButton.apply {

            setOnClickListener {
                hideKeyboardActivity(this@RegistrarActivity)
                isEnabled = false
                binding.cortina.isVisible = true

                var prestamo = Prestamo(
                    nombres     = nombres.text.toString().trim(),
                    apellidos   = apellidos.text.toString().trim(),
                    dni         = dni.text.toString().trim(),
                    celular     = celular.text.toString().trim(),
                    fecha       = fecha.text.toString().trim(),
                    unixtime    = fechaSelectedUnixtime,
                    unixtimeRegistered = getFechaActualNormalInUnixtime(),
                    capital     = prestamoReceived.capital,
                    interes     = prestamoReceived.interes,
                    plazo_vto   = prestamoReceived.plazo_vto,
                    dias_restantes_por_pagar   = prestamoReceived.plazo_vto,
                    diasPagados = 0,
                    montoDiarioAPagar = prestamoReceived.montoDiarioAPagar,
                    montoTotalAPagar = prestamoReceived.montoTotalAPagar,
                    state = "ABIERTO"
                )

                var idSucursalSelected:Int = INT_DEFAULT

                listaSucursales.forEach {
                    if(it.name?.equals(sucursalTxt.text.toString().trim()) == true) idSucursalSelected = it.id?: INT_DEFAULT
                }

                //Register ViewModel
                //Actualizar el idSucursal para crear un prestamo como superAdmin
                _viewModel.createPrestamo(prestamo, idSucursal = idSucursalSelected){
                        isCorrect, msj, result, isRefresh ->

                    if(isCorrect)
                    {
                        //showMessage(msj)
                        intent.putExtra("mensaje", msj)
                        setResult(RESULT_OK, intent)
                        finish()

                    }else{
                        binding.cortina.isVisible = false
                        isEnabled = true
                    }
                }
               //Fin click listener
            }



        }
    }

    private fun validateFields() {

        fecha.doAfterTextChanged {
            showbutton()
        }

        nombres.doAfterTextChanged {

            var nombresChanged = it.toString()

            layoutNombres.error = when
            {
                nombresChanged.isNullOrEmpty() -> "El nombre esta vacío"
                nombresChanged.count() < 4 -> "El nombre esta incompleto"
                else -> null
            }
            showbutton()

        }

        apellidos.doAfterTextChanged {

            var apellidosChanged = it.toString()

            layoutApellidos.error = when
            {
                apellidosChanged.isNullOrEmpty() -> "Los apellidos estan vacíos"
                apellidosChanged.count() < 4 -> "Los apellidos estan incompletos"
                else -> null
            }
            showbutton()

        }

        dni.doAfterTextChanged {

            var doucmentoChanged = it.toString()
            var documentoMax = resources.getInteger(R.integer.cantidad_documento_max)

            layoutDNI.error = when
            {
                doucmentoChanged.isNullOrEmpty() -> "${getString(R.string.documento_vacío)}"
                doucmentoChanged.count() in 1 until documentoMax -> "${getString(R.string.documento_incompleto)}"
                else -> null
            }

            showbutton()

        }

        celular.doAfterTextChanged {

            var celularChanged = it.toString()

            layoutCelular.error = when
            {
                celularChanged.isNullOrEmpty() -> "Celular vacío"
                celularChanged.count() in 1..8 -> "Celular incompleto"
                else -> null
            }

            showbutton()

        }


    }

    private fun showbutton() {

        if(!nombres.text.toString().trim().isNullOrEmpty()          &&
            nombres.text.toString().trim().count() >= 4             &&
            !apellidos.text.toString().trim().isNullOrEmpty()       &&
            apellidos.text.toString().trim().count() >= 4           &&
            !celular.text.toString().trim().isNullOrEmpty()         &&
            celular.text.toString().trim().count() == 9             &&
            !dni.text.toString().trim().isNullOrEmpty()             &&
            dni.text.toString().trim().count() == resources.getInteger(R.integer.cantidad_documento_max)             &&
            !fecha.text.toString().trim().isNullOrEmpty())
        {
            //Registrar prestamo
            registerButton.apply {
                isEnabled = true
                backgroundTintMode = PorterDuff.Mode.SCREEN
                backgroundTintList= ContextCompat.getColorStateList(context,R.color.colorPrimary)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }else
        {
            registerButton.apply {
                isEnabled = false
                backgroundTintMode = PorterDuff.Mode.MULTIPLY
                backgroundTintList= ContextCompat.getColorStateList(context,R.color.color_input_text)
                setTextColor(ContextCompat.getColor(context, R.color.color_input_text))
            }

        }
    }


    private fun showCalendar() {
        binding.fechaLayout.setEndIconOnClickListener {
                getCalendar()
        }
    }



    @SuppressLint("SimpleDateFormat")
    private fun getCalendar() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleciona una fecha")
                .build()

        datePicker.show(supportFragmentManager, "Datepickerdialog");

        datePicker.addOnPositiveButtonClickListener {
            println("UnixTime: ${it}")
            fechaSelectedUnixtime = it
            SimpleDateFormat("dd/MM/yyyy").apply {
                timeZone = TimeZone.getTimeZone("GMT")
                format(it).toString().also {
                    binding.fecha.setText(it)
                }
            }

        }

    }



    private fun getExtras() {

        if(intent.extras != null)
        {
           var extras  = intent.getStringExtra("prestamoJson")
           var gson = Gson()

            if(!extras.isNullOrEmpty())
            {
                prestamoReceived = gson.fromJson(extras, Prestamo::class.java)
                binding.interes.setText("${prestamoReceived.interes!!.toInt()}%")
                binding.capital.setText("${getString(R.string.tipo_moneda)} ${prestamoReceived.capital!!.toInt()}")
                binding.plazosEnDias.setText("${prestamoReceived.plazo_vto.toString()} dias")
            }
        }

    }

    override fun onDestroy() {

        ViewModelRegister.destroyInstance()
        super.onDestroy()
    }

}