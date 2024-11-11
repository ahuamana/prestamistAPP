package com.paparazziapps.pretamistapp.presentation.principal.views

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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
import com.paparazziapps.pretamistapp.presentation.dashboard.views.HomeFragment.Companion.setOnClickedLoanHome
import com.paparazziapps.pretamistapp.presentation.login.views.LoginActivity
import com.paparazziapps.pretamistapp.presentation.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PrincipalActivity : AppCompatActivity(){
    private lateinit var binding:ActivityPrincipalBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var layout_detalle_prestamo: BottomsheetDetallePrestamoBinding
    private lateinit var bottomSheetDetallePrestamo: BottomSheetBehavior<ConstraintLayout>
    private val preferences: MyPreferences by inject()
    private var isEnabledCheck = true
    private val viewModelPrincipal by viewModel<ViewModelPrincipal>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.navView
        toolbar              = binding.tool.toolbar
        isFreeTrial()
        setUpInicialToolbar()
        observers()
    }

    private fun observers() {
        lifecycleScope.launch {
            viewModelPrincipal.uiState.observe(this@PrincipalActivity, ::handleUIState)
        }
    }

    private fun handleUIState(uiStatePrincipal: ViewModelPrincipal.UIStatePrincipal) {
        Log.d("UIStatePrincipal", uiStatePrincipal.toString())
        when(uiStatePrincipal) {
            is ViewModelPrincipal.UIStatePrincipal.Loading -> {
                binding.loadingContainer.loadingContainer.beVisible()
            }
            is ViewModelPrincipal.UIStatePrincipal.Error -> {
                binding.errorContainer.root.beVisible()
            }
            is ViewModelPrincipal.UIStatePrincipal.SuccessActiveUser -> {
                binding.loadingContainer.root.beGone()
                binding.errorContainer.root.beGone()
                binding.cortinaUserInactive.beGone()
                binding.cortinaFreeTrial.beGone()

                binding.navView.beVisible()

                setUpBottomNav()
                setupBottomSheetDetallePrestamo()
                setNavGraphProgramaticatly()
                binding.navHostFragmentActivityMain

            }
            is ViewModelPrincipal.UIStatePrincipal.SuccessInactiveUser -> {
                binding.loadingContainer.root.beGone()
                binding.errorContainer.root.beGone()
                binding.cortinaUserInactive.beGone()
                binding.cortinaFreeTrial.beGone()
                binding.navView.beGone()

                isUserActivePrincipal()
            }
        }
    }

    private fun setNavGraphProgramaticatly() {
        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        navHostFragment.findNavController().setGraph(R.navigation.navigation_parent)
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
            val customResource = tintDrawable(this@PrincipalActivity,resouse, colorState)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resouse.colorFilter = BlendModeColorFilter(ContextCompat.getColor(this@PrincipalActivity, R.color.red), BlendMode.SRC_ATOP)
            }else{
                resouse.setColorFilter(ContextCompat.getColor(this@PrincipalActivity, R.color.red), PorterDuff.Mode.SRC_ATOP)
            }
            background = customResource

            val resouseDrawable = ContextCompat.getDrawable(this@PrincipalActivity, R.drawable.ic_logout) as Drawable
            val customResourceDrawable = tintDrawable(this@PrincipalActivity,resouseDrawable, colorStateTxt)

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

            when(item.itemId) {
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

        val quotas = loanDomain.quotas?:0
        val typeLoanInDays = loanDomain.typeLoanDays?:1
        val quotasPerDays = quotas * typeLoanInDays
        val daysWhenEndsInDays = getDiasRestantesFromStart(loanDomain.loanStartDateFormatted?:"",quotasPerDays)
        val daysSett = if(daysWhenEndsInDays > 0) daysWhenEndsInDays else 0

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
                standardSimpleButton(this@PrincipalActivity)
            }
        }
        layout_detalle_prestamo.contenCapitalPrestado.apply {
            if(preferences.isAdmin || preferences.isSuperAdmin) this.beVisible()
            else this.beGone()
        }


        layout_detalle_prestamo.tvMontoDiario.apply {
            text= "S./ ${loanDomain.amountPerQuota}"
        }

        layout_detalle_prestamo.tvPlazoPrestamo.text = "${loanDomain.quotas.toString()} días"

        handledCloseLoan(loanDomain, montoTotalAPagar)

        val typeLoan = PaymentScheduled.getPaymentScheduledById( loanDomain.typeLoan?: INT_DEFAULT)

        layout_detalle_prestamo.tvTypeOfLoan.text = typeLoan.displayName
        layout_detalle_prestamo.lblNombreCompleto.text = "${replaceFirstCharInSequenceToUppercase(loanDomain.names?:"")}, ${replaceFirstCharInSequenceToUppercase(loanDomain.lastnames?:"")}"
        layout_detalle_prestamo.tvCapitalPrestado.text = "${getString(R.string.tipo_moneda)} ${loanDomain.capital}"
        layout_detalle_prestamo.tvInteresPrestado.text = "${loanDomain.interest}%"
        layout_detalle_prestamo.tvPlazoVto.text = "en $daysWhenEndsInDays días"

        layout_detalle_prestamo.tvDni.text = "${loanDomain.dni}"
        layout_detalle_prestamo.tvFechaPrestamo.text = "${loanDomain.loanStartDateFormatted}"
        layout_detalle_prestamo.tvMontoTotal.text = "S/. 0.00"
        layout_detalle_prestamo.tvDiasRetrasados.text = "$diasRestrasado días"

        //Must change depending of type of loan
        when(typeLoan){
            PaymentScheduledEnum.DAILY -> {
                // set in days
                layout_detalle_prestamo.contentLayoutDiasAPagar.hint = "Día(s) a pagar"
                layout_detalle_prestamo.lblPaidDaysOrQuotas.text = getString(R.string.days_paid_title)
                layout_detalle_prestamo.tvDiasPagados.text = "${loanDomain.quotasPaid} de ${loanDomain.quotas} días"
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
                layout_detalle_prestamo.tvDiasPagados.text = "$paidQuotes de $quotes $displayQuotes"
            }
        }


        layout_detalle_prestamo.btnPagar.apply {
            setOnClickListener {
                //get type of loan
                val quotesToPay = binding.layoutBottomsheetDetallePrestamo.edtDiasAPagar.text.toString().trim().toIntOrNull()?:0

                when(typeLoan){
                    PaymentScheduledEnum.DAILY -> {
                        binding.cortinaBottomSheet.isVisible = false
                        bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                        setOnClickedLoanHome?.openDialogUpdateLoan(loanDomain, quotesToPay = quotesToPay)
                    }
                    else -> {
                        binding.cortinaBottomSheet.isVisible = false
                        bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
                        setOnClickedLoanHome?.openDialogUpdateLoan(loanDomain, quotesToPay = quotesToPay)
                    }
                }
            }
        }



        //Validar
        layout_detalle_prestamo.edtDiasAPagar.doAfterTextChanged {
            val input = it.toString().trim()
            val maxLimit = (loanDomain.quotas ?: 0) - (loanDomain.quotasPaid ?: 0)

            layout_detalle_prestamo.contentLayoutDiasAPagar.error = when {
                input.isEmpty() -> "Debe ingresar un valor"
                input.toInt() == 0 -> "El valor debe ser mayor a 0"
                input.toInt() > maxLimit -> "El valor no puede superar a $maxLimit"
                else -> null
            }

            if (input.isNotEmpty() && input.toInt() <= maxLimit) {
                layout_detalle_prestamo.btnPagar.apply {
                    this.standardSimpleButtonOutline(this@PrincipalActivity)
                    isEnabled = true
                }

                val totalAmount = when (typeLoan) {
                    PaymentScheduledEnum.DAILY -> loanDomain.amountPerQuota?.times(input.toInt()) ?: 0.0
                    else -> (loanDomain.amountPerQuota?.times(input.toInt())) ?: 0.0
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

        val needToClose = ((loanDomain.quotasPending?:loanDomain.quotas?:0) <=0)
        if(needToClose) {
            Log.d("this","Dias restantes por pagar es == a 0 *---> ${loanDomain.quotasPending}")
            //If dias restantes es cero
            layout_detalle_prestamo.apply {
                btnPagar.apply {
                    text = "Cerrar préstamo"
                    isVisible = true
                    isEnabled = true
                    standardSimpleButtonOutline(this@PrincipalActivity)
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