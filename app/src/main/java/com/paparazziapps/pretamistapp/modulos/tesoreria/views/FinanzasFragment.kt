package com.paparazziapps.pretamistapp.modulos.tesoreria.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels.ViewModelTesoreria


class FinanzasFragment : Fragment() {

    val _viewModel = ViewModelTesoreria.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finanzas, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FinanzasFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}