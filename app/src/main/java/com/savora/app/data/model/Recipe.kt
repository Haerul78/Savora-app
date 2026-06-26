package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String = "",
    val title: String = "",
    val slug: String = "",
    val category: String = "",
    val description: String? = null,
    @SerialName("cook_time_minutes") val cookTimeMinutes: Int = 0,
    val servings: Int = 4,
    val difficulty: String = "Sedang",
    @SerialName("image_url") val imageUrl: String? = null,
    val rating: Double = 0.0,
    @SerialName("total_reviews") val totalReviews: Int = 0,
    @SerialName("source_key") val sourceKey: String? = null,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)