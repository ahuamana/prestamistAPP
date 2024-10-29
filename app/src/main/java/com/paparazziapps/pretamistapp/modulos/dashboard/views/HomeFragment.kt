package com.paparazziapps.pretamistapp.modulos.dashboard.views

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import com.paparazziapps.pretamistapp.modulos.registro.pojo.LoanResponse
import com.paparazziapps.pretamistapp.modulos.registro.pojo.TypePrestamo
import com.paparazziapps.pretamistapp.application.MyPreferences
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class HomeFragment : Fragment(),setOnClickedPrestamo {

    private val viewModel by viewModels<ViewModelDashboard>()
    private val tag = HomeFragment::class.java.simpleName
    var _viewModelPrincipal = ViewModelPrincipal.getInstance()

    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!

    private var preferences = MyPreferences()
    private val prestamoAdapter = PrestamoAdapter(this)

    private lateinit var recyclerPrestamos: RecyclerView

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

        lifecycleScope.launch {
            viewModel.loans.flowWithLifecycle(
                lifecycle = lifecycle,
                minActiveState = androidx.lifecycle.Lifecycle.State.STARTED
            ).collectLatest(::updatePrestamos)
        }

    }

    fun updatePrestamos(prestamosAll:MutableList<LoanResponse>){
        if(prestamosAll.isEmpty()) {
            binding.emptyPrestamo.isVisible = true
            recyclerPrestamos.isVisible = false
            //showMessage("No hay prestamos")
        }else {
            //Recibes todos los prestamos
            binding.emptyPrestamo.isVisible = false

            if(MyPreferences().isSuperAdmin){
                val branches = MyPreferences().sucusales
                if(branches.isEmpty()){
                    prestamoAdapter.setData(prestamosAll)
                }else{
                    try {
                        val newLoanResponses = mutableListOf<LoanResponse>()
                        val localSucursales = fromJson<List<Sucursales>>(branches)
                        localSucursales.forEach{ sucurlocal ->
                            val item = LoanResponse(
                                type = TypePrestamo.TITLE.value,
                                title = sucurlocal.name
                            )
                            newLoanResponses.add(item)

                            val items = prestamosAll.filter {
                                it.sucursalId == sucurlocal.id
                            }
                            newLoanResponses.addAll(items)
                        }
                        prestamoAdapter.setData(newLoanResponses)

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
            viewModel.getLoans()
        }
    }

    private fun showMessage(message:String) {
        showMessageAboveMenuInferiorGlobal(message,binding.root)
    }




    companion object {
        var setOnClickedPrestamoHome:setOnClickedPrestamo? = null
    }

    override fun actualizarPagoPrestamo(loanResponse: LoanResponse, needUpdate:Boolean, montoTotalAPagar:Double, adapterPosition:Int, diasRestrasado:String) {
        context.apply {
            (this as PrincipalActivity).showBottomSheetDetallePrestamoPrincipal(loanResponse, montoTotalAPagar, diasRestrasado, adapterPosition, needUpdate)
        }
    }

    override fun openDialogoActualizarPrestamo(loanResponse: LoanResponse, montoTotalAPagar: Double, adapterPosition: Int, diasRestantesPorPagar:Int, diasPagados:Int, isClosed:Boolean) {

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
            desc.text  = ("Se cerrára el préstamo de: <b>${replaceFirstCharInSequenceToUppercase(loanResponse.nombres.toString())}, ${replaceFirstCharInSequenceToUppercase(loanResponse.apellidos.toString())}").fromHtml()
        }else {
            title.text = "¿Estas seguro de actualizar la deuda?"
            desc.text  = ("Se actualizará la deuda de: <b>${replaceFirstCharInSequenceToUppercase(loanResponse.nombres.toString())}, ${replaceFirstCharInSequenceToUppercase(loanResponse.apellidos.toString())} </b>" +
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
                    viewModel.cerrarPrestamo(loanResponse.id){
                            isCorrect, msj, result, isRefresh ->
                        if(isCorrect) {
                            prestamoAdapter.removeItem(adapterPosition)//remover item de  local recycler View
                            showMessage(msj)
                        }else {
                            showMessage(msj)
                        }
                    }
                }else {
                    viewModel.updateUltimoPago(loanResponse.id, getFechaActualNormalCalendar(), montoTotalAPagar, diasRestantesPorPagar, diasPagados){
                            isCorrect, msj, result, isRefresh ->

                        if(isCorrect) {
                            loanResponse.fechaUltimoPago = getFechaActualNormalCalendar()
                            loanResponse.dias_restantes_por_pagar = diasRestantesPorPagar
                            loanResponse.diasPagados = diasPagados
                            prestamoAdapter.updateItem(adapterPosition, loanResponse)//Actualizar local recycler View
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