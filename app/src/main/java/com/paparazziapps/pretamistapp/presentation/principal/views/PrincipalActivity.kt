package com.paparazziapps.pretamistapp.presentation.principal.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
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
import com.paparazziapps.pretamistapp.presentation.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PrincipalActivity : AppCompatActivity(){
    private lateinit var binding:ActivityPrincipalBinding
    private lateinit var layout_detalle_prestamo: BottomsheetDetallePrestamoBinding
    private lateinit var bottomSheetDetallePrestamo: BottomSheetBehavior<ConstraintLayout>
    private val preferences: MyPreferences by inject()
    private val viewModelPrincipal by viewModel<ViewModelPrincipal>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isFreeTrial()
        setupToolbar()
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
                setNavGraphProgrammatically()
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

    private fun setNavGraphProgrammatically() {
        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        navHostFragment.findNavController().setGraph(R.navigation.navigation_parent)
    }


    private fun isUserActivePrincipal() {
        binding.cortinaUserInactive.beVisible()
    }

    private fun isFreeTrial() {
        if(resources.getBoolean(R.bool.isFreeTrail)) {
            val fecha7Dias:Long = 1652651285000  // fechaPasado -> 1647147600000 o fechaSuperior -->1649826000000

            if(getFechaActualNormalInUnixtime().minus(fecha7Dias) > 0) {
                println("Fecha actual normal: ${getFechaActualNormalInUnixtime().minus(fecha7Dias)}")
                binding.cortinaFreeTrial.beVisible()
            }
        }

    }

    private fun setupBottomSheetDetallePrestamo() {

        layout_detalle_prestamo = binding.layoutBottomsheetDetallePrestamo

        bottomSheetDetallePrestamo = BottomSheetBehavior.from(layout_detalle_prestamo.root)

        bottomSheetDetallePrestamo.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.cortinaBottomSheet.isVisible = newState < 4 }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        layout_detalle_prestamo.root.setOnClickListener {
            bottomSheetDetallePrestamo.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun setupToolbar() {
        with(binding.tool){
            setSupportActionBar(toolbar)
            toolbar.navigationIcon?.setTint(getColor(R.color.white))
            ivAvatar.setOnClickListener {
                binding.navView.menu.findItem(R.id.navigation_profile)?.isChecked = true
                with(findNavController(R.id.nav_host_fragment_activity_main)) {
                    navigate(
                        R.id.navigation_profile, null, NavOptions.Builder()
                            .setPopUpTo(this.currentDestination?.id ?: R.id.navigation_home, inclusive = true)
                            .setLaunchSingleTop(true)
                            .build()
                    )
                }
            }
        }
    }

    private fun setUpBottomNav() {
        Log.d("setUpBottomNav", "setUpBottomNav")
        val navController: NavController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_registrar, R.id.navigation_finanzas,
                R.id.navigation_home, R.id.clients_menu,
                R.id.navigation_profile
            )
        )

        binding.tool.toolbar.beVisible()
        binding.navView.beVisible()

        binding.navView.setupWithNavController(navController)
        NavigationUI.setupWithNavController(binding.tool.toolbar, navController,appBarConfiguration)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("addOnDestinationChangedListener", "addOnDestinationChangedListener ${destination.id}")
            when (destination.id) {
                R.id.navigation_profile -> {
                    binding.tool.toolbar.beVisible()
                    binding.tool.ivAvatar.beGone()
                    //reset navigation selected item
                    binding.navView.menu.findItem(R.id.navigation_profile)?.isChecked = true
                }

                else -> {
                    binding.tool.toolbar.beVisible()
                    binding.tool.ivAvatar.beVisible()
                    Log.d("navController", "addOnDestinationChangedListener ${destination.id} -- ${destination.label}")
                }
            }
        }



        // Integrate OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event for navView
                if (navController.currentDestination?.id != R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home)
                } else {
                    finish()
                }
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        Log.d("onPrepareOptionsMenu", "onPrepareOptionsMenu")
        return super.onPrepareOptionsMenu(menu)
    }

    override fun invalidateOptionsMenu() {
        Log.d("invalidateOptionsMenu", "invalidateOptionsMenu")
        super.invalidateOptionsMenu()
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

    override fun onDestroy() {
        super.onDestroy()
    }
}