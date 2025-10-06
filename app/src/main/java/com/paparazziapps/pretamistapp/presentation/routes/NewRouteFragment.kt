package com.paparazziapps.pretamistapp.presentation.routes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentNewRouteBinding
import com.paparazziapps.pretamistapp.databinding.FragmentRouteParentBinding
import com.paparazziapps.pretamistapp.helper.base.BaseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewRouteFragment : Fragment() {

    private var _binding: FragmentNewRouteBinding ?=null
    private val binding get() = _binding!!

    private val viewModel by viewModel<NewRouteViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewRouteBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when(state) {
                    is BaseViewModel.UiState.Error -> {
                        // Handle error state, e.g., show a toast or dialog
                        binding.routeNameEditText.error = state.exception.message
                    }
                    is BaseViewModel.UiState.Success<*> -> {
                        binding.routeNameEditText.error = null
                        //clear the input field
                        binding.routeNameEditText.setText("")
                        // Optionally, show a success message
                        Toast.makeText(context, "Ruta creada exitosamente", Toast.LENGTH_SHORT).show()

                    }
                    else -> {
                        // No action needed for loading state
                    }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.apply {
            createRouteButton.setOnClickListener {
                val name = routeNameEditText.text.toString().trim()
                if (name.isNotEmpty()) {
                    // Call the ViewModel function to create the route

                    viewModel.createRoute(name)
                    // Optionally, navigate back or show a success message
                } else {
                    routeNameEditText.error = "Route name cannot be empty"
                }
            }
        }
    }

}