package com.paparazziapps.pretamistapp.presentation.clients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentClientsParentBinding

class ClientsParentFragment : Fragment() {

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