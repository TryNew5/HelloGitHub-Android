package com.hellogithub.app.data.repository

import com.hellogithub.app.data.remote.ApiService
import com.hellogithub.app.data.remote.dto.RepoDetailResponse

class RepoDetailRepository(private val api: ApiService) {

    suspend fun getDetail(rid: String): Result<RepoDetailResponse> = runCatching {
        api.getRepoDetail(rid)
    }
}
