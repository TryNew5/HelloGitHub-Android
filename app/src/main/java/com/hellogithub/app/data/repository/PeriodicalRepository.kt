package com.hellogithub.app.data.repository

import com.hellogithub.app.data.remote.ApiService
import com.hellogithub.app.data.remote.PeriodicalWebService
import com.hellogithub.app.data.remote.dto.PeriodicalListResponse
import com.hellogithub.app.data.remote.dto.PeriodicalVolumeResponse
import com.hellogithub.app.util.safeApiCall

class PeriodicalRepository(
    private val api: ApiService,
    private val webService: PeriodicalWebService,
) {

    suspend fun getList(): Result<PeriodicalListResponse> = safeApiCall {
        api.getPeriodicalList()
    }

    suspend fun getVolume(num: Int): Result<PeriodicalVolumeResponse> {
        return webService.fetchVolume(num)  // already returns Result via safeApiCall
    }
}
