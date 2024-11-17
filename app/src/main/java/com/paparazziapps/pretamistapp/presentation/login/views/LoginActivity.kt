package com.paparazziapps.pretamistapp.presentation.login.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.paparazziapps.pretamistapp.databinding.ActivityLoginBinding
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelLogin
import com.paparazziapps.pretamistapp.presentation.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.application.MyPreferences
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    val viewModel:ViewModelLogin  by viewModel()
    private val tag: String = LoginActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setColorToStatusBar(this)
        viewModel.checkIfUserIsLogged(this)


        binding.apply {
            email.setText(viewModel.getSavedEmail())
            binding.versioncode.text = "Versión ${getVersionName()}"
        }

        setupButtons()
        showObservables()
    }

    private fun showObservables() {
        viewModel.showMessage.observe(this) { message ->
            if (message != null) {
                showMessageMainThread(message)
            }
        }
        viewModel.isLoginAnonymous.observe(this) { isLoginAnonymous ->
            if (isLoginAnonymous) {
                startActivity(Intent(this, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }

        //Login with email
        viewModel.isLoginEmail.observe(this) { isLoginEmail ->
            Log.d(tag,"isLoginEmail: $isLoginEmail")
            if (isLoginEmail) {
                Log.e(tag, "EMAIL ENVIADO: " + binding.email.text.toString().lowercase())
                startActivity(
                    Intent(this, PrincipalActivity::class.java)
                        .putExtra("email", binding.email.text.toString().lowercase())
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            Log.e(tag, "ISLOADING:$isLoading")
            if (isLoading) {
                binding.cortinaLayout.visibility = View.VISIBLE
            } else {
                binding.cortinaLayout.visibility = View.GONE
            }
        }
    }


    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
        }

        binding.ingresarLoginButton.setOnClickListener {
            hideKeyboardActivity(this@LoginActivity)
            handledLogin()
        }
    }

    private fun handledLogin() {
        if (isConnected(applicationContext).not()) {
            showMessageMainThread("Sin conexion a internet")
            return
        }

        val email = binding.email.text.toString().trim()
        binding.emailLayout.error = when {
            email.isEmpty() -> "Ingrese un correo electrónico"
            isValidEmail(email).not() -> "Correo electrónico invalido"
            else -> null
        }

        val pass = binding.pass.text.toString().trim()
        binding.passLayout.error = when{
            pass.isEmpty() -> "Ingrese una contraseña"
            pass.length < 6 -> "La contraseña debe tener minimo 6 caracteres"
            else -> null
        }

        if(binding.passLayout.error != null) return
        if(binding.emailLayout.error != null) return

        viewModel.loginWithEmail(email, pass)
    }

    private fun showMessageMainThread(message: String) {
        Snackbar.make(findViewById(android.R.id.content), "" + message, Snackbar.LENGTH_SHORT).show()
    }
}