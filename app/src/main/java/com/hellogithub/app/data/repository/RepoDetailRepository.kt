package com.hellogithub.app.data.repository

import com.hellogithub.app.data.remote.ApiService
import com.hellogithub.app.data.remote.dto.RepoDetailResponse
import com.hellogithub.app.util.safeApiCall

class RepoDetailRepository(private val api: ApiService) {

    suspend fun getDetail(rid: String): Result<RepoDetailResponse> = safeApiCall {
        api.getRepoDetail(rid)
    }
}
