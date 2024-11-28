package com.paparazziapps.pretamistapp.presentation.registro.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentSelectUserBinding
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import com.paparazziapps.pretamistapp.domain.clients.ClientDomainSelect
import com.paparazziapps.pretamistapp.domain.clients.toClientDomainSelect
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import com.paparazziapps.pretamistapp.presentation.registro.adapter.ClientSelectAdapter
import com.paparazziapps.pretamistapp.presentation.registro.adapter.OnClientSelectListener
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SelectUserFragment : Fragment(), OnClientSelectListener {

    private var _binding: FragmentSelectUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SelectUserViewModel>()

    private val clientsAdapter: ClientSelectAdapter by lazy { ClientSelectAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupSearchView()
        setupButtons()
        setupAdapter()
        viewModel.getClients()
    }

    private fun setupAdapter() {
        binding.clientsRecyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = clientsAdapter
        }

    }

    private fun setupButtons() {
        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_select_user_to_navigation_register_loan)
        }
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
                        val clients:List<ClientDomainSelect>? = uiState.data as? List<ClientDomainSelect> // Replace Client with your actual data type
                        clientsAdapter.setClients(clients ?: listOf())
                    }
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchClients(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()) {
                    viewModel.getClients()
                }
                return true
            }
        })
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onClientSelect(client: ClientDomainSelect) {
        viewModel.saveClient(client)
    }
}