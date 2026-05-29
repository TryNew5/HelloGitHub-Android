package com.hellogithub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    val success: Boolean,
    val page: Int = 1,
    val data: List<HomeItemDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    val message: String? = null,
)
