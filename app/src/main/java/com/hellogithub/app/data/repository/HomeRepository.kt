package com.hellogithub.app.data.repository

import com.hellogithub.app.data.remote.ApiService
import com.hellogithub.app.data.remote.dto.HomeResponse
import com.hellogithub.app.data.remote.dto.StatsResponse
import com.hellogithub.app.data.remote.dto.TagResponse
import com.hellogithub.app.util.safeApiCall

class HomeRepository(private val api: ApiService) {

    suspend fun getFeed(
        sortBy: String = "featured",
        rankBy: String? = null,
        topicId: String? = null,
        year: Int? = null,
        month: Int? = null,
        page: Int = 1,
    ): Result<HomeResponse> = safeApiCall {
        api.getHomeItems(sortBy = sortBy, rankBy = rankBy, tid = topicId, year = year, month = month, page = page)
    }

    suspend fun getTags(): Result<TagResponse> = safeApiCall {
        api.getTags()
    }

    suspend fun getStats(): Result<StatsResponse> = safeApiCall {
        api.getStats()
    }
}
