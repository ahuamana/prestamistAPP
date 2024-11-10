package com.paparazziapps.pretamistapp.presentation.dashboard.views

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.DialogSalirSinGuardarBinding
import com.paparazziapps.pretamistapp.databinding.FragmentHomeBinding
import com.paparazziapps.pretamistapp.domain.InformationReceiptDomain
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.presentation.dashboard.adapters.LoanAdapter
import com.paparazziapps.pretamistapp.presentation.dashboard.interfaces.SetOnClickedLoan
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.presentation.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.domain.LoanDomain
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(),SetOnClickedLoan {

    private val viewModel by viewModel<ViewModelDashboard>()

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!
    private var loanAdapter = LoanAdapter(this)

    private val loadingDialog by lazy {
        PADialogFactory(requireContext()).createLoadingDialog()
    }

    private val generalErrorDialog by lazy {
        PADialogFactory(requireContext()).createGeneralErrorDialog(
            onRetryClick = {
                viewModel.processIntent(ViewModelDashboard.DashboardIntent.ResetStatusDialogs)
            }
        )
    }

    private val generalSuccessDialog by lazy {
        PADialogFactory(requireContext()).createGeneralSuccessDialog(
            successMessage = getString(R.string.loan_closed_sucefully_message),
            buttonTitle = getString(R.string.accept),
            onConfirmClick = {
                viewModel.processIntent(ViewModelDashboard.DashboardIntent.ResetStatusDialogs)
                //Navigate to receipt
                val information: InformationReceiptDomain? = viewModel.getInformationReceipt()
                Log.d("TAG", "information: $information")

                val bundle = Bundle()
                bundle.putSerializable("informationReceipt", information)
                findNavController().navigate(R.id.action_navigation_home_to_action_detail_receipt, bundle)

            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        setOnClickedLoanHome = this

        //Configuration
        requestPermissionsSms()
        setupRecyclerLoans()
        observers()
        return view
    }

    private fun requestPermissionsSms() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Request SMS permission if not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.SEND_SMS),
                123
            )
        }else {
            // Permission already granted
            Log.d("TAG", "Permission already granted")
        }
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

        when(state.dialogState){
            is ViewModelDashboard.DashboardDialogState.Error -> {
                loadingDialog.dismiss()
                generalSuccessDialog.dismiss()
                generalErrorDialog.show()
            }
            ViewModelDashboard.DashboardDialogState.ErrorCloseLoan -> {
                loadingDialog.dismiss()
                generalSuccessDialog.show()
                generalErrorDialog.show()
            }
            ViewModelDashboard.DashboardDialogState.Loading -> {
                loadingDialog.show()
            }
            ViewModelDashboard.DashboardDialogState.None -> {
                loadingDialog.dismiss()
                generalErrorDialog.dismiss()
                generalSuccessDialog.dismiss()
            }
            ViewModelDashboard.DashboardDialogState.SuccessCloseLoan -> {
                generalErrorDialog.dismiss()
                loadingDialog.dismiss()
                generalSuccessDialog.show()
            }

            ViewModelDashboard.DashboardDialogState.SuccessUpdateLoan -> {
                generalErrorDialog.dismiss()
                loadingDialog.dismiss()
                generalSuccessDialog.show()
            }
        }

    }

    private fun updateLoans(loans:MutableList<LoanDomain>){
        loanAdapter.setData(loans)
    }

    companion object {
        var setOnClickedLoanHome:SetOnClickedLoan? = null
    }

    override fun updateLoanPaid(loanDomain: LoanDomain, needUpdate:Boolean, totalAmountToPay:Double, adapterPosition:Int, daysDelayed:String) {
        context.apply {
            (this as PrincipalActivity).showBottomSheetDetallePrestamoPrincipal(loanDomain, totalAmountToPay, daysDelayed, adapterPosition, needUpdate)
        }
    }

    override fun openDialogUpdateLoan(
        loanDomain: LoanDomain,
        quotesToPay:Int,
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

                val intent = ViewModelDashboard.DashboardIntent.UpdateLoan(
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
        viewModel.processIntent(ViewModelDashboard.DashboardIntent.SendMessageToOtherApp(loanDomain, requireContext()))
    }

    override fun sendMessageToWhatsapp(loanDomain: LoanDomain) {
        viewModel.processIntent(ViewModelDashboard.DashboardIntent.SendMessageToWhatsApp(loanDomain, requireContext()))
    }

    override fun onDestroy() {
        loadingDialog.dismiss()
        generalErrorDialog.dismiss()
        generalSuccessDialog.dismiss()
        super.onDestroy()
    }
}