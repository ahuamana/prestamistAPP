package com.paparazziapps.pretamistapp.presentation.dashboard.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paparazziapps.pretamistapp.domain.InformationReceiptDomain
import com.paparazziapps.pretamistapp.domain.PAConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ViewModelDetailReceipt(
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<DetailReceiptState> = MutableStateFlow(DetailReceiptState.Loading)
    val state = _state.asStateFlow()

    private val informationReceipt = stateHandle.get<InformationReceiptDomain>(PAConstants.INFORMATION_RECEIPT)?: InformationReceiptDomain(
        idReceipt = "",
        codeOperation = 0,
        fullName = "",
        phoneNumber = "",
        names = "",
        lastNames = "",
        quotes = 0,
        quotesPaidNew = 0,
        totalAmountToPay = 0.0,
        amountPerQuote = 0.0,
        typeLoan = 0
    )

    fun getInformationReceipt() = viewModelScope.launch {
        Log.d("ViewModelDetailReceipt", "getInformationReceipt called")
        _state.value = DetailReceiptState.Loading
        try {
            _state.value = DetailReceiptState.Success(informationReceipt)
        } catch (e: Exception) {
            _state.value = DetailReceiptState.Error
        }

    }


    sealed class DetailReceiptState {
        data object Loading : DetailReceiptState()
        data class Success(val informationReceipt: InformationReceiptDomain) : DetailReceiptState()
        data object Error : DetailReceiptState()
    }

}