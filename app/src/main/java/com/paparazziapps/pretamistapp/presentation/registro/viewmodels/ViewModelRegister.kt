package com.paparazziapps.pretamistapp.presentation.registro.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.data.network.PAResult
import com.paparazziapps.pretamistapp.domain.LoanType
import com.paparazziapps.pretamistapp.helper.getDoubleWithOneDecimalsReturnDouble
import com.paparazziapps.pretamistapp.domain.LoanDomain
import com.paparazziapps.pretamistapp.data.repository.PARepository
import com.paparazziapps.pretamistapp.domain.PAConstants
import com.paparazziapps.pretamistapp.domain.clients.ClientDomainSelect
import com.paparazziapps.pretamistapp.helper.fromGson
import com.paparazziapps.pretamistapp.helper.getDoubleWithTwoDecimalsReturnDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.ceil

class ViewModelRegister (
    private val repository: PARepository,
    private val handle: SavedStateHandle
) : ViewModel(){

    private val tag = ViewModelRegister::class.java.simpleName

    //PAConstants.EXTRA_LOAN_JSON
    private val loanDomain =  handle.get<String>(PAConstants.EXTRA_LOAN_JSON)?.fromGson<LoanDomain>()
    private val clientSelected = handle.get<String>(PAConstants.EXTRA_CLIENT_JSON)?.fromGson<ClientDomainSelect>()

    private val _montoDiario = MutableLiveData<Double>()
    val amountDailyToPay: LiveData<Double> = _montoDiario

    private val _amountRounded = MutableLiveData<Double>()
    val amountRounded: LiveData<Double> = _amountRounded

    private val _dailyStringMode : MutableStateFlow<String> = MutableStateFlow(LoanType.DAILY.description)
    val dailyStringMode : StateFlow<String> = _dailyStringMode.asStateFlow()

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    private val _stateUserInfo: MutableStateFlow<ClientDomainSelect?> = MutableStateFlow(null)
    val stateUserInfo = _stateUserInfo.stateIn(
        scope =viewModelScope,
        started= SharingStarted.WhileSubscribed(5000,1),
        initialValue = null
    )



    init {
        Log.d(tag, "Loan: ${loanDomain}")
        Log.d(tag, "Client: ${clientSelected}")

        if (clientSelected != null) {
            _stateUserInfo.value = clientSelected
        }
    }

    fun getLoanInformationDomain(): LoanDomain? {
        return loanDomain
    }

    fun setDailyStringMode(value: String) {
        //split between spaces and get the first element handle the case of the string having a space at the end
        val description = value.trim().split(" ").firstOrNull()?:LoanType.DAILY.description
        _dailyStringMode.value = description
    }

    fun getClientSelected(): ClientDomainSelect? {
        return clientSelected
    }

    init {
        Log.d(tag, "Loan: ${loanDomain}")
        Log.d(tag, "Client: ${clientSelected}")
    }

    fun calcAmountToPay(capital:Int, interes:Int, quotes:Int) {
        try {
            val newCapital = capital.toDouble()
            val newInterest = interes.toDouble()

            val finalInterest = newCapital * (newInterest/100)
            val amountToPayDaily = (finalInterest + newCapital)/quotes
            val amountDailyFinal = getDoubleWithOneDecimalsReturnDouble(amountToPayDaily)?:-9999.00
            _montoDiario.value = amountDailyFinal
            _amountRounded.value = getDoubleWithTwoDecimalsReturnDouble(amountToPayDaily)

        }catch (e:Exception) {
            Log.d(tag,"ERROR: ${e.message}")
        }
    }


    fun createLoan(loanDomain: LoanDomain, branchId:Int)  = viewModelScope.launch {
        _state.value = RegisterState.Loading
        val result = repository.createLoan(loanDomain, idBranch = branchId)

        when (result) {
            is PAResult.Error -> {
                val msg = "La solicitud no se pudo procesar, intentalo otra vez"
                _state.value = RegisterState.Error(msg)
            }

            is PAResult.Success -> {
                val msg = "El prestamo se registro correctamente"
                _state.value = RegisterState.Success(msg)
            }

            else -> {
                Log.d(tag, "Error desconocido")
            }
        }
    }

    fun resetState() {
        _state.value = RegisterState.Idle
    }

    fun getDailyAmount(isCheckedRounded:Boolean):Double {

        if(isCheckedRounded.not()){
            return getDoubleWithOneDecimalsReturnDouble(_montoDiario.value ?: -9999.00) ?: 0.0
        }

        val dailyAmount = if (isCheckedRounded) {
            getDoubleWithTwoDecimalsReturnDouble(_montoDiario.value ?: -9999.00)
        } else {
            getDoubleWithOneDecimalsReturnDouble(_montoDiario.value ?: -9999.00)
        }
        val rounded = ceil(dailyAmount ?: -9999.00)

        return if (rounded == -9999.00) {
            0.0
        } else {
            rounded
        }
    }



}

sealed class RegisterState {
    data object Idle : RegisterState()
    data object Loading : RegisterState()
    data class Success(val message:String) : RegisterState()
    data class Error(val message:String) : RegisterState()
}