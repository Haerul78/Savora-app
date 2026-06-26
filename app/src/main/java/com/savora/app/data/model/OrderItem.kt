package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: String = "",
    @SerialName("order_id") val orderId: String = "",
    @SerialName("product_id") val productId: String = "",
    @SerialName("recipe_id") val recipeId: String? = null,
    @SerialName("product_name") val productName: String = "",
    @SerialName("product_image") val productImage: String? = null,
    @SerialName("price_at_purchase") val priceAtPurchase: Double = 0.0,
    val quantity: Int = 1,
    val subtotal: Double = 0.0,
)