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
import androidx.navigation.fragment.findNavController
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.databinding.FragmentLoanRegistrarBinding
import com.paparazziapps.pretamistapp.domain.PAConstants
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.presentation.clients.ClientsAddViewModel
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.presentation.registro.viewmodels.RegisterState
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegistrarLoanFragment : Fragment() {

    private val viewModel by viewModel<ViewModelRegister>()
    val _viewModelBranches: ViewModelBranches  by viewModel()

    private var _binding: FragmentLoanRegistrarBinding? = null
    private val binding get() = _binding!!
    lateinit var layoutFecha:TextInputLayout
    var fechaSelectedUnixtime:Long? = null

    private val preferences: MyPreferences by inject()

    private val generalErrorDialog by lazy {
        PADialogFactory(requireContext()).createGeneralErrorDialog(
            onRetryClick = {
                handledErrorDialog()
            }
        )
    }

    private fun handledErrorDialog() {
        generalErrorDialog.dismiss()
    }

    private val generalSuccessDialog by lazy {
        PADialogFactory(requireContext()).createGeneralSuccessDialog(
            successMessage = getString(R.string.operation_success_message),
            buttonTitle = getString(R.string.continue_button_message),
            onConfirmClick = {
                findNavController().navigate(R.id.action_navigation_register_loan_to_navigation_home)
            }
        )
    }

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
        _binding = FragmentLoanRegistrarBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutFecha         = binding.fechaLayout

        //SuperAdmin
        sucursalTxt         = binding.edtSucursal
        sucursalTxtLayout   = binding.sucursalTxtInputLyt

        viewProgressSucursal = binding.progressSucursal
        viewDotsSucursal     = binding.dotsSucursal
        viewCurtainSucursal  = binding.curtainSucursal

        fieldsSuperAdmin()

        //Set max lengh Document
        binding.document.setMaxLength(resources.getInteger(R.integer.cantidad_documento_max))
        binding.documentLayout.counterMaxLength = resources.getInteger(R.integer.cantidad_documento_max)

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

        lifecycleScope.launch {
            viewModel.stateUserInfo.collect { client ->
                client?.let {
                    binding.names.setText(it.name)
                    binding.lastNames.setText(it.lastName)
                    binding.document.setText(it.document)
                    binding.celular.setText(it.phone)
                    binding.email.setText(it.email)
                }
            }
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
                generalSuccessDialog.show()
            }
            is RegisterState.Error -> {
                binding.cortina.isVisible = false
                viewModel.resetState()
                generalErrorDialog.show()
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



        val document = binding.document.text.toString().trim()
        val documentMaxLength = resources.getInteger(R.integer.cantidad_documento_max)

        binding.documentLayout.error = when {
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

        val client = viewModel.getClientSelected()
        val loanInfo = viewModel.getLoanInformationDomain()

        val loanDomain = LoanDomain(
            names     = client?.name,
            lastnames   = client?.lastName,
            dni         = document,
            cellular     = client?.phone,
            email = client?.email,
            loanStartDateFormatted = date,
            loanStartDateUnix    = fechaSelectedUnixtime,
            loanCreationDateUnix = getFechaActualNormalInUnixtime(),
            capital     = loanInfo?.capital,
            interest     = loanInfo?.interest,
            quotasPaid = 0,
            amountPerQuota = loanInfo?.amountPerQuota,
            totalAmountToPay = loanInfo?.totalAmountToPay,
            state = "ABIERTO",
            //fields new version 2.0
            typeLoan = loanInfo?.typeLoan,
            typeLoanDays = loanInfo?.typeLoanDays,
            typeLoanName = loanInfo?.typeLoanName,
            quotas = loanInfo?.quotas // Only for other type of loans like weekly, biweekly, monthly
        )

        var idSucursalSelected:Int = INT_DEFAULT

        listaSucursales.forEach {
            if(it.name?.equals(sucursalTxt.text.toString().trim()) == true) idSucursalSelected = it.id?: INT_DEFAULT
        }

        viewModel.createLoan(loanDomain, branchId = idSucursalSelected)
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
        val loanInfo = viewModel.getLoanInformationDomain()

        loanInfo?.let { loan->
            binding.capital.setText("${getString(R.string.tipo_moneda)} ${loan.capital}")
            val typeLoanDisplayName = PaymentScheduled.getPaymentScheduledById(loan.typeLoan?: INT_DEFAULT).displayName
            binding.typeLoan.setText(typeLoanDisplayName)

            //New fields for the new version 2.0
            val typeLoan = PaymentScheduled.getPaymentScheduledById(loan.typeLoan?: INT_DEFAULT)
            when(typeLoan) {
                PaymentScheduledEnum.DAILY -> {
                    binding.containerDaily.beVisible()
                    binding.containerOther.beGone()
                    binding.plazosEnDias.setText("${loan.quotas.toString()} dias")
                    binding.interes.setText("${loan.interest!!.toInt()}%")

                }
                else -> {
                    binding.containerDaily.beGone()
                    binding.containerOther.beVisible()
                    binding.interesOther.setText("${loan.interest!!.toInt()}%")
                    binding.quotasOther.setText(loan.quotas.toString())
                }
            }
        }

    }
}