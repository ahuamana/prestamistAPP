package com.paparazziapps.pretamistapp.modulos.tesoreria.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentFinanzasBinding
import com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels.ViewModelTesoreria


class FinanzasFragment : Fragment() {

    val _viewModel = ViewModelTesoreria.getInstance()

    var _binding:FragmentFinanzasBinding?= null
    private val binding get() = _binding!!

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
        _binding = FragmentFinanzasBinding.inflate(inflater, container,false)
        var view = binding.root

        //all code here
        _viewModel.getPrestamosSize { isCorrect, msj, result, isRefresh ->

            showMessage("Cantidad de prestamos activos: $result")
        }


        return view
    }


    private fun showMessage(message:String)
    {
        Snackbar.make(activity!!.findViewById(R.id.nav_view),"$message", Snackbar.LENGTH_LONG).show()
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