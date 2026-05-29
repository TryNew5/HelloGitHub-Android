package com.hellogithub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeResponse(
    val success: Boolean,
    val page: Int = 1,
    val data: List<HomeItemDto> = emptyList(),
    val tags: List<TagDto> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean = false,
    val message: String? = null,
)

@Serializable
data class HomeItemDto(
    @SerialName("item_id") val itemId: String,
    val author: String,
    @SerialName("author_avatar") val authorAvatar: String = "",
    val title: String,
    @SerialName("title_en") val titleEn: String? = null,
    val name: String = "",
    val description: String = "",
    val summary: String = "",
    @SerialName("summary_en") val summaryEn: String? = null,
    @SerialName("primary_lang") val primaryLang: String = "",
    @SerialName("lang_color") val langColor: String = "",
    @SerialName("is_hot") val isHot: Boolean = false,
    @SerialName("is_claimed") val isClaimed: Boolean = false,
    @SerialName("clicks_total") val clicksTotal: Int = 0,
    @SerialName("comment_total") val commentTotal: Int = 0,
    @SerialName("updated_at") val updatedAt: String = "",
)

@Serializable
data class RecommendResponse(
    val success: Boolean,
    val total: Int = 0,
    val data: List<RecommendItemDto> = emptyList(),
)

@Serializable
data class RecommendItemDto(
    val rid: String,
    val name: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("author_avatar") val authorAvatar: String = "",
    val description: String = "",
    @SerialName("primary_lang") val primaryLang: String = "",
    @SerialName("lang_color") val langColor: String = "",
    val stars: Int = 0,
    @SerialName("stars_str") val starsStr: String = "",
)

@Serializable
data class StatsResponse(
    val success: Boolean,
    @SerialName("repo_total") val repoTotal: Int = 0,
    @SerialName("user_total") val userTotal: Int = 0,
)
