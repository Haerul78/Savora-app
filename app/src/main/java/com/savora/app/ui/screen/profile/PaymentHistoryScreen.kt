package com.savora.app.ui.screen.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.savora.app.data.model.PaymentWithOrder
import com.savora.app.ui.theme.*
import com.savora.app.ui.utils.formatPaymentDate
import com.savora.app.ui.utils.paymentStatusInfo
import com.savora.app.ui.viewmodel.PaymentHistoryViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    onNavigateBack: () -> Unit = {},
    onPaymentClick: (paymentId: String) -> Unit = {},
    viewModel: PaymentHistoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = SavoraSurface,
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pembayaran", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SavoraSurface),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                viewModel.filters.forEach { f ->
                    FilterChip(
                        selected = uiState.selectedFilter == f,
                        onClick = { viewModel.filterByStatus(f) },
                        label = { Text(f) },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SavoraPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = SavoraSurfaceContainerLow,
                            labelColor = SavoraOnSurfaceVariant,
                        ),
                        border = null,
                    )
                }
            }

            when {
                uiState.isLoading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator(color = SavoraPrimary) }

                uiState.payments.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.ReceiptLong, null,
                            tint = SavoraOnSurfaceVariant,
                            modifier = Modifier.size(64.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Belum ada riwayat",
                            style = MaterialTheme.typography.titleSmall,
                            color = SavoraOnSurfaceVariant,
                        )
                    }
                }

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    uiState.payments.forEach { pw ->
                        PaymentListItem(pw = pw, onClick = { onPaymentClick(pw.payment.id) })
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PaymentListItem(pw: PaymentWithOrder, onClick: () -> Unit) {
    val fmt = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateStr = formatPaymentDate(pw.payment.createdAt, pw.payment.paidAt)
    val statusInfo = paymentStatusInfo(pw.payment.status, compact = true)
    val itemSummary = pw.orderItems.take(2).joinToString(", ") { it.productName }
        .let { if (pw.orderItems.size > 2) "$it, +${pw.orderItems.size - 2} lainnya" else it }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SavoraSurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.ReceiptLong,
                contentDescription = null,
                tint = SavoraPrimary,
                modifier = Modifier.size(36.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    pw.order.orderNumber,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SavoraOnSurface,
                )
                Text(
                    itemSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = SavoraOnSurfaceVariant,
                    maxLines = 1,
                )
                Text(
                    dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = SavoraOnSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    fmt.format(pw.payment.amount),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SavoraPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(50), color = statusInfo.bgColor) {
                    Text(
                        statusInfo.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = statusInfo.textColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
        }
    }
}
