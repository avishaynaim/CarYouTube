package com.caryoutube

import com.caryoutube.data.ChannelListResponse
import com.caryoutube.data.PlaylistItemListResponse
import com.caryoutube.data.SubscriptionListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("subscriptions")
    suspend fun getSubscriptions(
        @Header("Authorization") auth: String,
        @Query("part") part: String = "snippet",
        @Query("mine") mine: Boolean = true,
        @Query("maxResults") maxResults: Int = 50
    ): SubscriptionListResponse

    @GET("channels")
    suspend fun getChannels(
        @Query("key") apiKey: String,
        @Query("id") id: String,
        @Query("part") part: String = "snippet,statistics"
    ): ChannelListResponse

    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("key") apiKey: String,
        @Query("playlistId") playlistId: String,
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int = 25
    ): PlaylistItemListResponse

    @GET("channels")
    suspend fun getChannelUploads(
        @Query("key") apiKey: String,
        @Query("id") id: String,
        @Query("part") part: String = "contentDetails"
    ): ChannelListResponse
}

class YouTubeApi {
    private val service: YouTubeApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(YouTubeApiService::class.java)
    }

    suspend fun getSubscriptions(accessToken: String): List<YouTubeChannel> {
        return try {
            val response = service.getSubscriptions("Bearer $accessToken")
            val channelIds = response.items.mapNotNull { it.snippet.resourceId.channelId }

            if (channelIds.isEmpty()) return emptyList()

            // Get channel details
            val channelsResponse = service.getChannels(
                apiKey = BuildConfig.YOUTUBE_API_KEY,
                id = channelIds.joinToString(",")
            )

            channelsResponse.items.map { channel ->
                YouTubeChannel(
                    id = channel.id,
                    name = channel.snippet?.title ?: "Unknown",
                    thumbnailUrl = channel.snippet?.thumbnails?.high?.url
                        ?: channel.snippet?.thumbnails?.medium?.url,
                    subscriberCount = channel.statistics?.subscriberCount?.toLongOrNull()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getChannelVideos(channelId: String): List<YouTubeVideo> {
        return try {
            // Get uploads playlist ID
            val channelResponse = service.getChannelUploads(
                apiKey = BuildConfig.YOUTUBE_API_KEY,
                id = channelId
            )

            val uploadsPlaylistId = channelResponse.items.firstOrNull()
                ?.contentDetails?.relatedPlaylists?.uploads
                ?: return emptyList()

            // Get videos from uploads playlist
            val playlistResponse = service.getPlaylistItems(
                apiKey = BuildConfig.YOUTUBE_API_KEY,
                playlistId = uploadsPlaylistId
            )

            playlistResponse.items.map { item ->
                YouTubeVideo(
                    id = item.snippet.resourceId.videoId,
                    title = item.snippet.title,
                    thumbnailUrl = item.snippet.thumbnails?.high?.url
                        ?: item.snippet.thumbnails?.medium?.url
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

data class YouTubeChannel(
    val id: String,
    val name: String,
    val thumbnailUrl: String?,
    val subscriberCount: Long?
)

data class YouTubeVideo(
    val id: String,
    val title: String,
    val thumbnailUrl: String?
)
