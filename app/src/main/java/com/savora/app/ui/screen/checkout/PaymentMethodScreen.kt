package com.savora.app.ui.screen.checkout

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.savora.app.ui.theme.*
import com.savora.app.ui.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    onNavigateBack: () -> Unit = {},
    onPaymentSuccess: (orderId: String, paymentMethod: String, total: Long) -> Unit = { _, _, _ -> },
    viewModel: CheckoutViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Buka browser Midtrans saat URL siap
    LaunchedEffect(uiState.midtransUrl) {
        uiState.midtransUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            viewModel.clearMidtransUrl()
        }
    }

    // Saat user kembali dari browser → cek status pembayaran ke Midtrans
    DisposableEffect(uiState.waitingForPayment) {
        if (!uiState.waitingForPayment) return@DisposableEffect onDispose {}
        // Android dispatch ON_RESUME ke observer baru sebagai catch-up event,
        // sehingga kita skip yang pertama dan hanya bereaksi saat user benar-benar kembali.
        var skippedInitialResume = false
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (!skippedInitialResume) {
                    skippedInitialResume = true
                    return@LifecycleEventObserver
                }
                viewModel.promptPaymentConfirmation(onPaymentSuccess)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(uiState.error) {
        if (!uiState.error.isNullOrBlank()) {
            snackbarHostState.showSnackbar(uiState.error!!)
        }
    }

    Scaffold(
        containerColor = SavoraSurface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Pilih Pembayaran", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Kembali") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SavoraSurface)
            )
        },
        bottomBar = {
            Surface(color = SavoraSurfaceContainerLowest, shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                        Text(formatter.format(uiState.subtotal), style = MaterialTheme.typography.bodySmall)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Pengiriman", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                        Text(formatter.format(uiState.deliveryFee), style = MaterialTheme.typography.bodySmall)
                    }
                    if (uiState.discount > 0) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Diskon", style = MaterialTheme.typography.bodySmall, color = SavoraPrimary)
                            Text("-${formatter.format(uiState.discount)}", style = MaterialTheme.typography.bodySmall, color = SavoraPrimary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = SavoraOutlineVariant.copy(0.15f))
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Pembayaran", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(formatter.format(uiState.total), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = SavoraPrimary)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.processPayment(onPaymentSuccess) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = uiState.selectedPaymentMethod != null && !uiState.isLoading
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                if (uiState.selectedPaymentMethod != null) Brush.linearGradient(listOf(SavoraPrimary, SavoraPrimaryContainer))
                                else Brush.linearGradient(listOf(SavoraOnSurfaceVariant.copy(0.3f), SavoraOnSurfaceVariant.copy(0.3f))),
                                RoundedCornerShape(24.dp)
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            else Text("Bayar Sekarang", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // E-Wallet Section
            PaymentSection(title = "E-Wallet") {
                listOf("GoPay" to "gopay", "OVO" to "ovo", "Dana" to "dana", "ShopeePay" to "shopeepay").forEach { (name, key) ->
                    PaymentOptionRow(
                        name = name,
                        isSelected = uiState.selectedPaymentMethod == key,
                        onClick = { viewModel.selectPaymentMethod(key) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // QRIS
            PaymentSection(title = "QRIS") {
                PaymentOptionRow(
                    name = "Bayar dengan QRIS",
                    icon = Icons.Filled.QrCode2,
                    isSelected = uiState.selectedPaymentMethod == "qris",
                    onClick = { viewModel.selectPaymentMethod("qris") },
                    showChevron = true
                )
            }

            Spacer(Modifier.height(8.dp))

            // Transfer Bank
            PaymentSection(title = "Transfer Bank") {
                listOf("BCA Virtual Account" to "bca", "Mandiri Virtual Account" to "mandiri", "BNI Virtual Account" to "bni").forEach { (name, key) ->
                    PaymentOptionRow(
                        name = name,
                        isSelected = uiState.selectedPaymentMethod == key,
                        onClick = { viewModel.selectPaymentMethod(key) }
                    )
                }
            }

        }
    }
}

@Composable
private fun PaymentSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = SavoraSurfaceContainerLow, modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
            content()
        }
    }
}

@Composable
private fun PaymentOptionRow(
    name: String,
    icon: ImageVector = Icons.Filled.AccountBalanceWallet,
    isSelected: Boolean,
    onClick: () -> Unit,
    showChevron: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(SavoraSurfaceContainerLowest)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = SavoraOnSurfaceVariant, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Text(name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        if (showChevron) {
            Icon(Icons.Filled.ChevronRight, null, tint = SavoraOnSurfaceVariant)
        } else {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = SavoraPrimary)
            )
        }
    }
}
