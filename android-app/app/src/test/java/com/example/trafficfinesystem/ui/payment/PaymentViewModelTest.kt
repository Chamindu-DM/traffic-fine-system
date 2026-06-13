package com.example.trafficfinesystem.ui.payment

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

class FakePaymentFineApiService : FineApiService {
    var shouldReturnError = false
    var errorResponseCode = 400
    var errorMessage = "Bad Request"
    var paymentResponseData: PaymentResponse? = null

    override suspend fun lookupFine(
        referenceNumber: String,
        categoryCode: String
    ): Response<FineLookupResponse> {
        return Response.error(404, ResponseBody.create(null, "Not Found"))
    }

    override suspend fun makePayment(request: PaymentRequest): Response<PaymentResponse> {
        return if (shouldReturnError) {
            Response.error(errorResponseCode, ResponseBody.create(null, errorMessage))
        } else {
            if (paymentResponseData != null) {
                Response.success(paymentResponseData)
            } else {
                Response.error(400, ResponseBody.create(null, "Bad Request"))
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeApiService = FakePaymentFineApiService()
    private lateinit var viewModel: PaymentViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PaymentViewModel(fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun payFine_withCardAndInvalidDigitsLength_updatesStateToError() {
        viewModel.payFine(
            referenceNumber = "TF-2026-000123",
            categoryCode = "SPD",
            paymentMethod = "CARD",
            cardLastFourDigits = "12" // Invalid length
        )
        val state = viewModel.uiState.value
        assert(state is PaymentUiState.Error)
        assertEquals("Card digits must contain exactly four numeric digits.", (state as PaymentUiState.Error).message)
    }

    @Test
    fun payFine_withCardAndNonNumericDigits_updatesStateToError() {
        viewModel.payFine(
            referenceNumber = "TF-2026-000123",
            categoryCode = "SPD",
            paymentMethod = "CARD",
            cardLastFourDigits = "123a" // Non-numeric
        )
        val state = viewModel.uiState.value
        assert(state is PaymentUiState.Error)
        assertEquals("Card digits must contain exactly four numeric digits.", (state as PaymentUiState.Error).message)
    }

    @Test
    fun payFine_success_updatesStateToSuccess() = runTest {
        val expectedResponse = PaymentResponse(
            paymentReference = "PAY-ABC123",
            referenceNumber = "TF-2026-000123",
            amount = 2500.0,
            status = "SUCCESS",
            paidAt = "2026-06-13T12:00:00",
            message = "Payment successful. SMS sent to officer."
        )
        fakeApiService.paymentResponseData = expectedResponse

        viewModel.payFine(
            referenceNumber = "TF-2026-000123",
            categoryCode = "SPD",
            paymentMethod = "CARD",
            cardLastFourDigits = "1234"
        )

        val state = viewModel.uiState.value
        assert(state is PaymentUiState.Success)
        assertEquals(expectedResponse, (state as PaymentUiState.Success).response)
    }

    @Test
    fun payFine_error_updatesStateToError() = runTest {
        fakeApiService.shouldReturnError = true
        fakeApiService.errorMessage = "Payment Declined"

        viewModel.payFine(
            referenceNumber = "TF-2026-000123",
            categoryCode = "SPD",
            paymentMethod = "CARD",
            cardLastFourDigits = "1234"
        )

        val state = viewModel.uiState.value
        assert(state is PaymentUiState.Error)
    }
}
