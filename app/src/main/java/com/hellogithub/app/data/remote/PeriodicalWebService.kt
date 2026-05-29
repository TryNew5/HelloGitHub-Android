package com.hellogithub.app.data.remote

import com.hellogithub.app.data.remote.dto.PeriodicalVolumeResponse
import com.hellogithub.app.util.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class PeriodicalWebService(private val okHttpClient: OkHttpClient) {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    suspend fun fetchVolume(num: Int): Result<PeriodicalVolumeResponse> = safeApiCall {
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://hellogithub.com/periodical/volume/$num")
                .header("User-Agent", "HelloGitHub-Android/1.0")
                .build()
            val response = okHttpClient.newCall(request).execute()
            val html = response.body?.string() ?: throw Exception("Empty response")

            // Extract __NEXT_DATA__ JSON from HTML
            val startTag = """<script id="__NEXT_DATA__" type="application/json">"""
            val endTag = "</script>"
            val startIdx = html.indexOf(startTag)
            if (startIdx == -1) throw Exception("No __NEXT_DATA__ found in HTML")
            val jsonStart = startIdx + startTag.length
            val jsonEnd = html.indexOf(endTag, jsonStart)
            if (jsonEnd == -1) throw Exception("Malformed __NEXT_DATA__ block")

            val nextDataJson = html.substring(jsonStart, jsonEnd)
            val nextData = json.decodeFromString<NextDataWrapper>(nextDataJson)
            nextData.props.pageProps.volume
        }
    }
}

@kotlinx.serialization.Serializable
data class NextDataWrapper(
    val props: NextDataProps,
)

@kotlinx.serialization.Serializable
data class NextDataProps(
    val pageProps: NextDataPageProps,
)

@kotlinx.serialization.Serializable
data class NextDataPageProps(
    val volume: PeriodicalVolumeResponse,
)
