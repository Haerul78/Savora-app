package com.savora.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductCategory(
    val id: String = "",
    val name: String = "",
    val slug: String = "",
    @SerialName("icon_url") val iconUrl: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0,
)