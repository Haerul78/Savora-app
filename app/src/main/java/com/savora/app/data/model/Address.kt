package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val label: String = "Rumah",
    @SerialName("recipient_name") val recipientName: String = "",
    val phone: String = "",
    @SerialName("full_address") val fullAddress: String = "",
    val city: String = "",
    val province: String = "",
    @SerialName("postal_code") val postalCode: String = "",
    @SerialName("is_primary") val isPrimary: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
)