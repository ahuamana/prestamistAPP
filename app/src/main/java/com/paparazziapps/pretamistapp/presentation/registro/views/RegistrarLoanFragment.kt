package com.paparazziapps.pretamistapp.presentation.registro.views

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.presentation.registro.viewmodels.ViewModelRegister
import java.text.SimpleDateFormat
import java.util.*
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.databinding.FragmentRegistrarLoanBinding
import com.paparazziapps.pretamistapp.domain.PAConstants
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.presentation.registro.viewmodels.RegisterState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegistrarLoanFragment : Fragment() {

    private val viewModel by viewModel<ViewModelRegister>()
    val _viewModelBranches: ViewModelBranches  by viewModel()

    private var _binding: FragmentRegistrarLoanBinding? = null
    private val binding get() = _binding!!
    var loanDomainReceived = LoanDomain()
    lateinit var layoutFecha:TextInputLayout
    lateinit var layoutDNI:TextInputLayout
    var fechaSelectedUnixtime:Long? = null

    private val preferences: MyPreferences by inject()

    //Sucursales Supér Admin
    var listaSucursales = mutableListOf<Sucursales>()
    lateinit var sucursalTxt:AutoCompleteTextView
    lateinit var sucursalTxtLayout:TextInputLayout
    lateinit var viewProgressSucursal: View
    lateinit var viewCurtainSucursal: View
    lateinit var viewDotsSucursal: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrarLoanBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutFecha         = binding.fechaLayout
        layoutDNI           = binding.dniLayout

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

        //Observers
        setupObservers()

        setupButtons()
    }



    private fun fieldsSuperAdmin() {
        if(preferences.isSuperAdmin){
            sucursalTxtLayout.beVisible()
            viewProgressSucursal.beVisible()
            _viewModelBranches.getBranches()
        }
    }

    private fun setupObservers() {
        _viewModelBranches.branches.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                listaSucursales = it.toMutableList()
                val scrsales = mutableListOf<String>()
                it.forEach {
                    scrsales.add(it.name?:"")
                }

                val adapterSucursales= ArrayAdapter(requireContext(),R.layout.select_items, scrsales)
                sucursalTxt.setAdapter(adapterSucursales)
                sucursalTxt.setOnClickListener { sucursalTxt.showDropDown() }
                sucursalTxtLayout.setEndIconOnClickListener { sucursalTxt.showDropDown() }

                viewProgressSucursal.isVisible = false
                viewDotsSucursal.isVisible = false
                viewCurtainSucursal.isVisible = false
            }
        }

        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collect(::stateHandler)
        }
    }

    private fun stateHandler(state: RegisterState) {
        when(state) {
            RegisterState.Loading -> {
                binding.cortina.isVisible = true
            }
            is RegisterState.Success -> {
                binding.cortina.isVisible = false
                viewModel.resetState()
                createIntentSuccess(state.message)
            }
            is RegisterState.Error -> {
                binding.cortina.isVisible = false
                viewModel.resetState()
                createIntentSuccess(state.message)
            }

            RegisterState.Idle -> {
                binding.cortina.isVisible = false
            }

        }
    }

    private fun showMessage(message: String?) {
        Snackbar.make(binding.root,"${message}",Snackbar.LENGTH_SHORT).show()
    }

    private fun setupButtons() {
        binding.registrarButton.setOnClickListener {
           handledRegister()
        }
    }

    private fun handledRegister() {
        hideKeyboardActivity(requireActivity())



        val document = binding.dni.text.toString().trim()
        val documentMaxLength = resources.getInteger(R.integer.cantidad_documento_max)

        binding.dniLayout.error = when {
            document.isEmpty() -> "Documento vacío"
            document.count() in 1 until documentMaxLength -> "Documento incompleto"
            else -> null
        }

        val date = binding.fecha.text.toString().trim()

        binding.fechaLayout.error = when {
            date.isEmpty() -> "Fecha vacía"
            else -> null
        }


        if (fechaSelectedUnixtime == null) return


        //Finish validation

        val loanDomain = LoanDomain(
            names     = "names",
            lastnames   = "lastnames",
            dni         = document,
            cellular     = "cellular",
            email = "email",
            loanStartDateFormatted       = date,
            loanStartDateUnix    = fechaSelectedUnixtime,
            loanCreationDateUnix = getFechaActualNormalInUnixtime(),
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

        viewModel.createLoan(loanDomain, branchId = idSucursalSelected)
    }

    private fun createIntentSuccess(msj:String) {
        /*intent.putExtra("mensaje", msj)
        setResult(RESULT_OK, intent)
        finish()*/
        TODO("Not yet implemented")
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

        datePicker.show(requireActivity().supportFragmentManager, "Datepickerdialog");

        datePicker.addOnPositiveButtonClickListener {
            Log.d("UNIXTIME","UnixTime: ${it}")
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
        val intent = requireActivity().intent //TODO: Revisar si se debe cambiar por requireActivity().intent
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