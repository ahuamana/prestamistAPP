package com.paparazziapps.pretamistapp.presentation.dashboard.views

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentDetailReceiptBinding
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.utils.convertUnixTimeToFormattedDate
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDetailReceipt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
        handledButtons()
    }

    private fun handledButtons() {
        binding.btnShare.setOnClickListener {
            val bitmap = takeScreenshot(binding.paymentCard)
            val file = saveBitmapToFile(bitmap)
            shareScreenshot(file)
        }
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

                        //Calculate the next payment date
                        if(information.quotesPaidNew < information.quotes){
                            Log.d("DetailReceiptFragment", "Loan Start Date: ${information.loanStartDateUnix} Quotes Paid: ${information.quotesPaidNew} ")
                            val tyLoan = PaymentScheduled.getPaymentScheduledById(information.typeLoan)
                            val nextPaymentDate = information.loanStartDateUnix + (information.quotesPaidNew * tyLoan.days * 86400L * 1000L)
                            Log.d("DetailReceiptFragment", "Next Payment Date: $nextPaymentDate")
                            tvNextPaymentDate.text = convertUnixTimeToFormattedDate(nextPaymentDate)
                        }else {
                            tvNextPaymentDate.text = getString(R.string.type_no_more_payments)
                        }

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

    // Function to capture screenshot of a view
    private fun takeScreenshot(view: View): Bitmap {
        // Make sure the view has been laid out
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        // Create a bitmap of the view
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )

        // Create a canvas with the bitmap
        val canvas = Canvas(bitmap)

        // Draw the view's background
        view.background?.draw(canvas) ?: canvas.drawColor(Color.WHITE)

        // Draw the view onto the canvas
        view.draw(canvas)

        return bitmap
    }

    // Function to save bitmap to file
    private fun saveBitmapToFile(bitmap: Bitmap): File {
        // Create a file in the cache directory
        val file = File(
            requireContext().cacheDir,
            "receipt_${System.currentTimeMillis()}.jpg"
        )

        try {
            // Convert bitmap to JPEG and save to file
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    // Function to share the screenshot
    private fun shareScreenshot(file: File) {
        // Create the sharing URI using FileProvider
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Compartir recibo"))
    }

}