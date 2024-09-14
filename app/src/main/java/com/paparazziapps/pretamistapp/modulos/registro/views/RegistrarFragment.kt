package com.paparazziapps.pretamistapp.modulos.registro.views

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
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentRegistrarBinding
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.modulos.registro.pojo.PaymentScheduled
import com.paparazziapps.pretamistapp.modulos.registro.pojo.PaymentScheduledEnum
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister

class RegistrarFragment : Fragment() {

    var _binding: FragmentRegistrarBinding ?= null
    private val binding get() = _binding!!

    var _viewModel = ViewModelRegister.getInstance()

    //Variables
    lateinit var modos: AppCompatAutoCompleteTextView
    lateinit var modoLayout: TextInputLayout
    lateinit var intereses: AppCompatAutoCompleteTextView
    lateinit var intereslayout: TextInputLayout
    lateinit var meses: AppCompatAutoCompleteTextView
    lateinit var meseslayout: TextInputLayout
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
    var prestamo = Prestamo()

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
            println("Resultado de actividad: $msj")
            showMessage(msj?:"")
        }else
        {
            println("Resultado de actividad--> null")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
        meses = binding.plazos
        meseslayout = binding.plazosLayout
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
            var newMontoDiario = Math.ceil(montoDiarioAPagar)
            montoDiario.setText("${getString(R.string.tipo_moneda)} ${getDoubleWithTwoDecimals(newMontoDiario)}")
            montoDiarioAPagar = getDoubleWithTwoDecimalsReturnDouble(newMontoDiario)?:0.0

            montoTotalAPagar = getDoubleWithTwoDecimalsReturnDouble(newMontoDiario* mesesEntero)?:0.0
            montoTotal.setText("${getString(R.string.tipo_moneda)} ${getDoubleWithTwoDecimals(newMontoDiario *mesesEntero)}")
        }
    }

    private fun continuar() {
        btnContinuar.setOnClickListener {

            prestamo.capital = capitalEntero
            prestamo.interes = interesEntero
            prestamo.plazo_vto = mesesEntero
            prestamo.montoDiarioAPagar = montoDiarioAPagar
            prestamo.montoTotalAPagar = montoTotalAPagar

            val gson = Gson()
            val prestamoJson = gson.toJson(prestamo)

            //Show next activity - Register pagos
            startForResult.launch(Intent(context, RegistrarActivity::class.java).putExtra("prestamoJson",prestamoJson))
        }
    }

    private fun validateAll() {

        intereses.doAfterTextChanged {

            if(intereses.text.toString() != "" && intereses.text != null)
            {
                isValidInteres = true
                validateFields()
            }else
            {
                Log.e("TAG","intereses: ${intereses.text}")
                isValidInteres = false
            }
        }

        meses.doAfterTextChanged {

            if(meses.text.toString() != "" && meses.text != null)
            {
                isValidMeses = true
                validateFields()
            }else
            {
                Log.e("TAG","meses: ${meses.text}")
                isValidMeses = false
            }
        }

        capitalPrestado.doAfterTextChanged {
            Log.e("TAG","Capital prestado: ${capitalPrestado.text}")

            if(capitalPrestado.text!!.length < 9)
            {
                if(capitalPrestado.text?.trim().toString() != "")
                {
                    isValidCapitalPrestado = true
                    validateFields()
                }
            }else
            {
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



        //Validate Mode
        modos.doAfterTextChanged {

            when (modos.text.toString()){
                M_STANDAR -> {
                    binding.layoutdefecto.isVisible = true
                    binding.layoutPersonalizado.isVisible = false
                    validateFields()
                }

                M_PERSONALIZADO ->{
                    binding.layoutdefecto.isVisible = false
                    binding.layoutPersonalizado.isVisible = true
                    clearData()
                    validateFields()

                }

                else -> { showMessage("Error cambiando de modo")}

            }
        }

        //Validate pago programado

        binding.modePaymentScheduled.doAfterTextChanged {

            val text = binding.modePaymentScheduled.text.toString()
            val paymentScheduled = PaymentScheduled.getPaymentScheduledByName(text)

            when(paymentScheduled)
            {
                PaymentScheduledEnum.DAILY -> {
                    meses.setText("30 dias")
                    meses.isEnabled = false
                }

                PaymentScheduledEnum.WEEKLY -> {
                    meses.setText("7 dias")
                    meses.isEnabled = false
                }

                PaymentScheduledEnum.FORTNIGHTLY -> {
                    meses.setText("15 dias")
                    meses.isEnabled = false
                }

                PaymentScheduledEnum.MONTHLY -> {
                    meses.setText("30 dias")
                    meses.isEnabled = false
                }

                PaymentScheduledEnum.BIMONTHLY -> {
                    meses.setText("60 dias")
                    meses.isEnabled = false
                }

                PaymentScheduledEnum.QUARTERLY -> {
                    meses.setText("90 dias")
                    meses.isEnabled = false
                }

                else -> {
                    meses.isEnabled = true
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
        meses.setAdapter(adapterPlazos)
        modos.setAdapter(adapterModos)
        binding.modePaymentScheduled.setAdapter(adapterModePaymentScheduled)


        intereslayout.setEndIconOnClickListener { intereses.showDropDown() }
        intereses.setOnClickListener { intereses.showDropDown() }
        meses.setOnClickListener { meses.showDropDown() }
        meseslayout.setEndIconOnClickListener { meses.showDropDown() }
        modoLayout.setEndIconOnClickListener { modos.showDropDown() }
        modos.setOnClickListener { modos.showDropDown() }
        binding.modePaymentScheduled.setOnClickListener { binding.modePaymentScheduled.showDropDown() }
    }

    private fun validateFields ()
    {
        when (modos.text.toString())
        {


            M_STANDAR -> {
                if(isValidInteres && isValidMeses && isValidCapitalPrestado)
                {
                    calcularTodo(M_STANDAR)
                    activateContinuar(true)
                }else
                {
                    activateContinuar(false)
                }
            }

            M_PERSONALIZADO -> {
                if(isValidInteresP && isValidMesesP && isValidCapitalPrestado)
                {
                    calcularTodo(M_PERSONALIZADO)
                    activateContinuar(true)
                }else
                {
                    activateContinuar(false)
                }
            }

            else -> showMessage("Error validando datos")

        }

    }

    private fun activateContinuar(isValidEverything: Boolean)
    {
        if(isValidEverything)
        {
            btnContinuar.backgroundTintMode = PorterDuff.Mode.SCREEN
            btnContinuar.backgroundTintList= ContextCompat.getColorStateList(requireContext(),R.color.colorPrimary)
            btnContinuar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            btnContinuar.isEnabled = true

        } else
        {
            btnContinuar.isEnabled = false
            btnContinuar.backgroundTintMode = PorterDuff.Mode.MULTIPLY
            btnContinuar.backgroundTintList= ContextCompat.getColorStateList(requireContext(),R.color.color_input_text)
            btnContinuar.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_input_text))

        }
    }

    private fun calcularTodo( mode: String) {

        when (mode)
        {
            M_STANDAR -> {
                capitalEntero = capitalPrestado.text.toString().trim().toInt()
                interesEntero = intereses.text.substring(0,intereses.text.length-1).toInt()
                mesesEntero = meses.text.substring(0,meses.text.length-5).toInt()
                _viewModel.calcularMontoDiario(capitalEntero,interesEntero,mesesEntero)
            }

            M_PERSONALIZADO -> {

                capitalEntero = capitalPrestado.text.toString().trim().toInt()
                interesEntero = interesesP.text.toString().toInt()
                mesesEntero = mesesP.text.toString().toInt()

                _viewModel.calcularMontoDiario(capitalEntero,interesEntero,mesesEntero)
            }

            else -> showMessage("No se pudo procesar tu solicitud")
        }

    }

    private fun observers() {

        _viewModel.getMessage().observe(viewLifecycleOwner) { message ->
            if(message!= null)  showMessage(message)
        }

        _viewModel.getMontoDiario().observe(viewLifecycleOwner){ montodiario ->
            if(montodiario !=null)
            {
                println("Monto Diario: $montodiario")
                //Asginar datos a variables globales
                montoDiarioAPagar = getDoubleWithTwoDecimalsReturnDouble(montodiario)?:0.0
                montoTotalAPagar = getDoubleWithTwoDecimalsReturnDouble(montodiario* mesesEntero)?:0.0

                montoDiario.setText("${getString(R.string.tipo_moneda)} ${getDoubleWithTwoDecimals(montodiario)}")
                montoTotal.setText("${getString(R.string.tipo_moneda)} ${getDoubleWithTwoDecimals(montodiario *mesesEntero)}")
            }else
            {
                println("Monto diario es 0")
            }
        }



    }

    private fun clearData()
    {
        mesesP.setText("")
        interesesP.setText("")
        montoDiario.setText("${getString(R.string.tipo_moneda_defecto_cero)}")
        montoTotal.setText("${getString(R.string.tipo_moneda_defecto_cero)}")

    }

    private fun showMessage(message:String)
    {
        showMessageAboveMenuInferiorGlobal(message,binding.root)
        //Snackbar.make(requireActivity().findViewById(R.id.nav_view),"$message",Snackbar.LENGTH_LONG).show()
    }


    companion object {

        const val M_PERSONALIZADO = "Personalizado"
        const val M_STANDAR = "Estándar"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrarFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onResume() {
        binding.root.hideKeyboardFrom()
        super.onResume()
    }

    override fun onDestroy() {
        ViewModelRegister.destroyInstance()
        super.onDestroy()
    }
}