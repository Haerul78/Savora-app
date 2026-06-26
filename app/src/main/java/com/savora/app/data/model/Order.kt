package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("address_id") val addressId: String = "",
    @SerialName("order_number") val orderNumber: String = "",
    val status: String = "pending",
    val subtotal: Double = 0.0,
    @SerialName("delivery_fee") val deliveryFee: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val notes: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)