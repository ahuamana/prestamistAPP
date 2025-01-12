package com.paparazziapps.pretamistapp.presentation.clients

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentClientsParentBinding
import com.paparazziapps.pretamistapp.domain.clients.ClientDomain
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import com.paparazziapps.pretamistapp.presentation.clients.adater.ClientAdapter
import com.paparazziapps.pretamistapp.presentation.clients.adater.SetOnClickListenerClient
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ClientsParentFragment : Fragment(), SetOnClickListenerClient {

    private val viewModel by viewModel<ClientsParentViewModel>()

    private var _binding: FragmentClientsParentBinding? = null
    private val binding get() = _binding!!

    private val clientsAdapter: ClientAdapter by lazy { ClientAdapter(this@ClientsParentFragment) }

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
        setupRecyclerView()
        setupObservers()
        setupSearchView()
        viewModel.getClients()
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

    private fun setupRecyclerView() {
        binding.clientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = clientsAdapter
        }
        binding.clientsRecyclerView.setHasFixedSize(true)
        binding.clientsRecyclerView.setItemViewCacheSize(20)
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
                        val clients:List<ClientDomain>? = uiState.data as? List<ClientDomain> // Replace Client with your actual data type
                        clientsAdapter.setClients(clients?: emptyList())
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

    override fun onItemClicked(client: ClientDomain) {
        //Show toast that user dont have permission to edit the client in spanish
        val message = "No tienes permisos para editar al cliente"
        //Show toast
       Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}