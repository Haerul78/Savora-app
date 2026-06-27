package com.savora.app.ui.screen.checkout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.savora.app.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentSuccessScreen(
    orderId: String = "",
    paymentMethod: String = "",
    total: Long = 0L,
    onNavigateHome: () -> Unit = {}
) {
    var startAnimation by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "success_scale"
    )

    // Dibuat sekali saat composable pertama kali masuk layar
    val displayOrderId = remember(orderId) {
        if (orderId.isNotBlank()) "SAV-${orderId.takeLast(8).uppercase()}"
        else "SAV-${System.currentTimeMillis().toString().takeLast(8)}"
    }
    val displayTime = remember {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
    }
    val displayMethod = remember(paymentMethod) {
        when (paymentMethod.lowercase()) {
            "gopay" -> "GoPay"
            "ovo" -> "OVO"
            "dana" -> "Dana"
            "shopeepay" -> "ShopeePay"
            "qris" -> "QRIS"
            "bca" -> "BCA Virtual Account"
            "mandiri" -> "Mandiri Virtual Account"
            "bni" -> "BNI Virtual Account"
            else -> paymentMethod.replaceFirstChar { it.uppercase() }
        }
    }
    val displayTotal = remember(total) {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(total)
    }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SavoraSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success icon
        Surface(
            shape = CircleShape,
            color = SavoraPrimaryContainer,
            modifier = Modifier
                .size(96.dp)
                .scale(scaleAnim)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Berhasil",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Pembayaran Berhasil!",
            style = MaterialTheme.typography.headlineSmall,
            color = SavoraPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Pesanan kamu sedang diproses",
            style = MaterialTheme.typography.bodyMedium,
            color = SavoraOnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Order detail card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SavoraSurfaceContainerLowest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "ID Pesanan: $displayOrderId",
                    style = MaterialTheme.typography.labelMedium,
                    color = SavoraOnSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SavoraSurfaceContainerLow)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Metode Pembayaran", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                    Text(displayMethod, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Waktu Transaksi", style = MaterialTheme.typography.bodySmall, color = SavoraOnSurfaceVariant)
                    Text(displayTime, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(12.dp))

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SavoraSurfaceContainerLow))

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Bayar", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        displayTotal,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SavoraPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hubungi Support
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = SavoraSecondaryContainer),
            border = null
        ) {
            Icon(Icons.Filled.SupportAgent, null, tint = SavoraOnSecondaryContainer, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Hubungi Support", color = SavoraOnSecondaryContainer, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Kembali ke Beranda
        Button(
            onClick = onNavigateHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(SavoraPrimary, SavoraPrimaryContainer)), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Kembali ke Beranda", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}
