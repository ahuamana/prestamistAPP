package com.paparazziapps.pretamistapp.presentation.login.views

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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegisterBinding
import com.paparazziapps.pretamistapp.helper.hideKeyboardActivity
import com.paparazziapps.pretamistapp.helper.isValidEmail
import com.paparazziapps.pretamistapp.helper.setColorToStatusBar
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.domain.UserForm
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelRegisterUser
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.presentation.principal.views.PrincipalActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    lateinit var binding:ActivityRegisterBinding
    val _viewModel:ViewModelBranches by viewModel()
    val _viewModelRegistro: ViewModelRegisterUser by viewModel()

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

    var userFormNew = UserForm()
    var listaSucursales = mutableListOf<Sucursales>()


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

        _viewModel.getBranches()

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

    private fun _saveOnFirebase(userForm: UserForm) {
        _viewModelRegistro.saveFirebaseUser(userForm)
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
                ContextCompat.getColorStateList(applicationContext, R.color.primary)
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
            userFormNew.lastnames = edtLastname?.text.toString().trim()
            userFormNew.email = edtEmail?.text.toString().trim()
            userFormNew.names = edtFullname?.text.toString().trim()
            userFormNew.branch = sucursalesTextView.text.toString().trim()
            userFormNew.password = edtPass?.text.toString().trim()  //Ocultar si no quieres que se muestre la contraseña

            listaSucursales.forEach {
                if(it.name?.equals(sucursalesTextView.text.toString().trim()) == true)  userFormNew.branchId = it.id
            }
            //Register

            val email = binding.email.text.toString().trim()
            val pass = binding.password.text.toString().trim()

            _viewModelRegistro.createUser(email, pass, userFormNew)
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

        lifecycleScope.launch {
            _viewModel.branches.observe(this@RegisterActivity) {
                listaSucursales = it.toMutableList()
                var scrsales = mutableListOf<String>()
                it.forEach {
                    scrsales.add(it.name?:"")
                }
                val adapterSucursales= ArrayAdapter(this@RegisterActivity,R.layout.select_items, scrsales)
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
            if (userFormNew.email.equals(user.getEmail())) {
                _saveOnFirebase(userFormNew)
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
            setBackgroundColor(ContextCompat.getColor(context, R.color.primary))
            setNavigationOnClickListener { onBackPressed() }
            title = "Registrar"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}