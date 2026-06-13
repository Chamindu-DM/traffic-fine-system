package com.example.trafficfinesystem.ui.main

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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.trafficfinesystem.Payment
import com.example.trafficfinesystem.data.FineLookupResponse
import com.example.trafficfinesystem.ui.lookup.FineLookupUiState
import com.example.trafficfinesystem.ui.lookup.FineLookupViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FineLookupViewModel = viewModel { FineLookupViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var referenceNumber by rememberSaveable { mutableStateOf("") }
    var categoryCode by rememberSaveable { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Trigger Snackbar on error state
    LaunchedEffect(uiState) {
        if (uiState is FineLookupUiState.Error) {
            val error = uiState as FineLookupUiState.Error
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = error.message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Traffic Fine Payment",
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
            // Header Banner
            BrandingHeader()

            Spacer(modifier = Modifier.height(24.dp))

            // Form card
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
                        text = "Verify & Look Up Fine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = referenceNumber,
                        onValueChange = { referenceNumber = it },
                        label = { Text("Fine Reference Number") },
                        placeholder = { Text("e.g. TF-2026-000123") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = categoryCode,
                        onValueChange = { categoryCode = it },
                        label = { Text("Category Code") },
                        placeholder = { Text("e.g. SPD") },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                viewModel.lookupFine(referenceNumber, categoryCode)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            viewModel.lookupFine(referenceNumber, categoryCode)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lookup Fine", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // State management for loading / success / empty
            AnimatedContent(
                targetState = uiState,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "stateAnimation"
            ) { state ->
                when (state) {
                    is FineLookupUiState.Idle -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Enter details above to lookup fine records.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    is FineLookupUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Retrieving records...", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    is FineLookupUiState.Success -> {
                        FineDetailsCard(
                            fine = state.fine,
                            onProceedToPay = {
                                onItemClick(
                                    Payment(
                                        referenceNumber = state.fine.referenceNumber,
                                        categoryCode = state.fine.categoryCode,
                                        amount = state.fine.amount
                                    )
                                )
                            }
                        )
                    }
                    is FineLookupUiState.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
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
fun BrandingHeader() {
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
            .padding(vertical = 24.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "SRI LANKA TRAFFIC POLICE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Online Fine Verification & Payment",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun FineDetailsCard(
    fine: FineLookupResponse,
    onProceedToPay: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FINE DETAILS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val isPaid = fine.status.uppercase(Locale.ROOT) == "PAID"
                val statusText = if (isPaid) "PAID" else "UNPAID"
                val statusColor = if (isPaid) Color(0xFF2E7D32) else Color(0xFFE65100)
                val statusBg = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            DetailRow(label = "Reference Number", value = fine.referenceNumber)
            DetailRow(label = "Violation Category", value = "${fine.category} (${fine.categoryCode})")
            DetailRow(label = "Location (District)", value = fine.district)
            DetailRow(label = "Issuing Officer", value = fine.officer)
            DetailRow(label = "Issued Date", value = formatDate(fine.issuedAt))

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Fine Amount",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatCurrency(fine.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (fine.status.uppercase(Locale.ROOT) == "UNPAID") {
                Button(
                    onClick = onProceedToPay,
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
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Proceed to Pay",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Already Paid",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
    }
}

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "LK"))
    val formatted = formatter.format(amount)
    return if (formatted.startsWith("LKR") || formatted.contains("Rs")) {
        formatted
    } else {
        "LKR ${String.format("%,.2f", amount)}"
    }
}

fun formatDate(isoDate: String): String {
    return try {
        val ldt = LocalDateTime.parse(isoDate)
        ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"))
    } catch (e: Exception) {
        isoDate
    }
}
