package com.paparazziapps.pretamistapp.modulos.registro.views

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityCalcularBinding
import com.paparazziapps.pretamistapp.databinding.ActivityRegistrarBinding
import com.paparazziapps.pretamistapp.databinding.FragmentRegistrarBinding
import com.paparazziapps.pretamistapp.helper.getDoubleWithTwoDecimals
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
    var capitalDouble:Double = 0.00
    var interesDouble:Double = 0.00
    var prestamo = Prestamo()

    //Layout
    val listaIntereses = arrayListOf<String>("8%","10%","20%","30%","40%","50%")
    val listaPlazos = arrayListOf<String>("30 dias","60 dias","90 dias","120 dias","180 dias")
    val listmode = arrayListOf(CalcularActivity.M_STANDAR, CalcularActivity.M_PERSONALIZADO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrarBinding.inflate(inflater, container, false)
        var view = binding.root

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


        return view
    }

    private fun continuar() {
        btnContinuar.setOnClickListener {

            prestamo.capital = capitalDouble
            prestamo.interes = interesDouble
            prestamo.plazo_vto = mesesEntero

            var gson = Gson()
            var prestamoJson = gson.toJson(prestamo)

            startActivity(Intent(context, RegistrarActivity::class.java).putExtra("prestamoJson",prestamoJson))
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

    }

    private fun setupSpinners() {

        val adapterIntereses= ArrayAdapter<String>(context!!,R.layout.select_items, listaIntereses)
        val adapterPlazos= ArrayAdapter<String>(context!!,R.layout.select_items, listaPlazos)
        val adapterModos= ArrayAdapter<String>(context!!,R.layout.select_items, listmode)

        intereses.setAdapter(adapterIntereses)
        meses.setAdapter(adapterPlazos)
        modos.setAdapter(adapterModos)


        intereslayout.setEndIconOnClickListener { intereses.showDropDown() }
        intereses.setOnClickListener { intereses.showDropDown() }
        meses.setOnClickListener { meses.showDropDown() }
        meseslayout.setEndIconOnClickListener { meses.showDropDown() }
        modoLayout.setEndIconOnClickListener { modos.showDropDown() }
        modos.setOnClickListener { modos.showDropDown() }


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
            btnContinuar.backgroundTintList= ContextCompat.getColorStateList(context!!,R.color.primarycolordark_two)
            btnContinuar.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            btnContinuar.isEnabled = true

        } else
        {
            btnContinuar.isEnabled = false
            btnContinuar.backgroundTintMode = PorterDuff.Mode.MULTIPLY
            btnContinuar.backgroundTintList= ContextCompat.getColorStateList(context!!,R.color.color_input_text)
            btnContinuar.setTextColor(ContextCompat.getColor(context!!, R.color.color_input_text))

        }
    }

    private fun calcularTodo( mode: String) {

        when (mode)
        {
            M_STANDAR -> {

                capitalDouble = capitalPrestado.text.toString().trim().toDouble()
                interesDouble = intereses.text.substring(0,intereses.text.length-1).toDouble()
                mesesEntero = meses.text.substring(0,meses.text.length-5).toInt()
                _viewModel.calcularMontoDiario(capitalDouble,interesDouble,mesesEntero)
            }

            M_PERSONALIZADO -> {

                capitalDouble = capitalPrestado.text.toString().trim().toDouble()
                interesDouble = interesesP.text.toString().toDouble()
                mesesEntero = mesesP.text.toString().toInt()

                _viewModel.calcularMontoDiario(capitalDouble,interesDouble,mesesEntero)
            }

            else -> showMessage("No se pudo procesar tu solicitud")
        }




    }

    private fun observers() {

        _viewModel.getMessage().observe(this) { message ->
            if(message!= null)  showMessage(message)
        }

        _viewModel.getMontoDiario().observe(this){ montodiario ->
            if(montodiario !=null)
            {
                montoDiario.setText("S./ ${getDoubleWithTwoDecimals(montodiario)}")
                montoTotal.setText("S./ ${getDoubleWithTwoDecimals(montodiario *mesesEntero)}")
            }
        }


    }

    private fun clearData()
    {
        mesesP.setText("")
        interesesP.setText("")
        montoDiario.setText("S./ 0")
        montoTotal.setText("S./ 0")

    }

    private fun showMessage(message:String)
    {
        println("Mensaje nuevo: $message")
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
}