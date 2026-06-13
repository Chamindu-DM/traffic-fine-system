package com.example.trafficfinesystem.ui.payment

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trafficfinesystem.Confirmation
import com.example.trafficfinesystem.theme.TrafficFineSystemTheme
import com.example.trafficfinesystem.ui.main.formatCurrency
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    referenceNumber: String,
    categoryCode: String,
    amount: Double,
    onPaymentSuccess: (Confirmation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = viewModel { PaymentViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val paymentMethods = listOf("CARD", "MOBILE_BANKING", "QR")
    var expanded by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf(paymentMethods[0]) }
    var cardDigits by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Trigger Side Effects for success/error
    LaunchedEffect(uiState) {
        when (uiState) {
            is PaymentUiState.Success -> {
                val response = (uiState as PaymentUiState.Success).response
                onPaymentSuccess(
                    Confirmation(
                        paymentReference = response.paymentReference,
                        status = response.status,
                        message = response.message,
                        amount = response.amount,
                        paidAt = response.paidAt
                    )
                )
            }
            is PaymentUiState.Error -> {
                val error = uiState as PaymentUiState.Error
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = error.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Fine Payment Process",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Summary Header Card
            PaymentSummaryHeader(referenceNumber = referenceNumber, amount = amount)

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Form
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Select Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = selectedMethod.replace("_", " "),
                            onValueChange = {},
                            label = { Text("Payment Method") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method.replace("_", " ")) },
                                    onClick = {
                                        selectedMethod = method
                                        expanded = false
                                        // Clear card digits when toggling away
                                        if (method != "CARD") cardDigits = ""
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }

                    // Card Details Input (only visible when CARD is selected)
                    AnimatedVisibility(
                        visible = selectedMethod == "CARD",
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        OutlinedTextField(
                            value = cardDigits,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    cardDigits = it
                                }
                            },
                            label = { Text("Card Last 4 Digits") },
                            placeholder = { Text("e.g. 1234") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Button or Loading
                    if (uiState is PaymentUiState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                        }
                    } else {
                        Button(
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                viewModel.payFine(
                                    referenceNumber = referenceNumber,
                                    categoryCode = categoryCode,
                                    paymentMethod = selectedMethod,
                                    cardLastFourDigits = cardDigits
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Pay Now",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentSummaryHeader(referenceNumber: String, amount: Double) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "FINE PAYMENT SUMMARY",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = formatCurrency(amount),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ref: $referenceNumber",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
