package com.example.trafficfinesystem.data

import com.google.gson.annotations.SerializedName

data class FineLookupResponse(
    @SerializedName("referenceNumber") val referenceNumber: String,
    @SerializedName("categoryCode") val categoryCode: String,
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("district") val district: String,
    @SerializedName("officer") val officer: String,
    @SerializedName("status") val status: String, // "PAID", "UNPAID"
    @SerializedName("issuedAt") val issuedAt: String,
    @SerializedName("paidAt") val paidAt: String? = null
)
