package com.example.trafficfinesystem.data

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    @SerializedName("paymentReference") val paymentReference: String,
    @SerializedName("referenceNumber") val referenceNumber: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("paidAt") val paidAt: String,
    @SerializedName("message") val message: String
)
