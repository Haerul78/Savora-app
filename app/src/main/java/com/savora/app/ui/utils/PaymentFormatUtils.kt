package com.savora.app.ui.utils

import androidx.compose.ui.graphics.Color
import com.savora.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class PaymentStatusInfo(val label: String, val textColor: Color, val bgColor: Color)

fun paymentMethodDisplayName(method: String?): String = when (method) {
    "gopay"     -> "GoPay"
    "ovo"       -> "OVO"
    "dana"      -> "Dana"
    "shopeepay" -> "ShopeePay"
    "qris"      -> "QRIS"
    "bca"       -> "BCA Virtual Account"
    "mandiri"   -> "Mandiri Virtual Account"
    "bni"       -> "BNI Virtual Account"
    else        -> method ?: "-"
}

fun paymentStatusInfo(status: String, compact: Boolean = false): PaymentStatusInfo = when (status) {
    "success"   -> PaymentStatusInfo("Berhasil", Color.White, SavoraPrimary)
    "pending"   -> PaymentStatusInfo(
        if (compact) "Pending" else "Menunggu Pembayaran",
        Color(0xFFFF8F00),
        Color(0xFFFFF8E1),
    )
    "failed"    -> PaymentStatusInfo("Gagal", Color.White, SavoraTertiaryContainer)
    "cancelled" -> PaymentStatusInfo("Dibatalkan", SavoraOnSurfaceVariant, SavoraSurfaceContainerHigh)
    else        -> PaymentStatusInfo(status, SavoraOnSurfaceVariant, SavoraSurfaceContainerHigh)
}

fun formatPaymentDate(createdAt: String, paidAt: String? = null): String {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).also {
        it.timeZone = TimeZone.getDefault()
    }
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).also {
        it.timeZone = TimeZone.getTimeZone("UTC")
    }
    return try {
        val raw = paidAt ?: createdAt
        dateFormatter.format(
            inputFormatter.parse(raw.substringBefore("+").substringBefore("Z") + "Z")
                ?: inputFormatter.parse(raw)
        )
    } catch (e: Exception) { createdAt.take(10) }
}
