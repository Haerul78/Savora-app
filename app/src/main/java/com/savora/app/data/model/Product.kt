package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    @SerialName("category_id") val categoryId: String = "",
    val name: String = "",
    val description: String? = null,
    val price: Double = 0.0,
    val stock: Int = 0,
    val unit: String = "pcs",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_available") val isAvailable: Boolean = true,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)