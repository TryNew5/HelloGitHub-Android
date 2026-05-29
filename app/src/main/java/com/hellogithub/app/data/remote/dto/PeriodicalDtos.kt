package com.hellogithub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PeriodicalListResponse(
    val success: Boolean,
    val data: List<PeriodicalIssueDto> = emptyList(),
    val message: String? = null,
)

@Serializable
data class PeriodicalIssueDto(
    @SerialName("volume_id") val volumeId: String,
    val name: String,
    @SerialName("name_en") val nameEn: String? = null,
    @SerialName("publish_at") val publishAt: String = "",
    val description: String = "",
)

@Serializable
data class PeriodicalDetailResponse(
    val success: Boolean,
    val data: PeriodicalDetailDto? = null,
    val message: String? = null,
)

@Serializable
data class PeriodicalDetailDto(
    @SerialName("volume_id") val volumeId: String,
    val name: String,
    val categories: List<PeriodicalCategoryDto> = emptyList(),
)

@Serializable
data class PeriodicalCategoryDto(
    val name: String,
    val items: List<HomeItemDto> = emptyList(),
)

@Serializable
data class TagResponse(
    val success: Boolean,
    val data: List<TagDto> = emptyList(),
)
