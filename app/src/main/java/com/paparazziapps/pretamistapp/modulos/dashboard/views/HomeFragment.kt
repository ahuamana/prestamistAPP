package com.paparazziapps.pretamistapp.modulos.dashboard.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.DialogSalirSinGuardarBinding
import com.paparazziapps.pretamistapp.databinding.FragmentHomeBinding
import com.paparazziapps.pretamistapp.helper.fromHtml
import com.paparazziapps.pretamistapp.helper.getFechaActualNormalCalendar
import com.paparazziapps.pretamistapp.helper.replaceFirstCharInSequenceToUppercase
import com.paparazziapps.pretamistapp.modulos.dashboard.adapters.PrestamoAdapter
import com.paparazziapps.pretamistapp.modulos.dashboard.interfaces.setOnClickedPrestamo
import com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister


class HomeFragment : Fragment(),setOnClickedPrestamo {

    var _viewModel = ViewModelDashboard.getInstance()
    var _viewModelregister = ViewModelRegister.getInstance()

    var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    //constructores
    val prestamoAdapter = PrestamoAdapter(this)

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

        setOnClickedPrestamoHome = this

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
        Snackbar.make(activity!!.findViewById(R.id.nav_view),"$message", Snackbar.LENGTH_LONG).show()
    }

    private fun showShortMessage(message:String)
    {
        Snackbar.make(activity!!.findViewById(R.id.nav_view),"$message", Snackbar.LENGTH_SHORT).show()
    }

    fun openDialogActualizarPago(prestamo: Prestamo, montoTotalAPagar: String, adapterPosition:Int) {




    }


    companion object {

        var setOnClickedPrestamoHome:setOnClickedPrestamo? = null

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {

        ViewModelRegister.destroyInstance()
        ViewModelDashboard.destroyInstance()
        super.onDestroy()
    }

    //->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Metodos override
    override fun actualizarPagoPrestamo(prestamo: Prestamo, needUpdate:Boolean, montoTotalAPagar:String, adapterPosition:Int, diasRestrasado:String) {
        //println("Hizo click en Actualizar Pago Prestamos")

        context.apply {
            (this as PrincipalActivity).showBottomSheetDetallePrestamoPrincipal(prestamo, montoTotalAPagar, diasRestrasado, adapterPosition, needUpdate)
        }

        /*
        if(needUpdate)
        {
            openDialogActualizarPago(prestamo, montoTotalAPagar,adapterPosition)
        }else
        {
            showShortMessage("El cliente no tiene deudas")
        }*/
    }

    override fun openDialogoActualizarPrestamo(prestamo: Prestamo, montoTotalAPagar: String, adapterPosition: Int) {

        binding.cntCortina.isVisible = true

        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialogBackground)
        val view : View   = layoutInflater.inflate(R.layout.dialog_salir_sin_guardar, null)
        val bindingDialogSalir = DialogSalirSinGuardarBinding.bind(view)

        val title       = bindingDialogSalir.textView
        val desc        = bindingDialogSalir.lblDescSalirNoticias
        val btnPositive   = bindingDialogSalir.btnAceptarSalir
        val btnNegative = bindingDialogSalir.btnCancelarSalir

        title.text = "¿Estas seguro de actualizar la deuda?"
        desc.text  = ("Se actualizará la deuda de: <b>${replaceFirstCharInSequenceToUppercase(prestamo.nombres.toString())}, ${replaceFirstCharInSequenceToUppercase(prestamo.apellidos.toString())} </b>" +
                ",con un monto total a pagar de: <br><b>S./${montoTotalAPagar}<b>").fromHtml()

        dialogBuilder.apply {
            setView(view)
        }

        val dialog = dialogBuilder.create()
        dialog.apply {
            setCanceledOnTouchOutside(false)
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window?.setGravity(Gravity.CENTER)
            window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setOnDismissListener {
                binding.cntCortina.visibility = View.GONE
            }
            show()
        }


        btnPositive?.apply {
            visibility = View.VISIBLE
            setOnClickListener {


                //(context as PrincipalActivity).showCortinaPrincipal(true)

                dialog.dismiss()

                _viewModelregister.updateUltimoPago(prestamo.id, getFechaActualNormalCalendar()){
                        isCorrect, msj, result, isRefresh ->

                    if(isCorrect)
                    {
                        prestamo.fechaUltimoPago = getFechaActualNormalCalendar()
                        //(context as PrincipalActivity).showCortinaPrincipal(false)
                        prestamoAdapter.updateItem(adapterPosition, prestamo)//Actualizar local recycler View
                        showMessage(msj)

                    }else
                    {
                        //(context as PrincipalActivity).showCortinaPrincipal(false)
                        showMessage(msj)
                    }

                }


            }
        }

        btnNegative?.apply {
            visibility = View.VISIBLE
            isAllCaps = false
            setOnClickListener {
                dialog.dismiss()
            }
        }
    }


}