package com.savora.app.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.savora.app.data.model.PaymentWithOrder
import com.savora.app.data.repository.OrderRepository
import com.savora.app.data.repository.PaymentRepository
import com.savora.app.ui.components.DashedDivider
import com.savora.app.ui.theme.*
import com.savora.app.ui.utils.formatPaymentDate
import com.savora.app.ui.utils.paymentMethodDisplayName
import com.savora.app.ui.utils.paymentStatusInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// ── ViewModel ────────────────────────────────────────────────────────────────

data class PaymentDetailUiState(
    val isLoading: Boolean = true,
    val data: PaymentWithOrder? = null,
    val error: String? = null,
)

class PaymentDetailViewModel(private val paymentId: String) : ViewModel() {
    private val paymentRepo = PaymentRepository()
    private val orderRepo = OrderRepository()

    private val _uiState = MutableStateFlow(PaymentDetailUiState())
    val uiState: StateFlow<PaymentDetailUiState> = _uiState

    init { load() }

    private fun load() {
        viewModelScope.launch {
            try {
                val payment = paymentRepo.getPaymentById(paymentId).getOrThrow()
                val order = orderRepo.getOrderById(payment.orderId).getOrThrow()
                val items = orderRepo.getOrderItems(order.id).getOrDefault(emptyList())
                _uiState.value = PaymentDetailUiState(
                    isLoading = false,
                    data = PaymentWithOrder(payment, order, items),
                )
            } catch (e: Exception) {
                _uiState.value = PaymentDetailUiState(isLoading = false, error = e.message)
            }
        }
    }

    class Factory(private val paymentId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PaymentDetailViewModel(paymentId) as T
    }
}

// ── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailScreen(
    paymentId: String,
    onNavigateBack: () -> Unit = {},
) {
    val viewModel: PaymentDetailViewModel = viewModel(factory = PaymentDetailViewModel.Factory(paymentId))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = SavoraSurface,
        topBar = {
            TopAppBar(
                title = { Text("Detail Pembayaran", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SavoraSurface),
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = SavoraPrimary) }

            uiState.error != null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(uiState.error!!, color = SavoraOnSurfaceVariant)
            }

            uiState.data != null -> ReceiptContent(
                pw = uiState.data!!,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun ReceiptContent(pw: PaymentWithOrder, modifier: Modifier = Modifier) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateStr = formatPaymentDate(pw.payment.createdAt, pw.payment.paidAt)
    val statusInfo = paymentStatusInfo(pw.payment.status)
    val methodName = paymentMethodDisplayName(pw.payment.paymentMethod)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Status badge
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = statusInfo.bgColor,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                val icon = when (pw.payment.status) {
                    "success" -> Icons.Filled.CheckCircle
                    "pending" -> Icons.Filled.Schedule
                    else      -> Icons.Filled.Cancel
                }
                Icon(icon, null, tint = statusInfo.textColor, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    statusInfo.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusInfo.textColor,
                )
            }
        }

        // Kwitansi card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SavoraSurfaceContainerLowest,
            shadowElevation = 2.dp,
        ) {
            Column {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Text(
                        "Kwitansi Pembayaran",
                        style = MaterialTheme.typography.labelSmall,
                        color = SavoraOnSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        pw.order.orderNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SavoraOnSurface,
                    )
                    Text(dateStr, style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                }

                DashedDivider()

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        "Pesanan",
                        style = MaterialTheme.typography.labelSmall,
                        color = SavoraOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    pw.orderItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${item.quantity}x ${item.productName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = SavoraOnSurface,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                fmt.format(item.subtotal),
                                style = MaterialTheme.typography.bodySmall,
                                color = SavoraOnSurfaceVariant,
                            )
                        }
                    }
                }

                DashedDivider()

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        "Rincian Biaya",
                        style = MaterialTheme.typography.labelSmall,
                        color = SavoraOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    val subtotal = pw.orderItems.sumOf { it.subtotal }
                    ReceiptRow("Subtotal", fmt.format(subtotal))
                    ReceiptRow("Biaya Pengiriman", fmt.format(pw.order.deliveryFee))
                    if (pw.order.discount > 0) {
                        ReceiptRow("Diskon", "-${fmt.format(pw.order.discount)}", SavoraPrimary)
                    }
                    Spacer(Modifier.height(4.dp))
                    ReceiptRow("Metode Pembayaran", methodName)
                }

                DashedDivider()

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Total Bayar", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        fmt.format(pw.payment.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SavoraPrimary,
                        fontSize = 18.sp,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ReceiptRow(label: String, value: String, valueColor: Color = SavoraOnSurface) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, color = valueColor)
    }
}
