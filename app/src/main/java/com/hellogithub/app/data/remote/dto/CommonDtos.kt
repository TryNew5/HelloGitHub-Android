package com.hellogithub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
)

@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val page: Int = 1,
    val data: List<T> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    val message: String? = null,
)

@Serializable
data class TagDto(
    val tid: String,
    val name: String,
    @SerialName("name_en") val nameEn: String? = null,
)
