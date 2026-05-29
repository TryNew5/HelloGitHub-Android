package com.hellogithub.app.data.repository

import com.hellogithub.app.data.remote.ApiService
import com.hellogithub.app.data.remote.dto.PeriodicalDetailResponse
import com.hellogithub.app.data.remote.dto.PeriodicalListResponse

class PeriodicalRepository(private val api: ApiService) {

    suspend fun getList(): Result<PeriodicalListResponse> = runCatching {
        api.getPeriodicalList()
    }

    suspend fun getDetail(num: Int): Result<PeriodicalDetailResponse> = runCatching {
        api.getPeriodicalDetail(num)
    }
}
