package com.example.trafficfinesystem.ui.payment

import com.example.trafficfinesystem.data.PaymentResponse

sealed interface PaymentUiState {
    data object Idle : PaymentUiState
    data object Loading : PaymentUiState
    data class Success(val response: PaymentResponse) : PaymentUiState
    data class Error(val message: String) : PaymentUiState
}
