package com.paparazziapps.pretamistapp.modulos.principal.views

import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.ActivityPrincipalBinding
import com.paparazziapps.pretamistapp.databinding.BottomsheetDetallePrestamoBinding
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.google.common.base.Strings.isNullOrEmpty
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.modulos.dashboard.views.HomeFragment.Companion.setOnClickedLoanHome
import com.paparazziapps.pretamistapp.modulos.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.modulos.login.views.LoginActivity
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import org.koin.androidx.viewmodel.ext.android.viewModel


class PrincipalActivity : AppCompatActivity(){
    private lateinit var binding:ActivityPrincipalBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var layout_detalle_prestamo: BottomsheetDetallePrestamoBinding
    private lateinit var bottomSheetDetallePrestamo: BottomSheetBehavior<ConstraintLayout>
    private var preferences = MyPreferences()

    private var isEnabledCheck = true

    val _viewModelPrincipal by viewModel<ViewModelPrincipal>()
    val _viewModelBranches:ViewModelBranches by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.navView
        toolbar              = binding.tool.toolbar


        MyPreferences().isLogin = true
        isFreeTrial()
        setUpInicialToolbar()
        //testCrashlytics()
        _viewModelBranches.getSucursales()
        observers()

        /*val disappearView = DisappearView.attach(this)
        disappearView.execute(binding.layoutBottomsheetDetallePrestamo.root,
            duration = 4000,
            interpolator = AccelerateInterpolator(0.5f),
            needDisappear = true)*/
    }

    private fun observers() {

        _viewModelPrincipal.getUser().observe(this){
            println("Info usuario: ${it.superAdmin}")
            preferences.isAdmin = it.admin
            preferences.isSuperAdmin = it.superAdmin
            preferences.branchId = it.sucursalId?: INT_DEFAULT
            preferences.branchName = it.sucursal?:""
            preferences.emailUser = it.email?:""
            preferences.isActiveUser = it.activeUser

            if(it.activeUser) {
                setUpBottomNav()
                setupBottomSheetDetallePrestamo()
                binding.navView.beVisible()

            }else {
                //Usuario desactivado
                binding.navView.beGone()
                isUserActivePrincipal()
            }
        }

        _viewModelBranches.sucursales.observe(this){
            //save info sucursales
            if(it.isNotEmpty()){
                MyPreferences().branches = toJson(it)
            }
            //Get Info user
            _viewModelPrincipal.searchUserByEmail()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_guardar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val btnSave   = menu?.findItem(R.id.action_save_perfil)?.actionView?.findViewById<AppCompatButton>(R.id.btn_save_item)
        val colorState = if (isEnabledCheck) ContextCompat.getColor(this@PrincipalActivity, R.color.red)
        else ContextCompat.getColor(this@PrincipalActivity, R.color.color_text_web)
        val colorStateTxt = ContextCompat.getColor(this@PrincipalActivity, R.color.colorWhite)


        btnSave?.apply {
            isEnabled = isEnabledCheck

            //val resouse = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.corner_boton_outline) as Drawable
            val resouse = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.border_mask) as Drawable
            val customResource = tintDrawable(resouse, colorState)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resouse.colorFilter = BlendModeColorFilter(ContextCompat.getColor(this@PrincipalActivity, R.color.red), BlendMode.SRC_ATOP)
            }else{
                resouse.setColorFilter(ContextCompat.getColor(this@PrincipalActivity, R.color.red), PorterDuff.Mode.SRC_ATOP)
            }
            background = customResource

            val resouseDrawable = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.ic_logout) as Drawable
            val customResourceDrawable = tintDrawable(resouseDrawable, colorStateTxt)

            setCompoundDrawablesWithIntrinsicBounds(customResourceDrawable, null, null, null)
            setTextColor(colorStateTxt)
            setText("Cerrar sessión")
            setOnClickListener {
                if (isEnabledCheck){
                    logout()
                }
            }

        }


        return true
    }

    fun logout(){
        preferences.isLogin = false
        preferences.removeLoginData()
        startActivity(Intent(this@PrincipalActivity, LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun isUserActivePrincipal() {
        binding.cortinaUserInactive.beVisible()
        binding.userInactiveLayout.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun isFreeTrial() {
        if(resources.getBoolean(R.bool.isFreeTrail))
        {
            var fecha7Dias:Long = 1652651285000  // fechaPasado -> 1647147600000 o fechaSuperior -->1649826000000

            if(getFechaActualNormalInUnixtime().minus(fecha7Dias) > 0)
            {
                println("Fecha actual normal: ${getFechaActualNormalInUnixtime().minus(fecha7Dias)}")
                binding.cortinaFreeTrial.beVisible()
            }
        }

    }

    private fun testCrashlytics() {
        throw RuntimeException("Test Crash") // Force a crash
    }

    private fun setupBottomSheetDetallePrestamo() {

        layout_detalle_prestamo = binding.layoutBottomsheetDetallePrestamo

        bottomSheetDetallePrestamo = BottomSheetBehavior.from(layout_detalle_prestamo.root)

        bottomSheetDetallePrestamo.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
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


    fun showBottomSheetDetallePrestamoPrincipal(loanDomain: LoanDomain, montoTotalAPagar: Double, diasRestrasado:String, adapterPosition: Int, needUpdate:Boolean) {
        var diasRestantesPorPagarNuevo:Int?= null
        val diasEnQueTermina = getDiasRestantesFromStart(loanDomain.fecha_start_loan?:"",loanDomain.plazo_vto_in_days?:0)
        var isClosed:Boolean = false


        //Set inicial bottomsheet
        layout_detalle_prestamo.edtDiasAPagar.apply {
            text?.clear()
            clearFocus()
        }
        layout_detalle_prestamo.apply {
            contentLayoutDiasAPagar.error = null
            btnPagar.apply {
                text = "Actualizar deuda"
                isVisible = false
                standardSimpleButton()
            }
        }
        layout_detalle_prestamo.contenCapitalPrestado.apply {
            if(preferences.isAdmin || preferences.isSuperAdmin) this.beVisible()
            else this.beGone()
        }


        layout_detalle_prestamo.tvMontoDiario.apply {
            text= "S./ ${loanDomain.montoDiarioAPagar}"
        }

        layout_detalle_prestamo.tvPlazoPrestamo.text = "${loanDomain.plazo_vto_in_days.toString()} días"

        handledCloseLoan(loanDomain, montoTotalAPagar)

        val typeLoan = PaymentScheduled.getPaymentScheduledById( loanDomain.typeLoan?: INT_DEFAULT)

        layout_detalle_prestamo.tvTypeOfLoan.text = typeLoan.displayName
        layout_detalle_prestamo.lblNombreCompleto.text = "${replaceFirstCharInSequenceToUppercase(loanDomain.nombres?:"")}, ${replaceFirstCharInSequenceToUppercase(loanDomain.apellidos?:"")}"
        layout_detalle_prestamo.tvCapitalPrestado.text = "${getString(R.string.tipo_moneda)} ${loanDomain.capital}"
        layout_detalle_prestamo.tvInteresPrestado.text = "${loanDomain.interes}%"
        layout_detalle_prestamo.tvPlazoVto.text = "en $diasEnQueTermina días"

        layout_detalle_prestamo.tvDni.text = "${loanDomain.dni}"
        layout_detalle_prestamo.tvFechaPrestamo.text = "${loanDomain.fecha_start_loan}"
        layout_detalle_prestamo.tvMontoTotal.text = "S/. 0.00"
        layout_detalle_prestamo.tvDiasRetrasados.text = "$diasRestrasado días"

        //Must change depending of type of loan
        when(typeLoan){
            PaymentScheduledEnum.DAILY -> {
                // set in days
                layout_detalle_prestamo.contentLayoutDiasAPagar.hint = "Día(s) a pagar"
                layout_detalle_prestamo.lblPaidDaysOrQuotas.text = getString(R.string.days_paid_title)
                layout_detalle_prestamo.tvDiasPagados.text = "${loanDomain.diasPagados} días de ${loanDomain.plazo_vto_in_days} días"
            }
            else -> {
                // set in quotes
                layout_detalle_prestamo.contentLayoutDiasAPagar.hint = "Cuota(s) a pagar"
                layout_detalle_prestamo.lblPaidDaysOrQuotas.text = getString(R.string.quotes_paid_title)
                val quotes = loanDomain.quotas?:0
                val displayQuotes = if(quotes == 1) "cuota" else "cuotas"
                val paidQuotes = loanDomain.quotasPaid?:0
                Log.d("PaidQuotes", paidQuotes.toString())
                val displayQuotesPaid = if(paidQuotes == 1) "cuota" else "cuotas"
                layout_detalle_prestamo.tvDiasPagados.text = "$paidQuotes $displayQuotesPaid de $quotes $displayQuotes"
            }
        }


        layout_detalle_prestamo.btnPagar.apply {
            setOnClickListener {
                //Actualizar en fragment
                isClosed = text.toString()=="Cerrar préstamo"
                if(isClosed) {
                    binding.cortinaBottomSheet.isVisible = false
                    bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                    setOnClickedLoanHome?.openDialogUpdateLoan(loanDomain,0.0,adapterPosition, 0, 0, isClosed = isClosed)
                }else{

                    //get type of loan
                    when(typeLoan){
                        PaymentScheduledEnum.DAILY -> {
                            val montoTotalAPagarNuevo = layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt() * (loanDomain.montoDiarioAPagar?:0.0)
                            diasRestantesPorPagarNuevo = loanDomain.dias_restantes_por_pagar?.minus(layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt())
                            val diasPagadosNuevo = loanDomain.diasPagados?.plus(layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt())
                            binding.cortinaBottomSheet.isVisible = false
                            bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                            setOnClickedLoanHome?.openDialogUpdateLoan(
                                loanDomain,
                                montoTotalAPagarNuevo,
                                adapterPosition,
                                diasRestantesPorPagarNuevo?:-9999,
                                paidDays = diasPagadosNuevo!!,
                                isClosed = isClosed)
                        }
                        else -> {
                            val quotesToPayNow = layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt()
                            val amountPay = layout_detalle_prestamo.edtDiasAPagar.text.toString().trim().toInt() * (loanDomain.montoDiarioAPagar?:0.0)
                            val quotesPaidBefore = loanDomain.quotasPaid?:0
                            val currentQuotesPending = loanDomain.quotasPending?:loanDomain.quotas?:0
                            val newQuotesPending = currentQuotesPending - quotesToPayNow
                            val quotesPaidNowPlusQuotesPaidBefore = quotesPaidBefore.plus(quotesToPayNow)

                            Log.d("QuotesPaidNowPlusQuotesPaidBefore", quotesPaidNowPlusQuotesPaidBefore.toString())
                            Log.d("newQuotesPending", newQuotesPending.toString())
                            Log.d("QuotesToPayNow", quotesToPayNow.toString())
                            Log.d("AmountPay", amountPay.toString())
                            Log.d("QuotesPaidBefore", quotesPaidBefore.toString())

                            binding.cortinaBottomSheet.isVisible = false
                            bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                            setOnClickedLoanHome?.openDialogUpdateLoan(
                                loanDomain,
                                amountPay,
                                adapterPosition,
                                daysMissingToPay = newQuotesPending,
                                paidDays = quotesPaidNowPlusQuotesPaidBefore,
                                isClosed = isClosed)

                        }
                    }
                }
            }
        }



        //Validar
        layout_detalle_prestamo.edtDiasAPagar.doAfterTextChanged {
            val input = it.toString().trim()
            val maxLimit = when (typeLoan) {
                PaymentScheduledEnum.DAILY -> (loanDomain.plazo_vto_in_days ?: 0) - (loanDomain.diasPagados ?: 0)
                else -> (loanDomain.quotas ?: 0) - (loanDomain.quotasPaid ?: 0)
            }

            layout_detalle_prestamo.contentLayoutDiasAPagar.error = when {
                input.isEmpty() -> "Debe ingresar un valor"
                input.toInt() == 0 -> "El valor debe ser mayor a 0"
                input.toInt() > maxLimit -> "El valor no puede superar a $maxLimit"
                else -> null
            }

            if (input.isNotEmpty() && input.toInt() <= maxLimit) {
                layout_detalle_prestamo.btnPagar.apply {
                    this.standardSimpleButtonOutline()
                    isEnabled = true
                }

                val totalAmount = when (typeLoan) {
                    PaymentScheduledEnum.DAILY -> loanDomain.montoDiarioAPagar?.times(input.toInt()) ?: 0.0
                    else -> (loanDomain.montoDiarioAPagar?.times(input.toInt())) ?: 0.0
                }
                Log.d("TotalAmount", totalAmount.toString())
                layout_detalle_prestamo.tvMontoTotal.text = "S/. ${getDoubleWithTwoDecimals(totalAmount)}"
            } else {
                layout_detalle_prestamo.btnPagar.apply {
                    this.standardSimpleButtonOutlineDisable()
                    isEnabled = false
                }
                layout_detalle_prestamo.tvMontoTotal.text = "S/. 0.00"
            }
        }

        //Mostrar bottom sheet
       binding.cortinaBottomSheet.isVisible = true
       bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun handledCloseLoan(loanDomain: LoanDomain, montoTotalAPagar:Double) {
        //Ocultar vistas si no tiene deudas
        if(loanDomain.dias_restantes_por_pagar == 0 || loanDomain.quotasPending == 0) {
            Log.d("this","Dias restantes por pagar es == a 0 *---> ${loanDomain.dias_restantes_por_pagar}")
            //If dias restantes es cero
            layout_detalle_prestamo.apply {
                btnPagar.apply {
                    text = "Cerrar préstamo"
                    isVisible = true
                    isEnabled = true
                    standardSimpleButtonOutline()
                }
                contentDiasAPagar.isVisible = false
                contentPagoTotal.isVisible = false
            }
        }else {
            layout_detalle_prestamo.btnPagar.apply {
                this.standardSimpleButtonOutlineDisable()
                isEnabled = false
            }
            if(isNullOrEmpty(montoTotalAPagar.toString())) {
                if(montoTotalAPagar.toString().contains("null")) {
                    layout_detalle_prestamo.apply {
                        this.contentPagoTotal.isVisible = true
                        this.btnPagar.isVisible = true
                        this.contentLineaExtra.isVisible = true
                        this.contentDiasAPagar.isVisible = true
                    }

                }else {
                    layout_detalle_prestamo.apply {
                        this.contentPagoTotal.isVisible = false
                        this.tvCapitalPrestado.text = "${getString(R.string.tipo_moneda_defecto_cero)}"
                        this.btnPagar.isVisible = false
                        this.contentLineaExtra.isVisible = false
                        this.contentDiasAPagar.isVisible = false
                    }
                }

            }else {
                layout_detalle_prestamo.apply {
                    this.contentPagoTotal.isVisible = true
                    this.btnPagar.isVisible = true
                    this.contentLineaExtra.isVisible = true
                    this.contentDiasAPagar.isVisible = true
                }
            }
        }
    }
}