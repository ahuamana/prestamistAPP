package com.paparazziapps.pretamistapp.modulos.dashboard.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentHomeBinding
import com.paparazziapps.pretamistapp.databinding.FragmentRegistrarBinding
import com.paparazziapps.pretamistapp.modulos.dashboard.adapters.PrestamoAdapter
import com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister


class HomeFragment : Fragment() {

    var _viewModel = ViewModelDashboard.getInstance()

    var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    //constructores
    val prestamoAdapter = PrestamoAdapter()

    private lateinit var recyclerPrestamos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        var view = binding.root

        //Link items with layout
        recyclerPrestamos = binding.recyclerPrestamos


        //Configuration
        setupRecyclerPrestamos()

        //Observers
        observers()

        //First time process
        obtenerPrestamosFirstTime()


        return view
    }

    private fun setupRecyclerPrestamos() {



        recyclerPrestamos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = prestamoAdapter
        }
    }

    private fun obtenerPrestamosFirstTime() {
        _viewModel.getPrestamos()
    }

    private fun observers() {
        _viewModel.receivePrestamos().observe(this) {
            if(it.count() == 0)
            {
                binding.emptyPrestamo.isVisible = true
                recyclerPrestamos.isVisible = false
                //showMessage("No hay prestamos")
            }else
            {
                binding.emptyPrestamo.isVisible = false
                prestamoAdapter.setData(it)
                recyclerPrestamos.isVisible = true
                //showMessage(" Lista de prestamos ${it.count()}")
            }
        }
    }

    private fun showMessage(message:String)
    {
        Snackbar.make(activity!!.findViewById(android.R.id.content),"$message", Snackbar.LENGTH_LONG).show()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}