package com.paparazziapps.pretamistapp.presentation.dashboard.views

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.DialogSalirSinGuardarBinding
import com.paparazziapps.pretamistapp.databinding.FragmentHomeBinding
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.presentation.dashboard.adapters.LoanAdapter
import com.paparazziapps.pretamistapp.presentation.dashboard.interfaces.SetOnClickedLoan
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.domain.User
import com.paparazziapps.pretamistapp.presentation.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.TypePrestamo
import com.paparazziapps.pretamistapp.application.MyPreferences
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.PaymentScheduledEnum
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(),SetOnClickedLoan {

    private val viewModel by viewModel<ViewModelDashboard>()

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    private val preferences: MyPreferences by inject()
    private var loanAdapter = LoanAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        setOnClickedLoanHome = this

        //Configuration
        setupRecyclerLoans()
        observers()
        return view
    }

    private fun setupRecyclerLoans() {
        binding.recyclerPrestamos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = loanAdapter
        }
    }

    private fun observers() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(
                lifecycle = lifecycle,
                minActiveState = androidx.lifecycle.Lifecycle.State.STARTED
            ).collectLatest(::onStateChange)
        }
    }

    private fun onStateChange(state: ViewModelDashboard.DashboardState) {
        when(state.state) {
            ViewModelDashboard.DashboardEvent.LOADING -> {
                with(binding) {
                    emptyPrestamo.isVisible = false
                    scrollPrestamos.isVisible = false
                    errorContainer.errorContainer.isVisible = false
                    loadingContainer.loadingContainer.isVisible = true
                }
            }
            ViewModelDashboard.DashboardEvent.SUCCESS -> {
                with(binding){
                    emptyPrestamo.isVisible = false
                    errorContainer.errorContainer.isVisible = false
                    loadingContainer.loadingContainer.isVisible = false
                    scrollPrestamos.isVisible = true
                }

                state.loans?.let {
                    updateLoans(it.toMutableList())
                }

            }
            ViewModelDashboard.DashboardEvent.ERROR -> {
                with(binding) {
                    emptyPrestamo.isVisible = false
                    scrollPrestamos.isVisible = false
                    loadingContainer.loadingContainer.isVisible = false
                    errorContainer.errorContainer.isVisible = true
                }
            }

            ViewModelDashboard.DashboardEvent.EMPTY -> {
                with(binding){
                    scrollPrestamos.isVisible = false
                    errorContainer.errorContainer.isVisible = false
                    loadingContainer.loadingContainer.isVisible = false
                    emptyPrestamo.isVisible = true
                }
            }
        }
    }

    private fun updateLoans(loans:MutableList<LoanDomain>){
        loanAdapter.setData(loans)
    }

    private fun showMessage(message:String) {
        showMessageAboveMenuInferiorGlobal(message,binding.root)
    }





    companion object {
        var setOnClickedLoanHome:SetOnClickedLoan? = null
    }

    override fun updateLoanPaid(loanDomain: LoanDomain, needUpdate:Boolean, totalAmountToPay:Double, adapterPosition:Int, daysDelayed:String) {
        context.apply {
            (this as PrincipalActivity).showBottomSheetDetallePrestamoPrincipal(loanDomain, totalAmountToPay, daysDelayed, adapterPosition, needUpdate)
        }
    }

    override fun openDialogUpdateLoan(loanDomain: LoanDomain, totalAmountToPay: Double, adapterPosition: Int, daysMissingToPay:Int, paidDays:Int, isClosed:Boolean) {

        binding.cntCortina.isVisible = true

        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialogBackground)
        val view : View   = layoutInflater.inflate(R.layout.dialog_salir_sin_guardar, null)
        val bindingDialogSalir = DialogSalirSinGuardarBinding.bind(view)

        val title       = bindingDialogSalir.textView
        val desc        = bindingDialogSalir.lblDescSalirNoticias
        val btnPositive   = bindingDialogSalir.btnAceptarSalir
        val btnNegative = bindingDialogSalir.btnCancelarSalir

        if(isClosed) {
            title.text = "¿Estas seguro de cerrar el préstamo?"
            desc.text  = ("Se cerrára el préstamo de: <b>${replaceFirstCharInSequenceToUppercase(loanDomain.nombres.toString())}, ${replaceFirstCharInSequenceToUppercase(loanDomain.apellidos.toString())}").fromHtml()
        }else {
            title.text = "¿Estas seguro de actualizar la deuda?"
            desc.text  = ("Se actualizará la deuda de: <b>${replaceFirstCharInSequenceToUppercase(loanDomain.nombres.toString())}, ${replaceFirstCharInSequenceToUppercase(loanDomain.apellidos.toString())} </b>" +
                    ",con un monto total a pagar de: <br><b>${getString(R.string.tipo_moneda)}${totalAmountToPay}<b>").fromHtml()
        }

        dialogBuilder.apply {
            setView(view)
        }

        val dialog = dialogBuilder.create()
        dialog.apply {
            setCanceledOnTouchOutside(false)
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window?.setGravity(Gravity.CENTER)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setOnDismissListener {
                binding.cntCortina.visibility = View.GONE
            }
            show()
        }


        btnPositive.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                dialog.dismiss()
                if (isClosed) {
                    viewModel.closeLoan(loanDomain.id){
                            isCorrect, msj, result, isRefresh ->
                        if(isCorrect) {
                            loanAdapter.removeItem(adapterPosition)//remover item de  local recycler View
                            showMessage(msj)
                        }else {
                            showMessage(msj)
                        }
                    }
                }else {

                    viewModel.updateUltimoPago(
                        loanDomain = loanDomain, loanDomain.id,
                        getFechaActualNormalCalendar(),
                        totalAmountToPay,
                        daysMissingToPay,
                        paidDays){
                            isCorrect, msj, result, isRefresh ->

                        if(isCorrect) {
                            //Here you can update the item in the recycler view with the new data
                            val loanType = PaymentScheduled.getPaymentScheduledById(loanDomain.typeLoan ?: INT_DEFAULT)
                            loanDomain.fechaUltimoPago = getFechaActualNormalCalendar()
                            when(loanType) {
                                PaymentScheduledEnum.DAILY -> {
                                    loanDomain.dias_restantes_por_pagar = daysMissingToPay
                                    loanDomain.diasPagados = paidDays
                                    loanAdapter.updateItem(adapterPosition, loanDomain)
                                }
                                else -> {
                                    //calculateTheNewDaysPaid
                                    val paidDaysBefore = loanDomain.diasPagados?:0
                                    val quotesPaidBefore = loanDomain.quotasPaid?:0
                                    val currentLoanDays = PaymentScheduled.getPaymentScheduledById(loanDomain.typeLoan?: INT_DEFAULT).days
                                    val newCurrentPaidDays = paidDaysBefore + (currentLoanDays.times(paidDays))

                                    Log.d("", "Dias pagados antes: $paidDaysBefore")
                                    loanDomain.quotasPending = daysMissingToPay
                                    loanDomain.quotasPaid = paidDays
                                    loanDomain.diasPagados = paidDays.times(currentLoanDays)
                                    loanDomain.dias_restantes_por_pagar = daysMissingToPay.times(currentLoanDays)
                                    loanAdapter.updateItem(adapterPosition, loanDomain)
                                }
                            }
                            showMessage(msj)
                        }else {
                            showMessage(msj)
                        }
                    }
                }
            }
        }

        btnNegative.apply {
            visibility = View.VISIBLE
            isAllCaps = false
            setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}