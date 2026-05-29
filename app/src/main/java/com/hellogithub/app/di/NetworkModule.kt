package com.hellogithub.app.di

import coil.ImageLoader
import coil.request.CachePolicy
import com.hellogithub.app.data.remote.ApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .addHeader("Referer", "https://hellogithub.com/")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        Retrofit.Builder()
            .baseUrl("https://api.hellogithub.com/")
            .client(get())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }

    // Periodical web scraper (fetches HTML, extracts __NEXT_DATA__)
    single { com.hellogithub.app.data.remote.PeriodicalWebService(get()) }

    // Coil ImageLoader with Referer header for img.hellogithub.com
    single {
        val okHttp = get<OkHttpClient>()
        ImageLoader.Builder(androidContext())
            .okHttpClient { okHttp }
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
}
