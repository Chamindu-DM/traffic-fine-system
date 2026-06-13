package com.example.trafficfinesystem.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FineApiService {
    @GET("fines/lookup")
    suspend fun lookupFine(
        @Query("referenceNumber") referenceNumber: String,
        @Query("categoryCode") categoryCode: String
    ): Response<FineLookupResponse>

    @POST("payments")
    suspend fun makePayment(
        @Body request: PaymentRequest
    ): Response<PaymentResponse>
}
