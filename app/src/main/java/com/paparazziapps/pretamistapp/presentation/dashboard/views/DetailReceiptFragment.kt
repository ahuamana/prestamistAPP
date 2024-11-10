package com.paparazziapps.pretamistapp.presentation.dashboard.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentDetailReceiptBinding
import com.paparazziapps.pretamistapp.domain.utils.convertUnixTimeToFormattedDate
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDetailReceipt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailReceiptFragment : Fragment() {

    private val viewModel by viewModel<ViewModelDetailReceipt>()

    private var _binding: FragmentDetailReceiptBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getInformationReceipt()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handledObservers()
    }

    private fun handledObservers() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collectLatest(::handleState)
        }
    }

    private fun handleState(state: ViewModelDetailReceipt.DetailReceiptState) {
        when(state) {
            is ViewModelDetailReceipt.DetailReceiptState.Loading -> {
                // Show loading dialog
            }
            is ViewModelDetailReceipt.DetailReceiptState.Success -> {
                state.informationReceipt.let { information ->
                    with(binding){
                        val amountWithPrefix = getString(R.string.type_money_with_amount,information.totalAmountToPay.toString())
                        val firstLetterFirstNameAndLastName = information.names.first().toString().uppercase() + information.lastNames.first().toString().uppercase()
                        tvPaymentDate.text = convertUnixTimeToFormattedDate(information.codeOperation)
                        tvPaymentAmount.text = amountWithPrefix
                        tvLoanId.text = information.codeOperation.toString()
                        tvBorrowerName.text = information.fullName
                        tvBorrowerPhone.text = information.phoneNumber
                        tvBorrowerInitials.text = firstLetterFirstNameAndLastName
                        tvMonthlyPayment.text = getString(R.string.type_money_with_amount, information.amountPerQuote.toString())

                        tvRemainingPayments.text = getString(R.string.type_remaining_payments, information.quotesPaidNew.toString(), information.quotes.toString())



                        //setup progress loan
                        val progress = (information.quotesPaidNew * 100) / information.quotes
                        Log.d("DetailReceiptFragment", "Progress: $progress")
                        loanProgressIndicator.progress = progress
                        tvProgressPercentage.text = getString(R.string.type_percentage, progress.toString())
                    }

                }
            }
            is ViewModelDetailReceipt.DetailReceiptState.Error -> {
                // Show error dialog
            }
        }
    }

}