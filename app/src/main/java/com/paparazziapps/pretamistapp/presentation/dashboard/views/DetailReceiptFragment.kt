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
import com.google.firebase.analytics.FirebaseAnalytics
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.data.PADataConstants
import com.paparazziapps.pretamistapp.databinding.FragmentDetailReceiptBinding
import com.paparazziapps.pretamistapp.domain.PaymentScheduled
import com.paparazziapps.pretamistapp.domain.utils.convertUnixTimeToFormattedDate
import com.paparazziapps.pretamistapp.domain.utils.roundToOneDecimal
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.getDoubleWithTwoDecimalsReturnDouble
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDetailReceipt
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.graphics.createBitmap
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset


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
        setupButtonsShare()
    }

    private fun setupButtonsShare() {
        binding.btnShare.setOnClickListener {
            viewModel.logEvent(PADataConstants.EVENT_SHARE_RECEIPT_OTHER_APPS)
            val bitmap = takeScreenshot(binding.paymentCard)
            val file = saveBitmapToFile(bitmap)
            shareScreenshot(file)
        }

        binding.btnWhatsapp.setOnClickListener {
            //add a toast with message in spanish about it will be implemented in future
            viewModel.logEvent(PADataConstants.EVENT_SHARE_RECEIPT_WHATSAPP)
            Toast.makeText(requireContext(), "Esta funcionalidad estará disponible próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handledObservers() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collectLatest(::handleState)
        }
    }

    private val STORAGE_ZONE: ZoneId = ZoneOffset.UTC
    // Business/display zone:
    private val BUSINESS_ZONE: ZoneId = ZoneId.of("America/Lima")

    private fun startOfBusinessDayFromStoredUTC(millis: Long): Long {
        val ms = if (millis < 1_000_000_000_000L) millis * 1000L else millis
        // 1) Read the stored instant as UTC, get its calendar date
        val dateUtc = Instant.ofEpochMilli(ms).atZone(STORAGE_ZONE).toLocalDate()
        // 2) Re-anchor that *date* to local midnight in your business zone
        return dateUtc.atStartOfDay(BUSINESS_ZONE).toInstant().toEpochMilli()
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
                            // normalize start to beginning of local day to avoid timezone drift
                            val startMillis = startOfBusinessDayFromStoredUTC(information.loanStartDateUnix)

                            val nextPaymentDate = startMillis + ((information.quotesPaidNew + 1) * tyLoan.days * 86400L * 1000L)
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

                        //Loan progress information
                        val totalPaid = getDoubleWithTwoDecimalsReturnDouble(information.amountPerQuote * information.quotesPaidNew)
                        val totalToPay = getDoubleWithTwoDecimalsReturnDouble(information.amountPerQuote * information.quotes)

                        tvTotalPaid.text = getString(R.string.type_money_with_amount, totalPaid.toString())
                        tvTotalAmount.text = getString(R.string.type_money_with_amount, totalToPay.toString())
                    }

                }
            }
            is ViewModelDetailReceipt.DetailReceiptState.Error -> {
                // Show error dialog
            }
        }
    }

    // Function to get the start of the local day in milliseconds in GMT
    // Start of the SAME local day as `millis`, returned as an absolute UTC instant (epoch millis).
    private fun startOfLocalDay(millis: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = if (millis < 1_000_000_000_000L) millis * 1000L else millis // seconds → ms safety
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis            // ✅ no offset subtraction
    }

    // Function to capture screenshot of a view
    private fun takeScreenshot(view: View): Bitmap {
        // Make sure the view has been laid out
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        // Create a bitmap of the view
        val bitmap = createBitmap(view.width, view.height)

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