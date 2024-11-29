package com.paparazziapps.pretamistapp.presentation.tesoreria.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.databinding.FragmentFinanzasBinding
import com.paparazziapps.pretamistapp.helper.INT_DEFAULT
import com.paparazziapps.pretamistapp.helper.views.beGone
import com.paparazziapps.pretamistapp.helper.views.beVisible
import com.paparazziapps.pretamistapp.domain.Sucursales
import com.paparazziapps.pretamistapp.presentation.login.viewmodels.ViewModelBranches
import com.paparazziapps.pretamistapp.presentation.tesoreria.adapter.LoanDetailAdapter
import com.paparazziapps.pretamistapp.presentation.tesoreria.viewmodels.ViewModelFinance
import com.paparazziapps.pretamistapp.application.MyPreferences
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class FinanzasFragment : Fragment() {

    private val _viewModel by viewModel<ViewModelFinance>()
    private val _viewModelBranches:ViewModelBranches  by viewModel()
    private val preferences: MyPreferences by inject()

    var _binding:FragmentFinanzasBinding?= null
    private val binding get() = _binding!!

    private val loanDetailAdapter = LoanDetailAdapter()


    //Components LAyout
    lateinit var cardViewCajaAdmin:CardView
    lateinit var layoutFechaInicio: TextInputLayout
    lateinit var layoutFechaFin: TextInputLayout
    lateinit var fechaIni: TextInputEditText
    lateinit var fechaFi: TextInputEditText
    lateinit var cajaAdminTotalTxt: MaterialTextView

    var fechaInicioUnixtime:Long?= null
    var fechaFinUnixtime:Long?= null

    //Super Admin Componentes
    var listaSucursales = mutableListOf<Sucursales>()
    lateinit var sucursalTxt: AutoCompleteTextView
    lateinit var sucursalTxtLayout:TextInputLayout
    lateinit var viewProgressSucursal: View
    lateinit var viewCurtainSucursal: View
    lateinit var viewDotsSucursal: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanzasBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            cardViewCajaAdmin   = cardviewCaja
            layoutFechaInicio   = fechaInicioLayout
            layoutFechaFin      = fechaFinLayout
            fechaIni            = fechaInicio
            fechaFi             = fechaFin
            cajaAdminTotalTxt   = cajaResultMoneyAdmin

            sucursalTxt          = edtSucursal
            sucursalTxtLayout    = txtInputLayoutSucursal
            viewProgressSucursal = progressSucursal
            viewDotsSucursal     = dotsSucursal
            viewCurtainSucursal  = curtainSucursal
        }

        //all code here

        superAdminComponentes()
        otherComponents()
        observers()

        binding.recyclerDetalle.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
            adapter = loanDetailAdapter
        }

        initialCode()
    }

    private fun superAdminComponentes() {
        if(preferences.isSuperAdmin){
            binding.contentCajaAll.beGone()
            binding.contentCajaHoy.beGone()
            binding.contentCajaAyer.beGone()
            cardViewCajaAdmin.beVisible()
            sucursalTxtLayout.beVisible()
            viewProgressSucursal.beVisible()
            _viewModelBranches.getBranches()

        }else{
            if(preferences.isAdmin){
                viewProgressSucursal.beGone()
                sucursalTxtLayout.beGone()
            }else{
                //Usuario Normal
                cardViewCajaAdmin.beGone()
            }

        }
    }

    private fun otherComponents() {
        if(preferences.isAdmin || preferences.isSuperAdmin){
            cardViewCajaAdmin?.beVisible()
            validateCajaAdmin()
            layoutFechaInicio.setEndIconOnClickListener {
                getCalendar(true)
            }

            layoutFechaFin.setEndIconOnClickListener {
                getCalendar(false)
            }

            fechaIni.setOnClickListener {
                getCalendar(true)
            }

            fechaFi.setOnClickListener {
                getCalendar(false)
            }
        }
    }

    private fun validateCajaAdmin() {
        fechaIni.doAfterTextChanged {
            showCajaTotal()
        }

        fechaFi.doAfterTextChanged {
            showCajaTotal()
        }

        sucursalTxt.doAfterTextChanged {
            showCajaTotal()
        }
    }

    private fun showCajaTotal() {

        if(!fechaIni.text.toString().trim().isNullOrEmpty()&&
            !fechaIni.text.toString().trim().isNullOrEmpty()){
            //Traer prestamos caja de esa fecha
            var idSucursalSelected:Int = INT_DEFAULT
            listaSucursales.forEach {
                if(it.name?.equals(sucursalTxt.text.toString().trim()) == true) idSucursalSelected = it.id?: INT_DEFAULT
            }
            _viewModel.getLoansByTime(fechaInicioUnixtime?:0, fechaFinUnixtime?:0, idSucursalSelected)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCalendar(isFechaInicio: Boolean) {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleciona una fecha")
                .build()

        datePicker.show(parentFragmentManager, "Datepickerdialog");

        datePicker.addOnPositiveButtonClickListener { unixtime ->
            println("UnixTime selected --isFechaInicio:${isFechaInicio}: ${unixtime}")
            if(isFechaInicio) fechaInicioUnixtime = unixtime else fechaFinUnixtime = unixtime
            SimpleDateFormat("dd/MM/yyyy").apply {
                timeZone = TimeZone.getTimeZone("GMT")
                format(unixtime).toString().also {
                    if(isFechaInicio) fechaIni.setText(it) else fechaFi.setText(it)
                }
            }

        }

    }

    private fun observers() {
        _viewModel.receivePrestamos().observe(viewLifecycleOwner) {
            if(it.count() == 0) {
                binding.apply {
                    imgEmptyDeuda.isVisible = true
                    lblEmptyDeuda.isVisible = true
                }
                //showMessage("No hay prestamos")
            }else {
                binding.apply {
                    imgEmptyDeuda.isVisible = false
                    lblEmptyDeuda.isVisible = false
                    recyclerDetalle.isVisible = true
                }
                loanDetailAdapter.setData(it)
                //showMessage(" Lista de prestamos ${it.count()}")
            }
        }

        //Observers SuperAdmin
        _viewModelBranches.branchesFinances.observe(viewLifecycleOwner){

            println("Sucursales Finanzas: $it")

            if(it.isNotEmpty()) {
                listaSucursales = it.toMutableList()
                var scrsales = mutableListOf<String>()
                it.forEach {
                    scrsales.add(it.name?:"")
                }

                val adapterSucursales= ArrayAdapter(requireContext(),R.layout.select_items, scrsales)
                sucursalTxt.setAdapter(adapterSucursales)
                sucursalTxt.setOnClickListener { sucursalTxt.showDropDown() }
                sucursalTxtLayout.setEndIconOnClickListener { sucursalTxt.showDropDown() }
                viewProgressSucursal.beGone()
                viewDotsSucursal.beGone()
                viewCurtainSucursal.beGone()
            }

        }

        _viewModel.getPagosTotalesByTime().observe(viewLifecycleOwner){
            println("getPagosTotalesByTime: $it")
            cajaAdminTotalTxt.text = it.toString()
        }
    }

    private fun initialCode() {

        _viewModel.getLoansSize { isCorrect, msj, result, isRefresh ->
            if(isCorrect && isAdded) {
                binding.apply {
                    lblPrestamosActivos.text = "$result activos"
                }
            }
        }

        _viewModel.getPaymentsToday { isCorrect, msj, result, isRefresh ->
            if (isCorrect && isAdded) {
                Log.d("FinanzasFragment", "getPaymentsToday: $result")
                binding.apply {
                    lblCajaHoy.text = "${getString(R.string.tipo_moneda)} $result"
                }
            }else{
                Log.d("FinanzasFragment", "getPaymentsToday: $msj")
            }

        }

        _viewModel.getPaymentsYesterday{ isCorrect, msj, result, isRefresh ->
            if (isCorrect && isAdded) {
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
}