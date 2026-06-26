package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeStep(
    val id: String = "",
    @SerialName("recipe_id") val recipeId: String = "",
    @SerialName("step_number") val stepNumber: Int = 0,
    val instruction: String = "",
    @SerialName("duration_minutes") val durationMinutes: Int? = null,
)