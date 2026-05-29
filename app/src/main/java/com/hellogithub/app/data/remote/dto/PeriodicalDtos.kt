package com.hellogithub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── List endpoint (REST API) ──

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

// ── Detail (scraped from website __NEXT_DATA__) ──

@Serializable
data class PeriodicalVolumeResponse(
    val success: Boolean,
    val total: Int = 0,
    @SerialName("page_total") val pageTotal: Int = 0,
    @SerialName("current_num") val currentNum: Int = 0,
    @SerialName("publish_at") val publishAt: String = "",
    val data: List<PeriodicalCategoryDto> = emptyList(),
)

@Serializable
data class PeriodicalCategoryDto(
    @SerialName("category_id") val categoryId: Int = 0,
    @SerialName("category_name") val categoryName: String,
    val items: List<PeriodicalItemDto> = emptyList(),
)

@Serializable
data class PeriodicalItemDto(
    val rid: String,
    val name: String,
    @SerialName("full_name") val fullName: String = "",
    val description: String = "",
    @SerialName("description_en") val descriptionEn: String? = null,
    @SerialName("github_url") val githubUrl: String = "",
    val stars: Int = 0,
    val forks: Int = 0,
    val watch: Int = 0,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("vote_total") val voteTotal: Int = 0,
    @SerialName("publish_at") val publishAt: String = "",
)

@Serializable
data class TagResponse(
    val success: Boolean,
    val data: List<TagDto> = emptyList(),
)
