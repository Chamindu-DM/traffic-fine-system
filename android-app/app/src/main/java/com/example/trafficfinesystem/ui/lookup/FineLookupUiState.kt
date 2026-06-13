package com.example.trafficfinesystem.ui.lookup

import com.example.trafficfinesystem.data.FineLookupResponse

sealed interface FineLookupUiState {
    data object Idle : FineLookupUiState
    data object Loading : FineLookupUiState
    data class Success(val fine: FineLookupResponse) : FineLookupUiState
    data class Error(val message: String) : FineLookupUiState
}
