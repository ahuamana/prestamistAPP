package com.paparazziapps.pretamistapp.presentation.registro.views

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentRegistrarBinding
import com.paparazziapps.pretamistapp.domain.PAConstants
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.presentation.registro.viewmodels.ViewModelRegister
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrarFragment : Fragment() {

    private var _binding: FragmentRegistrarBinding ?= null
    private val binding get() = _binding!!

    private val viewModel by viewModel<ViewModelRegister>()

    //Variables
    lateinit var modos: AppCompatAutoCompleteTextView
    lateinit var modoLayout: TextInputLayout
    lateinit var intereses: AppCompatAutoCompleteTextView
    lateinit var intereslayout: TextInputLayout
    lateinit var plazos: AppCompatAutoCompleteTextView
    lateinit var plazosLayout: TextInputLayout
    lateinit var capitalPrestado: TextInputEditText
    lateinit var btnContinuar: MaterialButton

    lateinit var interesesP: TextInputEditText
    lateinit var intereslayoutP: TextInputLayout
    lateinit var mesesP: TextInputEditText
    lateinit var meseslayoutP: TextInputLayout
    lateinit var montoDiario: MaterialTextView
    lateinit var montoTotal: MaterialTextView

    var isValidInteres = false
    var isValidInteresP = false
    var isValidMeses = false
    var isValidMesesP = false
    var isValidCapitalPrestado = false

    var mesesEntero:Int = 0
    var capitalEntero:Int = 0
    var interesEntero:Int = 0
    var montoDiarioAPagar:Double = 0.0
    var montoTotalAPagar:Double = 0.0
    var loanDomain = LoanDomain()

    //Layout
    private val listaIntereses = arrayListOf<String>("8%","10%","20%","30%","40%","50%")
    private val listaPlazos = arrayListOf<String>("30 dias","60 dias","90 dias","120 dias","180 dias")
    private val listTypePaymentScheduled: ArrayList<String> = PaymentScheduled.getPaymentScheduledListString()
    private val listmode = arrayListOf(M_STANDAR, M_PERSONALIZADO)

    //Result for activity
    val startForResult  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK) {
            val msj = it.data?.getStringExtra("mensaje")
            // Handle the Intent
            Log.d(tag,"Resultado de actividad: $msj")
            showMessage(msj?:"")
        }else {
            Log.d(tag,"Resultado de actividad--> null")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrarBinding.inflate(inflater, container, false)
        val view = binding.root

        //Modo Standar
        intereses = binding.interesSP
        intereslayout = binding.interesLayout
        plazos = binding.plazos
        plazosLayout = binding.plazosLayout
        capitalPrestado = binding.capitalprestadoEdt
        montoDiario = binding.montoDiario
        montoTotal = binding.montoTotal

        //Modo Personalizado
        intereslayoutP = binding.interesLayoutP
        interesesP = binding.interesSPP

        meseslayoutP=binding.plazosLayoutP
        mesesP=binding.plazosP

        btnContinuar = binding.continuarButton
        modos = binding.mode
        modoLayout = binding.modeLayout


        setupSpinners()
        observers()
        validateAll()
        continuar()

        redondear()

        return view
    }

    private fun redondear() {
        binding.cardviewMontoDiario.setOnClickListener {
            val newMontoDiario = Math.ceil(montoDiarioAPagar)
            montoDiario.setText("${getString(R.string.tipo_moneda)} ${getDoubleWithTwoDecimals(newMontoDiario)}")
            montoDiarioAPagar = getDoubleWithTwoDecimalsReturnDouble(newMontoDiario)?:0.0

            montoTotalAPagar = getDoubleWithTwoDecimalsReturnDouble(newMontoDiario* mesesEntero)?:0.0
            montoTotal.setText("${getString(R.string.tipo_moneda)} ${getDoubleWithTwoDecimals(newMontoDiario *mesesEntero)}")
        }
    }

    private fun continuar() {
        btnContinuar.setOnClickListener {

            loanDomain.capital = binding.capitalprestadoEdt.text.toString().toInt()

            loanDomain.amountPerQuota = montoDiarioAPagar
            loanDomain.totalAmountToPay = montoTotalAPagar
            //fields new version 2.0

            val mode = binding.modePaymentScheduled.text.toString()
            val paymentScheduled = PaymentScheduled.getPaymentScheduledByName(mode)
            if(paymentScheduled == PaymentScheduledEnum.DAILY) {
                loanDomain.quotas = mesesEntero
                loanDomain.interest = interesEntero
            } else {
                loanDomain.interest = binding.interesExtras.text.toString().toIntOrNull()?:0
                //multiply the quotas and the type of loan days
                val quotas = binding.quotasTextExtras.text.toString().toIntOrNull()?:0
                loanDomain.quotas = quotas
            }

            loanDomain.typeLoan = PaymentScheduled.getPaymentScheduledByName(binding.modePaymentScheduled.text.toString()).id
            loanDomain.typeLoanDays = PaymentScheduled.getPaymentScheduledByName(binding.modePaymentScheduled.text.toString()).days
            loanDomain.typeLoanName = binding.modePaymentScheduled.text.toString()

            val gson = Gson()
            val loanJson = gson.toJson(loanDomain)

            //Show next activity - Register pagos
            val intent = Intent(context, RegistrarActivity::class.java).apply {
                putExtra(PAConstants.EXTRA_LOAN_JSON,loanJson)
            }
            startForResult.launch(intent)
        }
    }

    private fun validateAll() {

        intereses.doAfterTextChanged {
            if(intereses.text.toString() != "" && intereses.text != null) {
                isValidInteres = true
                validateFields()
            }else {
                Log.e("TAG","intereses: ${intereses.text}")
                isValidInteres = false
            }
        }

        plazos.doAfterTextChanged {

            if(plazos.text.toString() != "" && plazos.text != null) {
                isValidMeses = true
                validateFields()
            }else {
                Log.e("TAG","meses: ${plazos.text}")
                isValidMeses = false
            }
        }

        capitalPrestado.doAfterTextChanged {
            Log.e("TAG","Capital prestado: ${capitalPrestado.text}")

            if(capitalPrestado.text!!.length < 9) {
                if(capitalPrestado.text?.trim().toString() != "") {
                    isValidCapitalPrestado = true
                    validateFields()
                }
            }else {
                isValidCapitalPrestado = false
                validateFields()
            }
        }

        interesesP.doAfterTextChanged {

            if(interesesP.text.toString() != "" && interesesP.text != null)
            {
                isValidInteresP = true
                validateFields()
            }else
            {
                Log.e("TAG","interesesP: ${interesesP.text}")
                isValidInteresP = false
            }

        }

        mesesP.doAfterTextChanged {

            if(mesesP.text.toString() != "" && mesesP.text != null)
            {
                isValidMesesP = true
                validateFields()
            }else
            {
                Log.e("TAG","MesesP: ${mesesP.text}")
                isValidMesesP = false
            }
        }

        binding.interesExtras.doAfterTextChanged {

            val interest = binding.interesExtras.text.toString()
            val quotas = binding.quotasTextExtras.text.toString()
            val capital = binding.capitalprestadoEdt.text.toString()

            if(interest.isEmpty()) return@doAfterTextChanged
            if(quotas.isEmpty()) return@doAfterTextChanged
            if(capital.isEmpty()) return@doAfterTextChanged
            validateFields ()
        }

        binding.quotasTextExtras.doAfterTextChanged {
            val interest = binding.interesExtras.text.toString()
            val quotas = binding.quotasTextExtras.text.toString()
            val capital = binding.capitalprestadoEdt.text.toString()

            if(interest.isEmpty()) return@doAfterTextChanged
            if(quotas.isEmpty()) return@doAfterTextChanged
            if(capital.isEmpty()) return@doAfterTextChanged

            validateFields ()
        }



        //Validate Mode
        modos.doAfterTextChanged {
            when (modos.text.toString()){
                M_STANDAR -> {
                    binding.apply {
                        layoutdefecto.beVisible()
                        layoutPersonalizado.beGone()
                        plazos.beVisible()
                        plazosLayoutP.beGone()
                        modeLayout.beVisible()
                    }
                    validateFields()
                }

                M_PERSONALIZADO ->{
                    binding.apply {
                        layoutdefecto.beGone()
                        layoutPersonalizado.beVisible()
                        plazos.beGone()
                        layoutParentExtras.beGone()
                        plazosLayoutP.beVisible()
                        modeLayout.beVisible()
                    }
                    clearData()
                    validateFields()
                }
                M_EXTRA -> {
                    binding.apply {
                        modeLayout.beGone()
                        plazosLayoutP.beGone()
                        layoutdefecto.beGone()
                        layoutPersonalizado.beGone()
                        layoutParentExtras.beVisible()
                    } // Only works for weekly, fortnightly, monthly, bimonthly, quarterly, semiannual, annual
                }

                else -> { showMessage("Error cambiando de modo")}
            }
        }


        //Validate pago programado
        binding.modePaymentScheduled.doAfterTextChanged {

            val text = binding.modePaymentScheduled.text.toString()
            val paymentScheduled = PaymentScheduled.getPaymentScheduledByName(text)
            clearData()

            when(paymentScheduled) {
                PaymentScheduledEnum.DAILY -> {
                    binding.apply {
                        if(modos.text.toString() == M_STANDAR) {
                            layoutPersonalizado.beGone()
                            layoutdefecto.beVisible()
                            modeLayout.beVisible()
                        }else {
                            layoutdefecto.beGone()
                            layoutPersonalizado.beVisible()
                            modeLayout.beVisible()
                        }
                        layoutParentExtras.beGone()
                    }
                    viewModel.setDailyStringMode(paymentScheduled.displayName)
                    clearData()
                }

                PaymentScheduledEnum.WEEKLY,
                PaymentScheduledEnum.FORTNIGHTLY,
                PaymentScheduledEnum.MONTHLY,
                PaymentScheduledEnum.BIMONTHLY,
                PaymentScheduledEnum.QUARTERLY,
                PaymentScheduledEnum.SEMIANNUAL,
                PaymentScheduledEnum.ANNUAL -> {
                    binding.apply {
                        layoutPersonalizado.beGone()
                        layoutdefecto.beGone()
                        modeLayout.beGone()
                        layoutParentExtras.beVisible()
                    }
                    viewModel.setDailyStringMode(paymentScheduled.displayName)
                    clearData()
                }
            }
        }

    }

    private fun setupSpinners() {

        val adapterIntereses= ArrayAdapter<String>(requireContext(),R.layout.select_items, listaIntereses)
        val adapterPlazos= ArrayAdapter<String>(requireContext(),R.layout.select_items, listaPlazos)
        val adapterModos= ArrayAdapter<String>(requireContext(),R.layout.select_items, listmode)
        val adapterModePaymentScheduled = ArrayAdapter(requireContext(),R.layout.select_items, listTypePaymentScheduled)

        intereses.setAdapter(adapterIntereses)
        plazos.setAdapter(adapterPlazos)
        modos.setAdapter(adapterModos)
        binding.modePaymentScheduled.setAdapter(adapterModePaymentScheduled)


        intereslayout.setEndIconOnClickListener { intereses.showDropDown() }
        intereses.setOnClickListener { intereses.showDropDown() }
        plazos.setOnClickListener { plazos.showDropDown() }
        plazosLayout.setEndIconOnClickListener { plazos.showDropDown() }
        modoLayout.setEndIconOnClickListener { modos.showDropDown() }
        modos.setOnClickListener { modos.showDropDown() }
        binding.modePaymentScheduled.setOnClickListener { binding.modePaymentScheduled.showDropDown() }
    }

    private fun validateFields () {
        val text = binding.modePaymentScheduled.text.toString()
        if(text.isEmpty()) return
        val paymentScheduled = PaymentScheduled.getPaymentScheduledByName(text)
        if(paymentScheduled == PaymentScheduledEnum.DAILY) {
            validateDaily()
        } else {
            validateExtras()
        }
    }

    private fun validateExtras(){
        val interest = binding.interesExtras.text.toString()
        val quotas = binding.quotasTextExtras.text.toString()
        val capital = binding.capitalprestadoEdt.text.toString()

        if(interest.isEmpty()) return
        if(quotas.isEmpty()) return
        if(capital.isEmpty()) return

        calcularTodo(M_EXTRA)
        activateContinuar(true)
    }

    private fun validateDaily() {
        when (modos.text.toString()) {
            M_STANDAR -> {
                if (isValidInteres && isValidMeses && isValidCapitalPrestado) {
                    calcularTodo(M_STANDAR)
                    activateContinuar(true)
                } else {
                    activateContinuar(false)
                }
            }

            M_PERSONALIZADO -> {
                if (isValidInteresP && isValidMesesP && isValidCapitalPrestado) {
                    calcularTodo(M_PERSONALIZADO)
                    activateContinuar(true)
                } else {
                    activateContinuar(false)
                }
            }

            else -> showMessage("Error validando datos")
        }
    }

    private fun activateContinuar(isValidEverything: Boolean) {
        if(isValidEverything) {
            btnContinuar.backgroundTintMode = PorterDuff.Mode.SCREEN
            btnContinuar.backgroundTintList= ContextCompat.getColorStateList(requireContext(),R.color.primary)
            btnContinuar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            btnContinuar.isEnabled = true
        } else {
            btnContinuar.isEnabled = false
            btnContinuar.backgroundTintMode = PorterDuff.Mode.MULTIPLY
            btnContinuar.backgroundTintList= ContextCompat.getColorStateList(requireContext(),R.color.color_input_text)
            btnContinuar.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_input_text))

        }
    }

    private fun calcularTodo(mode: String) {
        when (mode) {
            M_STANDAR -> {
                capitalEntero = capitalPrestado.text.toString().trim().toInt()
                interesEntero = intereses.text.substring(0,intereses.text.length-1).toInt()
                mesesEntero = plazos.text.substring(0,plazos.text.length-5).toInt()
                viewModel.calcularMontoDiario(capitalEntero,interesEntero,mesesEntero)
            }
            M_PERSONALIZADO -> {
                capitalEntero = capitalPrestado.text.toString().trim().toInt()
                interesEntero = interesesP.text.toString().toInt()
                mesesEntero = mesesP.text.toString().toInt()
                viewModel.calcularMontoDiario(capitalEntero,interesEntero,mesesEntero)
            }

            M_EXTRA -> {
                val capitalInteger = binding.capitalprestadoEdt.text.toString().toInt()
                val interestExtras = binding.interesExtras.text.toString().toInt()
                val quotasExtras = binding.quotasTextExtras.text.toString().toInt()
                viewModel.calcularMontoDiario(capitalInteger,interestExtras,quotasExtras)
            }

            else -> showMessage("No se pudo procesar tu solicitud")
        }

    }

    private fun observers() {

        viewModel.getMessage().observe(viewLifecycleOwner) { message ->
            if(message!= null)  showMessage(message)
        }

        viewModel.getMontoDiario().observe(viewLifecycleOwner){ montodiario ->
            if(montodiario !=null) {
                Log.d(tag, "Monto Diario: $montodiario")
                //Asginar datos a variables globales
                val amountDaily = getDoubleWithTwoDecimalsReturnDouble(montodiario)
                val amountTotal = calculateTotalAmountToPay(amountDaily)
                montoDiarioAPagar = amountDaily

                montoTotalAPagar = amountTotal

                montoDiario.setText("${getString(R.string.tipo_moneda)} $amountDaily")
                montoTotal.setText("${getString(R.string.tipo_moneda)} $amountTotal")
            }else {
                Log.d(tag, "Monto diario es 0")
            }
        }

        lifecycleScope.launch{
            viewModel.dailyStringMode.collectLatest {
                //bring string resource
                val descriptionWithArgument = getString(R.string.monto_a_pagar_daily_with_argument, it)
                binding.dailyAmountTitle.text = descriptionWithArgument
            }
        }
    }

    private fun calculateTotalAmountToPay(dailyAmount: Double): Double {
        val text = binding.modePaymentScheduled.text.toString()
        val paymentScheduled = PaymentScheduled.getPaymentScheduledByName(text)
        if(paymentScheduled == PaymentScheduledEnum.DAILY) {
            return getDoubleWithTwoDecimalsReturnDouble(dailyAmount * mesesEntero)
        } else {
            val quotas = binding.quotasTextExtras.text.toString().toInt()
            return getDoubleWithTwoDecimalsReturnDouble(dailyAmount * quotas)
        }
    }


    private fun clearData() {
        mesesP.setText("")
        interesesP.setText("")
        binding.apply {
            quotasTextExtras.setText("")
            plazosP.setText("")
            plazos.setText("")
        }
        montoDiario.setText("${getString(R.string.tipo_moneda_defecto_cero)}")
        montoTotal.setText("${getString(R.string.tipo_moneda_defecto_cero)}")

    }

    private fun showMessage(message:String) {
        showMessageAboveMenuInferiorGlobal(message,binding.root)
    }

    companion object {

        const val M_PERSONALIZADO = "Personalizado"
        const val M_STANDAR = "Estándar"
        const val M_EXTRA = "Extra"
    }

    override fun onResume() {
        binding.root.hideKeyboardFrom()
        super.onResume()
    }
}