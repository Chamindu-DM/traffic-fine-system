package com.example.trafficfinesystem

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey

@Serializable
data class Payment(
    val referenceNumber: String,
    val categoryCode: String,
    val amount: Double
) : NavKey

@Serializable
data class Confirmation(
    val paymentReference: String,
    val status: String,
    val message: String,
    val amount: Double,
    val paidAt: String
) : NavKey
