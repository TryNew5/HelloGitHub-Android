package com.hellogithub.app.data.remote

import com.hellogithub.app.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("v1/")
    suspend fun getHomeItems(
        @Query("sort_by") sortBy: String = "featured",
        @Query("rank_by") rankBy: String? = null,
        @Query("tid") tid: String? = null,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("page") page: Int = 1,
    ): HomeResponse

    @GET("v1/repository/detail/{rid}")
    suspend fun getRepoDetail(
        @Path("rid") rid: String,
    ): RepoDetailResponse

    @GET("v1/search/")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
    ): SearchResponse

    @GET("v1/periodical/")
    suspend fun getPeriodicalList(): PeriodicalListResponse

    @GET("v1/tag/")
    suspend fun getTags(
        @Query("sort_by") sortBy: String = "hot",
    ): TagResponse

    @GET("v1/stats/")
    suspend fun getStats(): StatsResponse

    @GET("v1/repository/recommend/")
    suspend fun getRecommend(): RecommendResponse
}
