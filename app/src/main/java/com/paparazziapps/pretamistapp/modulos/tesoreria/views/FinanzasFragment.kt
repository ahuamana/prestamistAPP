package com.paparazziapps.pretamistapp.modulos.tesoreria.views

import android.icu.lang.UCharacter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentFinanzasBinding
import com.paparazziapps.pretamistapp.modulos.dashboard.adapters.PrestamoAdapter
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.modulos.tesoreria.adapter.PrestamoDetalleAdapter
import com.paparazziapps.pretamistapp.modulos.tesoreria.viewmodels.ViewModelTesoreria


class FinanzasFragment : Fragment() {

    val _viewModel = ViewModelTesoreria.getInstance()


    var _binding:FragmentFinanzasBinding?= null
    private val binding get() = _binding!!

    val prestamoDetalleAdapter = PrestamoDetalleAdapter()



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
        initialCode()


        observers()

        binding.recyclerDetalle.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
            adapter = prestamoDetalleAdapter
        }


        return view
    }

    private fun observers() {
        _viewModel.receivePrestamos().observe(viewLifecycleOwner) {
            if(it.count() == 0)
            {
                binding.apply {
                    imgEmptyDeuda.isVisible = true
                    lblEmptyDeuda.isVisible = true
                }
                //showMessage("No hay prestamos")
            }else
            {
                binding.apply {
                    imgEmptyDeuda.isVisible = false
                    lblEmptyDeuda.isVisible = false
                    recyclerDetalle.isVisible = true
                }

                prestamoDetalleAdapter.setData(it)
                //showMessage(" Lista de prestamos ${it.count()}")
            }
        }
    }

    private fun initialCode() {

        _viewModel.getPrestamosSize { isCorrect, msj, result, isRefresh ->

            if(isCorrect)
            {
                binding.apply {
                    lblPrestamosActivos.text = "$result activos"
                }
            }
        }

        _viewModel.getPagosHoy() { isCorrect, msj, result, isRefresh ->

            if (isCorrect) {
                binding.apply {
                    lblCajaHoy.text = "${getString(R.string.tipo_moneda)} $result"
                }
            }

        }

        _viewModel.getPagosAyer() { isCorrect, msj, result, isRefresh ->

            if (isCorrect) {
                binding.apply {
                    lblCajaAyer.text = "${getString(R.string.tipo_moneda)} $result"
                }
            }

        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FinanzasFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onDestroy() {
        ViewModelTesoreria.destroyInstance()
        super.onDestroy()
    }
}