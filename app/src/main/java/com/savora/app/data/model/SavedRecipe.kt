package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SavedRecipe(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("recipe_id") val recipeId: String = "",
    @SerialName("saved_at") val savedAt: String = "",
)