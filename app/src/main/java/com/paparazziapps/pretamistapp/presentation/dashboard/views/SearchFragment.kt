package com.paparazziapps.pretamistapp.presentation.dashboard.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.databinding.DialogSalirSinGuardarBinding
import com.paparazziapps.pretamistapp.databinding.FragmentDetailReceiptBinding
import com.paparazziapps.pretamistapp.databinding.FragmentSearchBinding
import com.paparazziapps.pretamistapp.domain.InformationReceiptDomain
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PAConstants
import com.paparazziapps.pretamistapp.helper.PADialogFactory
import com.paparazziapps.pretamistapp.helper.fromHtml
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase
import com.paparazziapps.pretamistapp.presentation.dashboard.adapters.LoanAdapter
import com.paparazziapps.pretamistapp.presentation.dashboard.interfaces.SetOnClickedLoan
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelSearch
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelSearch.SearchIntent
import com.paparazziapps.pretamistapp.presentation.dashboard.views.HomeFragment.Companion.setOnClickedLoanHome
import com.paparazziapps.pretamistapp.presentation.principal.views.PrincipalActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.getValue


class SearchFragment : Fragment(),SetOnClickedLoan {

    private val viewModel by viewModel<ViewModelSearch>()
    private var loanAdapter = LoanAdapter(this)
    private var _binding: FragmentSearchBinding?= null
    private val binding get() = _binding!!

    var fechaInicioUnixtime:Long?= null
    private val loadingDialog by lazy {
        PADialogFactory(requireContext()).createLoadingDialog()
    }

    private val generalErrorDialog by lazy {
        PADialogFactory(requireContext()).createGeneralErrorDialog(
            onRetryClick = {
                viewModel.processIntent(ViewModelSearch.SearchIntent.ResetStatusDialogs)
            }
        )
    }

    private val generalSuccessDialog by lazy {
        PADialogFactory(requireContext()).createGeneralSuccessDialog(
            successMessage = getString(R.string.operation_success_message),
            buttonTitle = getString(R.string.continue_button_message),
            onConfirmClick = {
                viewModel.processIntent(ViewModelSearch.SearchIntent.ResetStatusDialogs)
            }
        )
    }

    private val successReceiptDialog by lazy {
        PADialogFactory(requireContext()).createGeneralSuccessDialog(
            successMessage = getString(R.string.operation_success_message),
            buttonTitle = getString(R.string.accept),
            onConfirmClick = {
                viewModel.logEvent(PADataConstants.EVENT_SEE_RECEIPT)
                viewModel.processIntent(ViewModelSearch.SearchIntent.ResetStatusDialogs)
                //Navigate to receipt
                val information: InformationReceiptDomain? = viewModel.getInformationReceipt()
                Log.d("TAG", "information: $information")
                val bundle = Bundle()
                bundle.putSerializable(PAConstants.INFORMATION_RECEIPT, information)
                findNavController().navigate(R.id.action_navigation_search_to_action_detail_receipt, bundle)

            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerLoans()
        setupSearch()
        observers()
        otherComponents()

        setOnClickedLoanHome = this
    }

    private fun otherComponents() {
        binding.fechaInicio.setOnClickListener {
            getCalendar()
        }

        binding.fechaInicioLayout.setEndIconOnClickListener {
            getCalendar()
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.processIntent(ViewModelSearch.SearchIntent.SearchLoanByName(query?:""))
                    Toast.makeText(requireContext(), "Buscando...", Toast.LENGTH_SHORT).show()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                   return true
                }

            }
        )
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

    private fun onStateChange(state: ViewModelSearch.SearchState) {
        when(state.state) {
            ViewModelSearch.SearchEvent.LOADING -> {
                with(binding) {
                    emptyPrestamo.isVisible = false
                    cardPrestamos.isVisible = false
                    errorContainer.errorContainer.isVisible = false
                    loadingContainer.loadingContainer.isVisible = true
                }
            }
            ViewModelSearch.SearchEvent.SUCCESS -> {
                with(binding){
                    emptyPrestamo.isVisible = false
                    errorContainer.errorContainer.isVisible = false
                    loadingContainer.loadingContainer.isVisible = false
                    cardPrestamos.isVisible = true
                }

                state.loans?.let {
                    updateLoans(it.toMutableList())
                }

            }
            ViewModelSearch.SearchEvent.ERROR -> {
                with(binding) {
                    emptyPrestamo.isVisible = false
                    cardPrestamos.isVisible = false
                    loadingContainer.loadingContainer.isVisible = false
                    errorContainer.errorContainer.isVisible = true
                }
            }

            ViewModelSearch.SearchEvent.EMPTY -> {
                with(binding){
                    cardPrestamos.isVisible = false
                    errorContainer.errorContainer.isVisible = false
                    loadingContainer.loadingContainer.isVisible = false
                    emptyPrestamo.isVisible = true
                }
            }
        }

        when(state.dialogState){
            is ViewModelSearch.SearchDialogState.Error -> {
                loadingDialog.dismiss()
                generalSuccessDialog.dismiss()
                generalErrorDialog.show()
            }
            ViewModelSearch.SearchDialogState.ErrorCloseLoan -> {
                loadingDialog.dismiss()
                generalSuccessDialog.show()
                generalErrorDialog.show()
            }
            ViewModelSearch.SearchDialogState.Loading -> {
                loadingDialog.show()
            }
            ViewModelSearch.SearchDialogState.None -> {
                loadingDialog.dismiss()
                generalErrorDialog.dismiss()
                generalSuccessDialog.dismiss()
                successReceiptDialog.dismiss()
            }
            ViewModelSearch.SearchDialogState.SuccessCloseLoan -> {
                generalErrorDialog.dismiss()
                loadingDialog.dismiss()
                generalSuccessDialog.show()
            }

            ViewModelSearch.SearchDialogState.SuccessUpdateLoan -> {
                generalErrorDialog.dismiss()
                loadingDialog.dismiss()
                generalSuccessDialog.dismiss()
                successReceiptDialog.show()

            }

            ViewModelSearch.SearchDialogState.SuccessSendMessage ->{
                generalErrorDialog.dismiss()
                loadingDialog.dismiss()
                successReceiptDialog.dismiss()
                generalSuccessDialog.show()
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun getCalendar() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleciona una fecha")
                .build()

        datePicker.show(parentFragmentManager, "Datepickerdialog");

        datePicker.addOnPositiveButtonClickListener { unixtime ->
            println("UnixTime selected -- ${unixtime}")
            fechaInicioUnixtime = unixtime
            SimpleDateFormat("dd/MM/yyyy").apply {
                timeZone = TimeZone.getTimeZone("GMT")
                format(unixtime).toString().also {
                     binding.fechaInicio.setText(it)
                    viewModel.processIntent(ViewModelSearch.SearchIntent.SearchLoanByNextPayment(unixtime))
                }
            }

        }
    }

    private fun updateLoans(loans:MutableList<LoanDomain>){
        loanAdapter.setData(loans)
    }

    override fun updateLoanPaid(
        loanDomain: LoanDomain,
        needUpdate: Boolean,
        totalAmountToPay: Double,
        adapterPosition: Int,
        daysDelayed: String
    ) {
        context.apply {
            (this as PrincipalActivity).showBottomSheetDetallePrestamoPrincipal(loanDomain, totalAmountToPay, daysDelayed, adapterPosition, needUpdate)
        }
    }

    override fun openDialogUpdateLoan(
        loanDomain: LoanDomain,
        quotesToPay: Int
    ) {
        binding.cntCortina.isVisible = true

        val needToClose = loanDomain.quotasPending == quotesToPay //Works for daily and other
        val amountToPay = quotesToPay.times(loanDomain.amountPerQuota ?: 0.0) // rename to quotaAmount // works for daily and other

        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialogBackground)
        val view : View   = layoutInflater.inflate(R.layout.dialog_salir_sin_guardar, null)
        val bindingDialogSalir = DialogSalirSinGuardarBinding.bind(view)

        val title       = bindingDialogSalir.textView
        val desc        = bindingDialogSalir.lblDescSalirNoticias
        val btnPositive   = bindingDialogSalir.btnAceptarSalir
        val btnNegative = bindingDialogSalir.btnCancelarSalir

        if(needToClose) {
            title.text = "¿Estas seguro de cerrar el préstamo?"
            desc.text  = ("Se cerrára el préstamo de: <b>${replaceFirstCharInSequenceToUppercase(loanDomain.names.toString())}, ${replaceFirstCharInSequenceToUppercase(loanDomain.lastnames.toString())}").fromHtml()
        }else {
            title.text = "¿Estas seguro de actualizar la deuda?"
            desc.text  = ("Se actualizará la deuda de: <b>${replaceFirstCharInSequenceToUppercase(loanDomain.names.toString())}, ${replaceFirstCharInSequenceToUppercase(loanDomain.lastnames.toString())} </b>" +
                    ",con un monto total a pagar de: <br><b>${getString(R.string.tipo_moneda)}${amountToPay}<b>").fromHtml()
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



                val intent = if(needToClose) SearchIntent.CloseLoan(
                    loanId = loanDomain.id ?: "",
                ) else SearchIntent.UpdateLoan(
                    loanDomain = loanDomain,
                    quotesToPay = quotesToPay
                )
                viewModel.processIntent(intent)
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

    override fun sendMessageToOtherApp(loanDomain: LoanDomain) {
        viewModel.processIntent(SearchIntent.SendMessageToOtherApp(loanDomain, requireContext()))

    }

    override fun sendMessageToWhatsapp(loanDomain: LoanDomain) {
        viewModel.processIntent(SearchIntent.SendMessageToWhatsApp(loanDomain, requireContext()))
    }

}