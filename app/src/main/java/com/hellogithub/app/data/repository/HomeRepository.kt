package com.hellogithub.app.data.repository

import com.hellogithub.app.data.remote.ApiService
import com.hellogithub.app.data.remote.dto.HomeResponse
import com.hellogithub.app.data.remote.dto.StatsResponse
import com.hellogithub.app.data.remote.dto.TagResponse

class HomeRepository(private val api: ApiService) {

    suspend fun getFeed(
        sortBy: String = "featured",
        rankBy: String? = null,
        topicId: String? = null,
    ): Result<HomeResponse> = runCatching {
        api.getHomeItems(sortBy = sortBy, rankBy = rankBy, tid = topicId)
    }

    suspend fun getTags(): Result<TagResponse> = runCatching {
        api.getTags()
    }

    suspend fun getStats(): Result<StatsResponse> = runCatching {
        api.getStats()
    }
}
