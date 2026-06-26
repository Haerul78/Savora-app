package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    @SerialName("full_name") val fullName: String = "",
    val email: String = "",
    val phone: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val role: String = "user",
    val address: String? = null,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
)