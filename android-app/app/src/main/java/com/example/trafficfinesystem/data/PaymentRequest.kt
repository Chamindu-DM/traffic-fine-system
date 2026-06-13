package com.example.trafficfinesystem.data

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
    @SerializedName("referenceNumber") val referenceNumber: String,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("cardLastFourDigits") val cardLastFourDigits: String? = null
)
