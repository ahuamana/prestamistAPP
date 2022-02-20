package com.paparazziapps.pretamistapp.modulos.principal.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.compose.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityPrincipalBinding

class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPrincipalBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.navView
        toolbar              = binding.tool.toolbar


        setUpBottomNav()
        setUpInicialToolbar()
    }

    private fun setUpInicialToolbar() {

        toolbar.title = "Dashboard"
        setSupportActionBar(toolbar)
    }

    private fun setUpBottomNav() {

        val navController =findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_registrar, R.id.navigation_finanzas, R.id.navigation_home
            )
        )
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemSelectedListener { item ->

            when(item.itemId)
            {
                R.id.navigation_finanzas -> {
                    println("Mostraste finanzas")
                    navController.navigate(R.id.navigation_finanzas)
                    toolbar.title = "Finanzas"
                    true
                }

                R.id.navigation_home -> {
                    println("Mostraste home")
                    navController.navigate(R.id.navigation_home)
                    toolbar.title = "Dashboard"
                    true
                }

                R.id.navigation_registrar -> {
                    println("Mostraste registrar")
                    navController.navigate(R.id.navigation_registrar)
                    toolbar.title = "Registrar"
                    true
                }

                else -> false
            }

        }
    }


}