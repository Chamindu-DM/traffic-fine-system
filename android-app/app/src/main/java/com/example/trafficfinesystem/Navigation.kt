package com.example.trafficfinesystem

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.trafficfinesystem.ui.main.MainScreen
import com.example.trafficfinesystem.ui.payment.PaymentScreen
import com.example.trafficfinesystem.ui.confirmation.ConfirmationScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Main)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Main> {
          MainScreen(
            onItemClick = { navKey -> backStack.add(navKey) },
            modifier = Modifier.safeDrawingPadding().padding(16.dp)
          )
        }
        entry<Payment> { key ->
          PaymentScreen(
            referenceNumber = key.referenceNumber,
            categoryCode = key.categoryCode,
            amount = key.amount,
            onPaymentSuccess = { confirmation ->
              backStack.add(confirmation)
            },
            modifier = Modifier.safeDrawingPadding().padding(16.dp)
          )
        }
        entry<Confirmation> { key ->
          ConfirmationScreen(
            paymentReference = key.paymentReference,
            status = key.status,
            message = key.message,
            amount = key.amount,
            paidAt = key.paidAt,
            onBackToHome = {
              backStack.removeLastOrNull() // Pop Confirmation
              backStack.removeLastOrNull() // Pop Payment
            },
            modifier = Modifier.safeDrawingPadding().padding(16.dp)
          )
        }
      },
  )
}
