package com.hellogithub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PeriodicalListResponse(
    val success: Boolean,
    @SerialName("repo_total") val repoTotal: Int = 0,
    val categories: List<PeriodicalCategorySummaryDto> = emptyList(),
    val volumes: List<PeriodicalIssueDto> = emptyList(),
)

@Serializable
data class PeriodicalCategorySummaryDto(
    val name: String,
)

@Serializable
data class PeriodicalIssueDto(
    val num: Int,
    val lastmod: String = "",
)

@Serializable
data class PeriodicalDetailResponse(
    val success: Boolean,
    val data: PeriodicalDetailDto? = null,
    val message: String? = null,
)

@Serializable
data class PeriodicalDetailDto(
    val num: Int = 0,
    val name: String = "",
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
