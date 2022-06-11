package com.paparazziapps.pretamistapp.modulos.login.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityRegisterBinding
import com.paparazziapps.pretamistapp.helper.MainApplication
import com.paparazziapps.pretamistapp.helper.setColorToStatusBar
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelRegistro
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister

class RegisterActivity : AppCompatActivity() {

    lateinit var binding:ActivityRegisterBinding
    var _viewModel = ViewModelRegistro.getInstance()


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
        }

        _viewModel.getSucursales()

        setupToolbar()
        observers()
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
    }

    private fun setupToolbar() {
        toolbar.apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            setNavigationOnClickListener { onBackPressed() }
            title = "Registrar"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}