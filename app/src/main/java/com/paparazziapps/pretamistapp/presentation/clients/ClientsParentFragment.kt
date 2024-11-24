package com.paparazziapps.pretamistapp.presentation.clients

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentClientsParentBinding
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClientsParentFragment : Fragment() {

    private val viewModel by viewModel<ClientsParentViewModel>()

    private var _binding: FragmentClientsParentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClientsParentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Code
        setupButtons()
        setupObservers()

        viewModel.getClients()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is BaseViewModel.UiState.Error -> {
                        Log.e("ClientsParentFragment", "Error ${uiState.exception}")
                    }
                    BaseViewModel.UiState.GenericError -> {
                        Log.e("ClientsParentFragment", "Generic error")
                    }
                    BaseViewModel.UiState.Idle -> {
                        Log.e("ClientsParentFragment", "Idle")
                    }
                    BaseViewModel.UiState.Loading -> {
                        Log.e("ClientsParentFragment", "Loading")
                    }
                    is BaseViewModel.UiState.Success<*> -> {
                        val clients:List<ClientDomain> = uiState.data as List<ClientDomain> // Replace Client with your actual data type
                        Log.e("ClientsParentFragment", "Success $clients")
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.addClientButton.setOnClickListener {
            findNavController().navigate(R.id.action_clients_menu_to_clients_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}