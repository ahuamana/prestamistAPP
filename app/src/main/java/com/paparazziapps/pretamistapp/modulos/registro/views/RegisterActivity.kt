package com.paparazziapps.pretamistapp.modulos.registro.views

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegisterBinding
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister

class RegisterActivity : AppCompatActivity() {

    var _viewModel = ViewModelRegister.getInstance()

    //Variables
    lateinit var intereses: AppCompatAutoCompleteTextView
    lateinit var intereslayout: TextInputLayout
    lateinit var meses: AppCompatAutoCompleteTextView
    lateinit var meseslayout: TextInputLayout
    lateinit var capitalPrestado: TextInputEditText
    lateinit var btnRegistrar: MaterialButton

    var isValidInteres = false
    var isValidMeses = false
    var isValidCapitalPrestado = false
    var mesesEntero:Int = 0

    //Layout
    lateinit var binding:ActivityRegisterBinding
    val listaIntereses = arrayListOf<String>("8%","10%","20%","30%","40%","50%")
    val listaPlazos = arrayListOf<String>("30 dias","60 dias","90 dias","120 dias","180 dias")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //vincular vistas
        intereses = binding.interesSP
        intereslayout = binding.interesLayout
        meses = binding.plazos
        meseslayout = binding.plazosLayout
        capitalPrestado = binding.capitalprestadoEdt
        btnRegistrar = binding.continuarButton



        setupSpinners()
        observers()

        validateAll()

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
    }

    private fun setupSpinners() {

        val adapterIntereses= ArrayAdapter<String>(applicationContext,R.layout.select_items, listaIntereses)
        val adapterPlazos= ArrayAdapter<String>(applicationContext,R.layout.select_items, listaPlazos)

        intereses.setAdapter(adapterIntereses)
        meses.setAdapter(adapterPlazos)

        intereslayout.setEndIconOnClickListener { intereses.showDropDown() }
        intereses.setOnClickListener { intereses.showDropDown() }
        meses.setOnClickListener { meses.showDropDown() }
        meseslayout.setOnClickListener { meses.showDropDown() }




    }

   private fun validateFields ()
   {
       if(isValidInteres && isValidMeses && isValidCapitalPrestado)
       {
           var capitalDouble = capitalPrestado.text.toString().trim().toDouble()
           var interesDouble = intereses.text.substring(0,intereses.text.length-1).toDouble()
           mesesEntero = meses.text.substring(0,meses.text.length-5).toInt()

           _viewModel.calcularMontoDiario(capitalDouble,interesDouble,mesesEntero)
           btnRegistrar.backgroundTintMode = PorterDuff.Mode.SCREEN
           btnRegistrar.backgroundTintList= ContextCompat.getColorStateList(this,R.color.primarycolordark_two)
           btnRegistrar.setTextColor(ContextCompat.getColor(this, R.color.white))
           btnRegistrar.isEnabled = true

       } else
       {
           btnRegistrar.isEnabled = false
           btnRegistrar.backgroundTintMode = PorterDuff.Mode.MULTIPLY
           btnRegistrar.backgroundTintList= ContextCompat.getColorStateList(this,R.color.color_input_text)
           btnRegistrar.setTextColor(ContextCompat.getColor(this, R.color.color_input_text))

       }
   }

    private fun observers() {

        _viewModel.getMessage().observe(this) { message ->
            if(message!= null)  showMessage(message)
        }

        _viewModel.getMontoDiario().observe(this){ montodiario ->
            if(montodiario !=null)
            {
                var total = mesesEntero * montodiario
                binding.montoDiario.setText("S./ ${montodiario}")
                binding.montoTotal.setText("S./ $total")
            }
        }


    }

    private fun showMessage(message:String)
    {
        println("Mensaje nuevo: $message")
    }

}