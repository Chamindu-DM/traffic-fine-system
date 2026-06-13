package com.example.trafficfinesystem.ui.lookup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficfinesystem.data.FineApiService
import com.example.trafficfinesystem.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FineLookupViewModel(
    private val apiService: FineApiService = RetrofitClient.apiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<FineLookupUiState>(FineLookupUiState.Idle)
    val uiState: StateFlow<FineLookupUiState> = _uiState.asStateFlow()

    fun lookupFine(referenceNumber: String, categoryCode: String) {
        if (referenceNumber.isBlank() || categoryCode.isBlank()) {
            _uiState.value = FineLookupUiState.Error("Please enter both Reference Number and Category Code.")
            return
        }

        viewModelScope.launch {
            _uiState.value = FineLookupUiState.Loading
            try {
                val response = apiService.lookupFine(referenceNumber.trim(), categoryCode.trim())
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uiState.value = FineLookupUiState.Success(body)
                    } else {
                        _uiState.value = FineLookupUiState.Error("No details found for the given reference and category.")
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error code: ${response.code()}"
                    _uiState.value = FineLookupUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = FineLookupUiState.Error(e.localizedMessage ?: "Failed to connect to server.")
            }
        }
    }

    fun resetState() {
        _uiState.value = FineLookupUiState.Idle
    }
}
