package com.paparazziapps.pretamistapp.modulos.dashboard.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.DialogSalirSinGuardarBinding
import com.paparazziapps.pretamistapp.databinding.FragmentHomeBinding
import com.paparazziapps.pretamistapp.helper.*
import com.paparazziapps.pretamistapp.modulos.dashboard.adapters.PrestamoAdapter
import com.paparazziapps.pretamistapp.modulos.dashboard.interfaces.setOnClickedPrestamo
import com.paparazziapps.pretamistapp.modulos.dashboard.viewmodels.ViewModelDashboard
import com.paparazziapps.pretamistapp.modulos.login.pojo.Sucursales
import com.paparazziapps.pretamistapp.modulos.login.pojo.User
import com.paparazziapps.pretamistapp.modulos.principal.viewmodels.ViewModelPrincipal
import com.paparazziapps.pretamistapp.modulos.principal.views.PrincipalActivity
import com.paparazziapps.pretamistapp.modulos.registro.pojo.Prestamo
import com.paparazziapps.pretamistapp.modulos.registro.pojo.TypePrestamo
import com.paparazziapps.pretamistapp.modulos.registro.viewmodels.ViewModelRegister
import com.paparazziapps.pretamistapp.application.MyPreferences


class HomeFragment : Fragment(),setOnClickedPrestamo {

    private val viewModel by viewModels<ViewModelDashboard>()
    private val tag = HomeFragment::class.java.simpleName
    var _viewModelPrincipal = ViewModelPrincipal.getInstance()

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    var preferences = MyPreferences()

    //constructores
    val prestamoAdapter = PrestamoAdapter(this)

    private lateinit var recyclerPrestamos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        setOnClickedPrestamoHome = this

        //Link items with layout
        recyclerPrestamos = binding.recyclerPrestamos

        //Configuration
        setupRecyclerPrestamos()
        //Observers
        observers()
        getInforUser()
        return view
    }



    private fun setupRecyclerPrestamos() {
        recyclerPrestamos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = prestamoAdapter
        }
    }

    private fun getInforUser() {
        _viewModelPrincipal.searchUserByEmail()
    }



    private fun observers() {
        _viewModelPrincipal.getUser().observe(viewLifecycleOwner, Observer(::updateUser))
        viewModel.receivePrestamos().observe(viewLifecycleOwner, Observer(::updatePrestamos))
    }

    fun updatePrestamos(prestamosAll:MutableList<Prestamo>){
        if(prestamosAll.isEmpty()) {
            binding.emptyPrestamo.isVisible = true
            recyclerPrestamos.isVisible = false
            //showMessage("No hay prestamos")
        }else {
            //Recibes todos los prestamos
            binding.emptyPrestamo.isVisible = false

            if(MyPreferences().isSuperAdmin){
                var sucurs = MyPreferences().sucusales
                if(sucurs.isEmpty()){
                    prestamoAdapter.setData(prestamosAll)
                }else{
                    try {
                        var newPrestamos = mutableListOf<Prestamo>()
                        var localSucursales = fromJson<List<Sucursales>>(sucurs)
                        localSucursales.forEach{ sucurlocal ->
                            var item = Prestamo(
                                type = TypePrestamo.TITLE.value,
                                title = sucurlocal.name
                            )
                            newPrestamos.add(item)

                            var items = prestamosAll.filter {
                                it.sucursalId == sucurlocal.id
                            }
                            newPrestamos.addAll(items)
                        }
                        prestamoAdapter.setData(newPrestamos)

                    }catch (t:Throwable){
                        FirebaseCrashlytics.getInstance().recordException(t)
                    }
                }
            }else{
                prestamoAdapter.setData(prestamosAll)
            }


            recyclerPrestamos.isVisible = true
            //showMessage(" Lista de prestamos ${it.count()}")
        }
    }

    fun updateUser(it: User){
        println("Info usuario: ${it.superAdmin}")
        preferences.isAdmin = it.admin
        preferences.isSuperAdmin = it.superAdmin
        preferences.sucursalId = it.sucursalId?: INT_DEFAULT
        preferences.sucursalName = it.sucursal?:""
        preferences.email_login = it.email?:""
        preferences.isActiveUser = it.activeUser

        if(it.activeUser) {
            viewModel.getPrestamos()
        }
    }

    private fun showMessage(message:String) {
        showMessageAboveMenuInferiorGlobal(message,binding.root)
    }




    companion object {
        var setOnClickedPrestamoHome:setOnClickedPrestamo? = null
    }

    override fun actualizarPagoPrestamo(prestamo: Prestamo, needUpdate:Boolean, montoTotalAPagar:Double, adapterPosition:Int, diasRestrasado:String) {
        context.apply {
            (this as PrincipalActivity).showBottomSheetDetallePrestamoPrincipal(prestamo, montoTotalAPagar, diasRestrasado, adapterPosition, needUpdate)
        }
    }

    override fun openDialogoActualizarPrestamo(prestamo: Prestamo, montoTotalAPagar: Double, adapterPosition: Int, diasRestantesPorPagar:Int, diasPagados:Int, isClosed:Boolean) {

        binding.cntCortina.isVisible = true

        val dialogBuilder = AlertDialog.Builder(context, R.style.CustomDialogBackground)
        val view : View   = layoutInflater.inflate(R.layout.dialog_salir_sin_guardar, null)
        val bindingDialogSalir = DialogSalirSinGuardarBinding.bind(view)

        val title       = bindingDialogSalir.textView
        val desc        = bindingDialogSalir.lblDescSalirNoticias
        val btnPositive   = bindingDialogSalir.btnAceptarSalir
        val btnNegative = bindingDialogSalir.btnCancelarSalir

        if(isClosed) {
            title.text = "¿Estas seguro de cerrar el préstamo?"
            desc.text  = ("Se cerrára el préstamo de: <b>${replaceFirstCharInSequenceToUppercase(prestamo.nombres.toString())}, ${replaceFirstCharInSequenceToUppercase(prestamo.apellidos.toString())}").fromHtml()
        }else {
            title.text = "¿Estas seguro de actualizar la deuda?"
            desc.text  = ("Se actualizará la deuda de: <b>${replaceFirstCharInSequenceToUppercase(prestamo.nombres.toString())}, ${replaceFirstCharInSequenceToUppercase(prestamo.apellidos.toString())} </b>" +
                    ",con un monto total a pagar de: <br><b>${getString(R.string.tipo_moneda)}${montoTotalAPagar}<b>").fromHtml()
        }

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


        btnPositive.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                dialog.dismiss()
                if (isClosed) {
                    viewModel.cerrarPrestamo(prestamo.id){
                            isCorrect, msj, result, isRefresh ->
                        if(isCorrect) {
                            prestamoAdapter.removeItem(adapterPosition)//remover item de  local recycler View
                            showMessage(msj)
                        }else {
                            showMessage(msj)
                        }
                    }
                }else {
                    viewModel.updateUltimoPago(prestamo.id, getFechaActualNormalCalendar(), montoTotalAPagar, diasRestantesPorPagar, diasPagados){
                            isCorrect, msj, result, isRefresh ->

                        if(isCorrect) {
                            prestamo.fechaUltimoPago = getFechaActualNormalCalendar()
                            prestamo.dias_restantes_por_pagar = diasRestantesPorPagar
                            prestamo.diasPagados = diasPagados
                            prestamoAdapter.updateItem(adapterPosition, prestamo)//Actualizar local recycler View
                            showMessage(msj)
                        }else {
                            showMessage(msj)
                        }
                    }
                }
            }
        }

        btnNegative.apply {
            visibility = View.VISIBLE
            isAllCaps = false
            setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}