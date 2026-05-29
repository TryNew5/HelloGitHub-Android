package com.hellogithub.app.data.repository

import com.hellogithub.app.data.remote.ApiService
import com.hellogithub.app.data.remote.dto.SearchResponse
import com.hellogithub.app.util.safeApiCall

class SearchRepository(private val api: ApiService) {

    suspend fun search(query: String, page: Int = 1): Result<SearchResponse> = safeApiCall {
        api.searchRepos(query = query, page = page)
    }
}
