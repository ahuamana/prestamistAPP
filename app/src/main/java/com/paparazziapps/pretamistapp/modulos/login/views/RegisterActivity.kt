package com.paparazziapps.pretamistapp.modulos.login.views

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegisterBinding
import com.paparazziapps.pretamistapp.helper.hideKeyboardActivity
import com.paparazziapps.pretamistapp.helper.isValidEmail
import com.paparazziapps.pretamistapp.helper.setColorToStatusBar
import com.paparazziapps.pretamistapp.modulos.login.pojo.User
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelRegistroUsuario
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelSucursales
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity

class RegisterActivity : AppCompatActivity() {

    lateinit var binding:ActivityRegisterBinding
    var _viewModel = ViewModelSucursales.getInstance()
    var _viewModelRegistro = ViewModelRegistroUsuario.getInstance()

    var edtFullname: TextInputEditText? = null
    var edtLastname:TextInputEditText? = null
    var edtEmail:TextInputEditText? = null
    var edtPass:TextInputEditText? = null
    var inputLayoutFullname: TextInputLayout? = null
    var inputLayoutLastname:TextInputLayout? = null
    var inputLayoutEmail:TextInputLayout? = null
    var inputLayoutPass:TextInputLayout? = null

    var isValidFullname = false
    var isValidlastname:Boolean? = false
    var isValidSucursal:kotlin.Boolean? = false
    var isValidEmail:kotlin.Boolean? = false
    var isValidPass:kotlin.Boolean? = false
    var btnSignUp: MaterialButton? = null

    var userNew = User()


    lateinit var sucursalesTextView: AppCompatAutoCompleteTextView
    lateinit var sucursalesLayout: TextInputLayout
    lateinit var viewProgressSucursal:View
    lateinit var viewCurtainSucursal: View
    lateinit var viewDotsSucursal:View

    private lateinit var toolbar  : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setColorToStatusBar(this)

        binding.apply {
                         toolbar = tool.toolbar
            termsConditions.text = getString(R.string.app_terms_conditions, getString(R.string.app_name))
            sucursalesTextView   = edtSucursal
            sucursalesLayout     = txtInputLayoutSucursal
            viewProgressSucursal = progressSucursal
            viewDotsSucursal     = dotsSucursal
            viewCurtainSucursal  = curtainSucursal
            edtFullname          = fullname
            edtLastname         = lastname
            edtEmail            = email
            edtPass             = password
            inputLayoutFullname = layoutFullname
            inputLayoutLastname = layoutLastname
            inputLayoutEmail    = layoutEmail
            inputLayoutPass     = layoutPass
            btnSignUp           = signUp
        }


        signUp()
        hideKeyboardActivity(this)
        validateFields()

        _viewModel.getSucursales()

        setupToolbar()
        observers()
    }

    private fun validateFields() {
        edtFullname!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 2) {
                    inputLayoutFullname!!.error = null
                    isValidFullname = true
                } else {
                    inputLayoutFullname!!.error = "El campo debe tener minimo 3 caracteres"
                    isValidPass = false
                }
                isAllValid()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        edtLastname?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 2) {
                    inputLayoutLastname!!.error = null
                    isValidlastname = true
                } else {
                    inputLayoutLastname!!.error = "El campo debe tener minimo 3 caracteres"
                    isValidlastname = false
                }
                isAllValid()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        sucursalesTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 2) {
                    sucursalesLayout!!.error = null
                    isValidSucursal = true
                    //LLamar al metodo principal
                } else {
                    sucursalesLayout!!.error = "Seleccione una opción"
                    isValidSucursal = false
                }
                isAllValid()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        edtEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isValidEmail(s)) {
                    inputLayoutEmail!!.error = null
                    isValidEmail = true
                } else {
                    inputLayoutEmail!!.error = "Correo electrónico invalido"
                    isValidEmail = false
                }
                isAllValid()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        edtPass!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length > 5) {
                    inputLayoutPass!!.error = null
                    isValidPass = true
                } else {
                    inputLayoutPass!!.error = "La contraseña debe tener minimo 6 caracteres"
                    isValidPass = false
                }
                isAllValid()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun _saveOnFirebase(user: User) {
        _viewModelRegistro.saveFirebaseUser(user)
    }

    private fun _showMessageMainThread(message: String?) {
        Snackbar.make(findViewById(android.R.id.content), "" + message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun isAllValid() {
        if (isValidSucursal!!
            && isValidPass!!
            && isValidEmail!!
            && isValidFullname
            && isValidlastname!!
        ) {
            btnSignUp?.apply {
                isEnabled = true
                backgroundTintMode = PorterDuff.Mode.SCREEN
                backgroundTintList =
                ContextCompat.getColorStateList(applicationContext, R.color.colorPrimary)
                setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            }
        } else {

            btnSignUp?.apply {
                isEnabled = false
                backgroundTintMode = PorterDuff.Mode.MULTIPLY
                backgroundTintList = ContextCompat.getColorStateList(applicationContext, R.color.color_input_text)
                setTextColor(ContextCompat.getColor(applicationContext, R.color.color_input_text))
                }
            }

    }

    private fun signUp() {
        binding.signUp.setOnClickListener {
            userNew.apellidos = edtLastname?.text.toString().trim()
            userNew.email = edtEmail?.text.toString().trim()
            userNew.nombres = edtFullname?.text.toString().trim()
            userNew.sucursal = sucursalesTextView.text.toString().trim()
            userNew.password = edtPass?.text.toString().trim()  //Ocultar si no quieres que se muestre la contraseña
            _viewModelRegistro.createUser(
                edtEmail!!.text.toString().trim(),
                edtPass!!.text.toString().trim())
        }
    }

    private fun goToPrincipal() {
        //Guardar las preferencias
        //guardarPreferencias();
        val i = Intent(applicationContext, PrincipalActivity::class.java)
            .putExtra("email", edtEmail!!.text.toString().trim())
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK //eliminar activities anteriores
        startActivity(i)
    }


    private fun observers() {
        _viewModel.showSucursales().observe(this){

            if(it.isNotEmpty())
            {
                val adapterSucursales= ArrayAdapter(this,R.layout.select_items, it)
                sucursalesTextView.setAdapter(adapterSucursales)
                sucursalesTextView.setOnClickListener { sucursalesTextView.showDropDown() }
                sucursalesLayout.setEndIconOnClickListener { sucursalesTextView.showDropDown() }

                viewProgressSucursal.isVisible = false
                viewDotsSucursal.isVisible = false
                viewCurtainSucursal.isVisible = false

            }

        }

        _viewModelRegistro.showMessage().observe(this) { message ->
            if (message != null) {
                _showMessageMainThread(message)
            }
        }

        _viewModelRegistro.getUser().observe(this) { user ->
            if (userNew.email.equals(user.getEmail())) {
                _saveOnFirebase(userNew)
            } else {
                _showMessageMainThread(user.email)
            }
        }

        _viewModelRegistro.getIsLoading().observe(this) { isLoading ->
            Log.e("ISLOADING", "ISLOADING:$isLoading")
            if (isLoading) {
                binding.cortinaLayout.visibility = View.VISIBLE
            } else {
                binding.cortinaLayout.visibility = View.GONE
            }
        }

        _viewModelRegistro.getIsSavedFirebase().observe(this) { isSavedFirebase ->
            if (isSavedFirebase) {
                goToPrincipal()
            } else {
                _showMessageMainThread("isSavedFirebase:" + isSavedFirebase.toString())
            }
        }


    }

    private fun setupToolbar() {
        toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            setNavigationOnClickListener { onBackPressed() }
            title = "Registrar"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        ViewModelSucursales.destroyInstance()
        ViewModelRegistroUsuario.destroyInstance()
        super.onDestroy()
    }
}