package com.example.trafficfinesystem.ui.lookup

import com.example.trafficfinesystem.data.FineApiService
import com.example.trafficfinesystem.data.FineLookupResponse
import com.example.trafficfinesystem.data.PaymentRequest
import com.example.trafficfinesystem.data.PaymentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FakeFineApiService : FineApiService {
    var shouldReturnError = false
    var errorResponseCode = 404
    var errorMessage = "Not Found"
    var responseData: FineLookupResponse? = null

    override suspend fun lookupFine(
        referenceNumber: String,
        categoryCode: String
    ): Response<FineLookupResponse> {
        return if (shouldReturnError) {
            Response.error(errorResponseCode, ResponseBody.create(null, errorMessage))
        } else {
            if (responseData != null) {
                Response.success(responseData)
            } else {
                Response.error(404, ResponseBody.create(null, "Not Found"))
            }
        }
    }

    override suspend fun makePayment(request: PaymentRequest): Response<PaymentResponse> {
        return Response.error(500, ResponseBody.create(null, "Stub"))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FineLookupViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeApiService = FakeFineApiService()
    private lateinit var viewModel: FineLookupViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FineLookupViewModel(fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun lookupFine_withEmptyInputs_updatesStateToError() {
        viewModel.lookupFine("", "")
        val state = viewModel.uiState.value
        assert(state is FineLookupUiState.Error)
        assertEquals("Please enter both Reference Number and Category Code.", (state as FineLookupUiState.Error).message)
    }

    @Test
    fun lookupFine_success_updatesStateToSuccess() = runTest {
        val expectedResponse = FineLookupResponse(
            referenceNumber = "TF-2026-000123",
            categoryCode = "SPD",
            category = "Speeding",
            amount = 2500.0,
            district = "Colombo",
            officer = "Officer Name",
            status = "UNPAID",
            issuedAt = "2026-06-10T10:00:00"
        )
        fakeApiService.responseData = expectedResponse

        viewModel.lookupFine("TF-2026-000123", "SPD")

        val state = viewModel.uiState.value
        assert(state is FineLookupUiState.Success)
        assertEquals(expectedResponse, (state as FineLookupUiState.Success).fine)
    }

    @Test
    fun lookupFine_error_updatesStateToError() = runTest {
        fakeApiService.shouldReturnError = true
        fakeApiService.errorMessage = "Connection Timeout"

        viewModel.lookupFine("TF-2026-000123", "SPD")

        val state = viewModel.uiState.value
        assert(state is FineLookupUiState.Error)
    }
}
