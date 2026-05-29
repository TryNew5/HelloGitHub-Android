package com.hellogithub.app.data.remote

import com.hellogithub.app.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/")
    suspend fun getHomeItems(
        @Query("sort_by") sortBy: String = "featured",
        @Query("rank_by") rankBy: String? = null,
        @Query("tid") tid: String? = null,
    ): HomeResponse

    @GET("/repository/detail/{rid}")
    suspend fun getRepoDetail(
        @Path("rid") rid: String,
    ): RepoDetailResponse

    @GET("/search/repository/")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
    ): SearchResponse

    @GET("/periodical/")
    suspend fun getPeriodicalList(): PeriodicalListResponse

    @GET("/periodical/{volumeId}")
    suspend fun getPeriodicalDetail(
        @Path("volumeId") volumeId: String,
    ): PeriodicalDetailResponse

    @GET("/tag/")
    suspend fun getTags(
        @Query("sort_by") sortBy: String = "hot",
    ): TagResponse

    @GET("/stats/")
    suspend fun getStats(): StatsResponse

    @GET("/repository/recommend/")
    suspend fun getRecommend(): RecommendResponse
}
