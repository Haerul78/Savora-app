package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val id: String = "",
    @SerialName("order_id") val orderId: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("payment_method") val paymentMethod: String? = null,
    val status: String = "pending",
    val amount: Double = 0.0,
    @SerialName("transaction_id") val transactionId: String? = null,
    @SerialName("midtrans_token") val midtransToken: String? = null,
    @SerialName("midtrans_redirect_url") val midtransRedirectUrl: String? = null,
    @SerialName("failure_reason") val failureReason: String? = null,
    @SerialName("paid_at") val paidAt: String? = null,
    @SerialName("expired_at") val expiredAt: String? = null,
    @SerialName("created_at") val createdAt: String = "",
)

data class PaymentWithOrder(
    val payment: Payment,
    val order: Order,
    val orderItems: List<OrderItem> = emptyList(),
)