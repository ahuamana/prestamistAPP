package com.paparazziapps.pretamistapp.modulos.principal.views

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityPrincipalBinding
import com.paparazziapps.pretamistapp.databinding.BottomsheetDetallePrestamoBinding
import com.paparazziapps.pretamistapp.databinding.DialogSalirSinGuardarBinding
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.modulos.dashboard.views.HomeFragment
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import androidx.fragment.app.FragmentManager
import com.paparazziapps.pretamistapp.modulos.dashboard.interfaces.setOnClickedPrestamo
import com.paparazziapps.pretamistapp.modulos.dashboard.views.HomeFragment.Companion.setOnClickedPrestamoHome


class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPrincipalBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var layout_detalle_prestamo: BottomsheetDetallePrestamoBinding

    private lateinit var bottomSheetDetallePrestamo: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.navView
        toolbar              = binding.tool.toolbar


        setUpBottomNav()
        setUpInicialToolbar()
        setupBottomSheetDetallePrestamo()
    }

    private fun setupBottomSheetDetallePrestamo() {

        layout_detalle_prestamo = binding.layoutBottomsheetDetallePrestamo


        bottomSheetDetallePrestamo = BottomSheetBehavior.from(layout_detalle_prestamo.root)

        bottomSheetDetallePrestamo.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback()
        {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //Ocultar cortina cuando se oculta bottomsheet
                binding.cortinaBottomSheet.isVisible = newState < 4

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

        })

        layout_detalle_prestamo.root.setOnClickListener {

            bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
        }

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


    fun showBottomSheetDetallePrestamoPrincipal(prestamo: Prestamo, montoTotalAPagar: String, diasRestrasado:String, adapterPosition: Int, needUpdate:Boolean) {
        println("FEcha Unixtime:${getFechaActualNormalInUnixtime()}")

        var diasEnQueTermina = getDiasRestantesFromStart(prestamo.fecha?:"",prestamo.plazo_vto?:0)

        if(montoTotalAPagar.isNullOrEmpty())
        {
            layout_detalle_prestamo.contentPagoTotal.isVisible = false
            layout_detalle_prestamo.tvCapitalPrestado.text = "S./. 0.0"
            layout_detalle_prestamo.btnPagar.isVisible = false

        }else
        {
            layout_detalle_prestamo.contentPagoTotal.isVisible = true
            layout_detalle_prestamo.btnPagar.isVisible = true
        }


        layout_detalle_prestamo.lblNombreCompleto.text = "${replaceFirstCharInSequenceToUppercase(prestamo.nombres?:"")}, ${replaceFirstCharInSequenceToUppercase(prestamo.apellidos?:"")}"
        layout_detalle_prestamo.tvCapitalPrestado.text = "S./. ${prestamo.capital}"
        layout_detalle_prestamo.tvInteresPrestado.text = "${prestamo.interes}%"
        layout_detalle_prestamo.tvPlazoVto.text = "en $diasEnQueTermina dias"
        layout_detalle_prestamo.tvDiasRetrasados.text = "$diasRestrasado dias"
        layout_detalle_prestamo.tvDni.text = "${prestamo.dni}"
        layout_detalle_prestamo.tvFechaPrestamo.text = "${prestamo.fecha}"
        layout_detalle_prestamo.tvMontoTotal.text = "S/. ${montoTotalAPagar}"

        layout_detalle_prestamo.btnPagar.apply {
            this.standardSimpleButtonOutline()
            isVisible = needUpdate
            setOnClickListener {
                //Actualizar en fragment

                binding.cortinaBottomSheet.isVisible = false
                bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                setOnClickedPrestamoHome?.openDialogoActualizarPrestamo(prestamo,montoTotalAPagar,adapterPosition)

            }

        }

        //Mostrar bottom sheet
       binding.cortinaBottomSheet.isVisible = true
       bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_EXPANDED





    }



}