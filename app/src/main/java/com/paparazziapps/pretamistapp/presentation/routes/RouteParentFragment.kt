package com.paparazziapps.pretamistapp.presentation.routes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentRouteParentBinding
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import com.paparazziapps.pretamistapp.presentation.routes.adapter.OnRouteClickListener
import com.paparazziapps.pretamistapp.presentation.routes.adapter.RouteAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RouteParentFragment : Fragment(), OnRouteClickListener {

    private val viewModel by viewModel<RouteParentViewModel>()

    private var _binding: FragmentRouteParentBinding?=null
    private val binding get() = _binding!!

    private val routeAdapter by lazy {
        RouteAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteParentBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Code
        setupButtons()
        setupRecycler()
        observers()
        viewModel.getAllRoutes("")
    }

    private fun setupRecycler() {
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = routeAdapter
            }
        }
    }

    private fun observers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                handleUIState(state)
            }


        }

        lifecycleScope.launch {
            viewModel.routes.collect { routes ->
                routeAdapter.setData(routes.toMutableList())
            }
        }

    }

    private fun handleUIState(state: BaseViewModel.UiState) {
        when (state) {
            is BaseViewModel.UiState.Loading -> {
                // TODO: Show loading indicator
            }
            is BaseViewModel.UiState.Success<*> -> state.data.run{
                // TODO: update UI with data
            }
            is BaseViewModel.UiState.Error -> {
                // Show error message
                state.exception.printStackTrace()
            }

            BaseViewModel.UiState.GenericError -> {
                // Show generic error message
            }
            BaseViewModel.UiState.Idle -> {
                // Initial state, do nothing or setup initial UI
            }
        }
    }

    private fun setupButtons() {
        binding.apply {
            addRouteButton.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_routes_to_navigation_add_route)
            }
        }
    }

    override fun onRouteClick(routeId: String) {
        Toast.makeText(context, "Clicked route ID: $routeId", Toast.LENGTH_SHORT).show()
    }

}