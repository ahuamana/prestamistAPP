package com.paparazziapps.pretamistapp.presentation.registro.views

import android.annotation.SuppressLint
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
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.presentation.registro.viewmodels.ViewModelRegister
import java.text.SimpleDateFormat
import java.util.*
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.domain.PAConstants
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegistrarActivity : AppCompatActivity() {

    private val viewModel by viewModel<ViewModelRegister>()
    val _viewModelBranches: ViewModelBranches  by viewModel()

    lateinit var binding: ActivityRegistrarBinding
    var loanDomainReceived = LoanDomain()
    lateinit var layoutFecha:TextInputLayout
    lateinit var layoutNombres:TextInputLayout
    lateinit var layoutApellidos:TextInputLayout
    lateinit var layoutDNI:TextInputLayout
    lateinit var layoutCelular:TextInputLayout

    lateinit var registerButton:MaterialButton
    lateinit var toolbar: Toolbar
    var fechaSelectedUnixtime:Long? = null

    private val preferences: MyPreferences by inject()

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
        binding.dni.setMaxLength(resources.getInteger(R.integer.cantidad_documento_max))
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
            _viewModelBranches.getBranches()
        }
    }

    private fun startObservers() {
        viewModel.getMessage().observe(this){ message ->  showMessage(message)}

        _viewModelBranches.branches.observe(this){
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

                val loanDomain = LoanDomain(
                    names     = binding.nombres.text.toString().trim(),
                    lastnames   = binding.apellidos.text.toString().trim(),
                    dni         = binding.dni.text.toString().trim(),
                    cellular     = binding.celular.text.toString().trim(),
                    fecha_start_loan       = binding.fecha.text.toString().trim(),
                    unixtime    = fechaSelectedUnixtime,
                    unixtimeRegistered = getFechaActualNormalInUnixtime(),
                    capital     = loanDomainReceived.capital,
                    interest     = loanDomainReceived.interest,
                    quotasPaid = 0,
                    amountPerQuota = loanDomainReceived.amountPerQuota,
                    totalAmountToPay = loanDomainReceived.totalAmountToPay,
                    state = "ABIERTO",
                    //fields new version 2.0
                    typeLoan = loanDomainReceived.typeLoan,
                    typeLoanDays = loanDomainReceived.typeLoanDays,
                    typeLoanName = loanDomainReceived.typeLoanName,
                    quotas = loanDomainReceived.quotas // Only for other type of loans like weekly, biweekly, monthly
                )

                var idSucursalSelected:Int = INT_DEFAULT

                listaSucursales.forEach {
                    if(it.name?.equals(sucursalTxt.text.toString().trim()) == true) idSucursalSelected = it.id?: INT_DEFAULT
                }

                //Register ViewModel
                //Actualizar el idSucursal para crear un prestamo como superAdmin
                viewModel.createPrestamo(loanDomain, idSucursal = idSucursalSelected){
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

        binding.fecha.doAfterTextChanged {
            showbutton()
        }

        binding.nombres.doAfterTextChanged { input->
            val names = input.toString()
            layoutNombres.error = when {
                names.isEmpty() -> "El nombre esta vacío"
                names.count() < 4 -> "El nombre esta incompleto"
                else -> null
            }
            showbutton()
        }

        binding.apellidos.doAfterTextChanged { input->
            val lastnames = input.toString()
            layoutApellidos.error = when {
                lastnames.isEmpty() -> "Los apellidos estan vacíos"
                lastnames.count() < 4 -> "Los apellidos estan incompletos"
                else -> null
            }
            showbutton()

        }

        binding.dni.doAfterTextChanged { input ->
            val document = input.toString()
            val documentMaxLength = resources.getInteger(R.integer.cantidad_documento_max)
            layoutDNI.error = when {
                document.isEmpty() -> getString(R.string.documento_vacío)
                document.count() in 1 until documentMaxLength -> getString(R.string.documento_incompleto)
                else -> null
            }
            showbutton()
        }

        binding.celular.doAfterTextChanged {
            val cellular = it.toString()
            layoutCelular.error = when {
                cellular.isEmpty() -> "Celular vacío"
                cellular.count() in 1..8 -> "Celular incompleto"
                else -> null
            }
            showbutton()
        }


    }

    private fun showbutton() {
        if(!binding.nombres.text.toString().trim().isNullOrEmpty()          &&
            binding.nombres.text.toString().trim().count() >= 4             &&
            !binding.apellidos.text.toString().trim().isNullOrEmpty()       &&
            binding.apellidos.text.toString().trim().count() >= 4           &&
            !binding.celular.text.toString().trim().isNullOrEmpty()         &&
            binding.celular.text.toString().trim().count() == 9             &&
            !binding.dni.text.toString().trim().isNullOrEmpty()             &&
            binding.dni.text.toString().trim().count() == resources.getInteger(R.integer.cantidad_documento_max)             &&
            !binding.fecha.text.toString().trim().isNullOrEmpty())
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
        if(intent.extras != null) {
           val extras  = intent.getStringExtra(PAConstants.EXTRA_LOAN_JSON)
           val gson = Gson()

            if(!extras.isNullOrEmpty()) {
                loanDomainReceived = gson.fromJson(extras, LoanDomain::class.java)
                binding.capital.setText("${getString(R.string.tipo_moneda)} ${loanDomainReceived.capital!!.toInt()}")
                val typeLoanDisplayName = PaymentScheduled.getPaymentScheduledById(loanDomainReceived.typeLoan?: INT_DEFAULT).displayName
                binding.typeLoan.setText(typeLoanDisplayName)

                //New fields for the new version 2.0
                val typeLoan = PaymentScheduled.getPaymentScheduledById(loanDomainReceived.typeLoan?: INT_DEFAULT)
                when(typeLoan) {
                    PaymentScheduledEnum.DAILY -> {
                        binding.containerDaily.beVisible()
                        binding.containerOther.beGone()
                        binding.plazosEnDias.setText("${loanDomainReceived.quotas.toString()} dias")
                        binding.interes.setText("${loanDomainReceived.interest!!.toInt()}%")

                    }
                    else -> {
                        binding.containerDaily.beGone()
                        binding.containerOther.beVisible()
                        binding.interesOther.setText("${loanDomainReceived.interest!!.toInt()}%")
                        binding.quotasOther.setText(loanDomainReceived.quotas.toString())
                    }
                }
            }
        }

    }
}