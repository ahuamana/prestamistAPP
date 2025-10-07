package com.paparazziapps.pretamistapp.presentation.dashboard.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.databinding.FragmentDetailReceiptBinding
import com.paparazziapps.pretamistapp.databinding.FragmentSearchBinding
import com.paparazziapps.pretamistapp.domain.InformationReceiptDomain
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.domain.PAConstants
import com.paparazziapps.pretamistapp.helper.PADialogFactory
import com.paparazziapps.pretamistapp.presentation.dashboard.adapters.LoanAdapter
import com.paparazziapps.pretamistapp.presentation.dashboard.interfaces.SetOnClickedLoan
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelSearch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class SearchFragment : Fragment(),SetOnClickedLoan {

    private val viewModel by viewModel<ViewModelSearch>()
    private var loanAdapter = LoanAdapter(this)
    private var _binding: FragmentSearchBinding?= null
    private val binding get() = _binding!!

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
                findNavController().navigate(R.id.action_navigation_home_to_action_detail_receipt, bundle)

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
                    scrollPrestamos.isVisible = false
                    errorContainer.errorContainer.isVisible = false
                    loadingContainer.loadingContainer.isVisible = true
                }
            }
            ViewModelSearch.SearchEvent.SUCCESS -> {
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
            ViewModelSearch.SearchEvent.ERROR -> {
                with(binding) {
                    emptyPrestamo.isVisible = false
                    scrollPrestamos.isVisible = false
                    loadingContainer.loadingContainer.isVisible = false
                    errorContainer.errorContainer.isVisible = true
                }
            }

            ViewModelSearch.SearchEvent.EMPTY -> {
                with(binding){
                    scrollPrestamos.isVisible = false
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
        //TODO("Not yet implemented")
    }

    override fun openDialogUpdateLoan(
        loanDomain: LoanDomain,
        quotesToPay: Int
    ) {
        //TODO("Not yet implemented")
    }

    override fun sendMessageToOtherApp(loanDomain: LoanDomain) {
        //TODO("Not yet implemented")
    }

    override fun sendMessageToWhatsapp(loanDomain: LoanDomain) {
        //TODO("Not yet implemented")
    }

}