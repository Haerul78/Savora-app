package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("product_id") val productId: String = "",
    @SerialName("recipe_id") val recipeId: String? = null,
    val quantity: Int = 1,
    @SerialName("added_at") val addedAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)

data class CartItemWithDetails(
    val cartItem: CartItem,
    val product: Product,
    val recipeName: String? = null,
)