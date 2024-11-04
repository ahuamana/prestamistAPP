package com.paparazziapps.pretamistapp.helper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.PaCustomGeneralErrorDialogBinding
import com.paparazziapps.pretamistapp.databinding.PaCustomLoadingDialogBinding
import com.paparazziapps.pretamistapp.databinding.PaCustomSuccessGeneralDialogBinding

class PADialogFactory(private val context: Context) {

    /**
     * Creates and returns a loading dialog without showing it.
     *
     * @param message Optional loading message to display.
     * @return A Dialog instance.
     */
    fun createLoadingDialog(message: String? = context.getString(R.string.loading_dialog_message)): Dialog {
        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            // Inflate the loading dialog layout using View Binding
            val binding = PaCustomLoadingDialogBinding.inflate(layoutInflater)
            setContentView(binding.root) // Set the root view of the binding
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set the loading message in the TextView
            binding.loadingMessage.text = message
        }
        return dialog
    }

    /**
     * Creates and returns a general error dialog with a retry button.
     *
     * @param errorMessage The error message to be displayed.
     * @param onRetryClick Listener for retry button click.
     * @return An AlertDialog instance.
     */
    fun createGeneralErrorDialog(
        onRetryClick: (() -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)

            // Inflate the general error dialog layout using View Binding
            val binding = PaCustomGeneralErrorDialogBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Set up the retry button listener
            binding.btnRetry.setOnClickListener {
                onRetryClick?.invoke()
                dismiss()
            }


            binding.root.setOnClickListener {
                onRetryClick?.invoke()
                dismiss()
            }

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

    /**
     * Creates and returns a general success dialog.
     *
     * @param successMessage The success message to be displayed.
     * @param onConfirmClick Listener for the confirmation button click.
     * @return An AlertDialog instance.
     */
    fun createGeneralSuccessDialog(
        successMessage: String,
        onConfirmClick: (() -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)

            // Inflate the success dialog layout using View Binding
            val binding = PaCustomSuccessGeneralDialogBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Set the success message in the TextView
            binding.successMessage.text = successMessage

            // Set up the confirmation button listener
            binding.btnConfirm.setOnClickListener {
                onConfirmClick?.invoke()
                dismiss()
            }

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        return dialog
    }

    /**
     * Creates and returns a simple alert dialog with a title and message.
     *
     * @param title Title of the dialog.
     * @param message Message to be displayed in the dialog.
     * @param positiveButtonText Text for the positive button.
     * @param negativeButtonText Text for the negative button.
     * @param onPositiveClick Listener for positive button click.
     * @param onNegativeClick Listener for negative button click.
     * @return An AlertDialog instance.
     */
    fun createAlertDialog(
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        negativeButtonText: String = "Cancel",
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveClick?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                onNegativeClick?.invoke()
                dialog.dismiss()
            }
        return builder.create()
    }

    // You can add more dialog types as needed.
}