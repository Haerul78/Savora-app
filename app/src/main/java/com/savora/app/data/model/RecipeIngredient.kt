package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredient(
    val id: String = "",
    @SerialName("recipe_id") val recipeId: String = "",
    @SerialName("product_id") val productId: String? = null,
    @SerialName("raw_text") val rawText: String? = null,
    val name: String = "",
    val quantity: Double? = null,
    val unit: String? = null,
    @SerialName("is_optional") val isOptional: Boolean = false,
    @SerialName("sort_order") val sortOrder: Int = 0,
)