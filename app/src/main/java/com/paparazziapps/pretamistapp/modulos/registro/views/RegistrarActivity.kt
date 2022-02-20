package com.paparazziapps.pretamistapp.modulos.registro.views

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.PorterDuff
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegistrarBinding
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import java.text.SimpleDateFormat
import java.util.*

class RegistrarActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistrarBinding
    var prestamo = Prestamo()

    lateinit var fecha:TextInputEditText
    lateinit var nombres:TextInputEditText
    lateinit var apellidos:TextInputEditText
    lateinit var dni:TextInputEditText
    lateinit var celular:TextInputEditText

    lateinit var registerButton:MaterialButton

    var isValidFecha:Boolean = false
    var isValidNombres:Boolean = false
    var isValidApellidos:Boolean = false
    var isValidDni:Boolean = false
    var isValidCelular:Boolean = false


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


        //get intent
        getExtras()
        showCalendar()
        validateFields()
        registerPrestamo()
    }

    private fun registerPrestamo() {

        binding.registrarButton.setOnClickListener {
            //Register ViewModel
        }
    }

    private fun validateFields() {

        fecha.doAfterTextChanged {
            isValidFecha = !fecha.text.isNullOrEmpty()
            isValidFecha.apply {
                if(this) showbutton()
            }
        }

        nombres.doAfterTextChanged {
            isValidNombres = !nombres.text.isNullOrEmpty()
            isValidNombres.apply {
                if(this) showbutton()
            }

        }

        apellidos.doAfterTextChanged {
            isValidApellidos = !apellidos.text.isNullOrEmpty()
            isValidApellidos.apply {
                if(this) showbutton()
            }
        }

        dni.doAfterTextChanged {

            if(!dni.text.isNullOrEmpty())
            {
                println("is valid dni: ${dni.text!!.count()}")

                if(dni.text!!.count() == 8)
                {
                    isValidDni = true
                    showbutton()

                }else {
                    isValidDni = false
                    showbutton()
                }
            }else {
                isValidDni = false
                showbutton()
            }

        }

        celular.doAfterTextChanged {
            if(!celular.text.isNullOrEmpty())
            {
                if(celular.text!!.count() == 9)
                {
                    isValidCelular = true
                    showbutton()

                }else {
                    isValidCelular = false
                    showbutton()
                }
            }else
            {
                isValidCelular = false
                showbutton()
            }

        }


    }

    private fun showbutton() {

        if(isValidFecha
            && isValidApellidos
            && isValidCelular
            && isValidNombres
            && isValidDni)
        {
            //Registrar prestamo
            registerButton.apply {
                isEnabled = true
                backgroundTintMode = PorterDuff.Mode.SCREEN
                backgroundTintList= ContextCompat.getColorStateList(context,R.color.primarycolordark_two)
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
            SimpleDateFormat("dd/MM/yyyy").format(it).toString().also { binding.fecha.setText(it) }

        }

    }



    private fun getExtras() {

        if(intent.extras != null)
        {
           var extras  = intent.getStringExtra("prestamoJson")
           var gson = Gson()

            if(!extras.isNullOrEmpty())
            {
                prestamo = gson.fromJson(extras, Prestamo::class.java)
                binding.interes.setText("${prestamo.interes!!.toInt()}%")
                binding.capital.setText("S./ ${prestamo.capital!!.toInt()}")
                binding.plazosEnDias.setText("${prestamo.plazo_vto.toString()} dias")
            }
        }

    }

}