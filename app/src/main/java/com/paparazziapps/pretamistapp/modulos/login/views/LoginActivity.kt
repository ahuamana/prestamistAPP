package com.paparazziapps.pretamistapp.modulos.login.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.textview.MaterialTextView
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityLoginBinding
import com.paparazziapps.pretamistapp.helper.setColorToStatusBar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var txtRegistroNuevo:MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setColorToStatusBar(this)

        binding.apply {
            txtRegistroNuevo = btnRegister
        }


        openNewRegistro()
    }

    private fun openNewRegistro() {

        txtRegistroNuevo.apply {
            setOnClickListener {
                startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            }
        }

    }
}