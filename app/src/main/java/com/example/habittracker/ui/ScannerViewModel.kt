package com.example.habittracker.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habittracker.api.OffRetrofitClient
import com.example.habittracker.data.OffProduct
import kotlinx.coroutines.launch

class ScannerViewModel : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val product: OffProduct? = null,
        val error: String? = null
    )

    private val _state = MutableLiveData(UiState())
    val state: LiveData<UiState> = _state

    fun fetchProduct(barcode: String) {
        _state.value = UiState(loading = true)
        viewModelScope.launch {
            try {
                val response = OffRetrofitClient.service.getProduct(barcode)
                if (response.status == 1 && response.product != null) {
                    _state.postValue(UiState(product = response.product))
                } else {
                    _state.postValue(UiState(error = response.statusVerbose ?: "Produkt nie znaleziony"))
                }
            } catch (e: Exception) {
                _state.postValue(UiState(error = "Blad sieci: ${e.message}"))
            }
        }
    }
}
