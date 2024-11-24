package com.paparazziapps.pretamistapp.presentation.clients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.api.Api.Client
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentClientsAddBinding
import com.paparazziapps.pretamistapp.helper.hideKeyboardActivity
import com.paparazziapps.pretamistapp.helper.isValidEmail
import com.paparazziapps.pretamistapp.presentation.profile.viewmodels.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ClientsAddFragment : Fragment() {

    private val viewModel by viewModel<ClientsAddViewModel>()

    private var _binding: FragmentClientsAddBinding? = null
    private val binding get() = _binding!!

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
            isValidEmail(email) -> {
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


        if (name.isNotEmpty()
            && email.isNotEmpty()
            && lastName.isNotEmpty()
            && document.isNotEmpty()
            && phone.isNotEmpty()
            && !isValidEmail(email)
            ) {
            viewModel.saveClient(
                document = document,
                name = name,
                email = email,
                phone = phone,
                lastName = lastName
            )
        }



    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}