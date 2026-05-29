package com.hellogithub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetailResponse(
    val success: Boolean,
    val data: RepositoryDto? = null,
    val message: String? = null,
)

@Serializable
data class RepositoryDto(
    val rid: String,
    val url: String = "",
    val name: String = "",
    @SerialName("full_name") val fullName: String = "",
    val title: String = "",
    @SerialName("title_en") val titleEn: String? = null,
    val description: String = "",
    val summary: String = "",
    @SerialName("summary_en") val summaryEn: String? = null,
    val forks: Int = 0,
    val stars: Int = 0,
    @SerialName("stars_str") val starsStr: String = "",
    @SerialName("open_issues") val openIssues: Int = 0,
    val subscribers: Int = 0,
    @SerialName("primary_lang") val primaryLang: String = "",
    @SerialName("lang_color") val langColor: String = "",
    val license: String = "",
    @SerialName("license_lid") val licenseLid: String = "",
    @SerialName("has_chinese") val hasChinese: Boolean = false,
    @SerialName("is_org") val isOrg: Boolean = false,
    @SerialName("is_show") val isShow: Boolean = false,
    @SerialName("is_featured") val isFeatured: Boolean = false,
    @SerialName("repo_created_at") val repoCreatedAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("publish_at") val publishAt: Long = 0,
    val author: String = "",
    val tags: List<TagDto> = emptyList(),
    @SerialName("is_active") val isActive: Boolean = false,
    @SerialName("is_claimed") val isClaimed: Boolean = false,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("author_avatar") val authorAvatar: String = "",
    val homepage: String? = null,
    val document: String? = null,
    val download: String? = null,
    val online: String? = null,
    @SerialName("other_url") val otherUrl: String? = null,
    @SerialName("video_url") val videoUrl: String? = null,
    @SerialName("star_history") val starHistory: StarHistoryDto? = null,
    @SerialName("release_tag") val releaseTag: String? = null,
    val contributors: Int? = null,
    val score: Float = 0f,
    @SerialName("score_str") val scoreStr: String? = null,
    val votes: Int = 0,
    @SerialName("collect_total") val collectTotal: Int = 0,
    @SerialName("comment_total") val commentTotal: Int = 0,
    @SerialName("praise_rate") val praiseRate: Float = 0f,
)

@Serializable
data class StarHistoryDto(
    val increment: Int = 0,
    val max: Int = 0,
    val min: Int = 0,
    val y: List<Int> = emptyList(),
    val x: List<String> = emptyList(),
)
