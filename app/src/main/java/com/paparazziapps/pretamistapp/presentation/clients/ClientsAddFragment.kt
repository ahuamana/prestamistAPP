package com.paparazziapps.pretamistapp.presentation.clients

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.api.Api.Client
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentClientsAddBinding
import com.paparazziapps.pretamistapp.helper.PADialogFactory
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import com.paparazziapps.pretamistapp.helper.hideKeyboardActivity
import com.paparazziapps.pretamistapp.helper.isValidEmail
import com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.presentation.profile.viewmodels.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ClientsAddFragment : Fragment() {

    private val viewModel by viewModel<ClientsAddViewModel>()

    private var _binding: FragmentClientsAddBinding? = null
    private val binding get() = _binding!!

    private val typeDocuments = listOf("DNI", "CE", "PASSPORT", "OTHER")

    private val loadingDialog by lazy {
        PADialogFactory(requireContext()).createLoadingDialog()
    }

    private val generalErrorDialog by lazy {
        PADialogFactory(requireContext()).createGeneralErrorDialog(
            onRetryClick = {
                viewModel.processIntent(ClientsAddViewModel.ClientsAddIntent.None)
            }
        )
    }

    private val generalSuccessDialog by lazy {
        PADialogFactory(requireContext()).createGeneralSuccessDialog(
            successMessage = getString(R.string.operation_success_message),
            buttonTitle = getString(R.string.continue_button_message),
            onConfirmClick = {
                viewModel.processIntent(ClientsAddViewModel.ClientsAddIntent.None)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClientsAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Code
        setupButtons()
        setupObservers()
        setupSpinners()
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(requireContext(), R.layout.select_items, typeDocuments)
        with(binding.typeDocumentEditText) {
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                binding.typeDocumentEditText.showDropDown()
            }
        }

        binding.typeDocumentLayout.apply {
            setEndIconOnClickListener {
                binding.typeDocumentEditText.showDropDown()
            }
            setErrorIconOnClickListener {
                binding.typeDocumentEditText.showDropDown()
            }
        }



    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle).collectLatest { uiState ->
                when(uiState){
                    is BaseViewModel.UiState.Error -> {
                        loadingDialog.dismiss()
                        generalErrorDialog.show()
                    }
                    BaseViewModel.UiState.GenericError -> {
                        loadingDialog.dismiss()
                        generalErrorDialog.show()
                    }
                    BaseViewModel.UiState.Idle -> {
                        //Do nothing
                    }
                    BaseViewModel.UiState.Loading -> {
                        loadingDialog.show()
                    }
                    is BaseViewModel.UiState.Success<*> -> {
                        loadingDialog.dismiss()
                        generalSuccessDialog.show()
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.saveMessageButton.setOnClickListener {
            hideKeyboardActivity(requireActivity())
            handledSaveMessage()
        }

    }

    private fun handledSaveMessage() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val lastName = binding.lastNameEditText.text.toString()
        val document = binding.documentEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()

        val typeDocument = binding.typeDocumentEditText.text.toString()


        binding.namesLayout.error = when {
            name.isEmpty() -> {
                getString(R.string.error_name_empty)
            }
            else -> null
        }

        binding.emailLayout.error = when {
            email.isEmpty() -> {
                getString(R.string.error_email_empty)
            }
            !isValidEmail(email) -> {
                getString(R.string.error_email_invalid)
            }
            else -> null
        }

        binding.lastNameLayout.error = when {
            lastName.isEmpty() -> {
                getString(R.string.error_last_name_empty)
            }
            else -> null
        }

        binding.documentLayout.error = when {
            document.isEmpty() -> {
                getString(R.string.error_document_empty)
            }
            else -> null
        }

        binding.phoneLayout.error = when {
            phone.isEmpty() -> {
                getString(R.string.error_phone_empty)
            }
            else -> null
        }

        binding.typeDocumentLayout.error = when {
            typeDocument.isEmpty() -> {
                getString(R.string.error_type_document_empty)
            }
            else -> null
        }

        val note = binding.noteEditText.text.toString()


        if (name.isNotEmpty()
            && email.isNotEmpty()
            && lastName.isNotEmpty()
            && document.isNotEmpty()
            && phone.isNotEmpty()
            && isValidEmail(email)
            && typeDocument.isNotEmpty()
            ) {
            viewModel.processIntent(
                ClientsAddViewModel.ClientsAddIntent.SaveClientIntent(
                    typeDocument = typeDocument,
                    document = document,
                    name = name,
                    email = email,
                    phone = phone,
                    lastName = lastName,
                    notes = note
                )
            )
        } else {
            Log.d("ClientsAddFragment", "Error in the form")
        }



    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}