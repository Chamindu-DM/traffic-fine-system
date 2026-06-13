package com.example.trafficfinesystem.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficfinesystem.data.FineApiService
import com.example.trafficfinesystem.data.PaymentRequest
import com.example.trafficfinesystem.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val apiService: FineApiService = RetrofitClient.apiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun payFine(
        referenceNumber: String,
        categoryCode: String,
        paymentMethod: String,
        cardLastFourDigits: String?
    ) {
        if (paymentMethod.uppercase() == "CARD") {
            val digits = cardLastFourDigits?.trim() ?: ""
            if (digits.length != 4 || !digits.all { it.isDigit() }) {
                _uiState.value = PaymentUiState.Error("Card digits must contain exactly four numeric digits.")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            try {
                val request = PaymentRequest(
                    referenceNumber = referenceNumber.trim(),
                    categoryCode = categoryCode.trim(),
                    paymentMethod = paymentMethod.uppercase(),
                    cardLastFourDigits = if (paymentMethod.uppercase() == "CARD") cardLastFourDigits?.trim() else null
                )
                val response = apiService.makePayment(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uiState.value = PaymentUiState.Success(body)
                    } else {
                        _uiState.value = PaymentUiState.Error("Failed to receive payment confirmation details.")
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error: ${response.message()}"
                    _uiState.value = PaymentUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.localizedMessage ?: "Failed to process payment.")
            }
        }
    }

    fun resetState() {
        _uiState.value = PaymentUiState.Idle
    }
}
