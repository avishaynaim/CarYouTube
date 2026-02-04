package com.caryoutube.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.caryoutube.CarYouTubeApp
import com.caryoutube.YouTubeChannel
import com.caryoutube.YouTubeVideo
import kotlinx.coroutines.*

/**
 * Screen showing videos from a YouTube channel
 */
class ChannelVideosScreen(
    carContext: CarContext,
    private val channel: YouTubeChannel
) : Screen(carContext) {

    private val app = CarYouTubeApp.get()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var videos: List<YouTubeVideo> = emptyList()
    private var isLoading = true
    private var error: String? = null

    init {
        loadVideos()
    }

    private fun loadVideos() {
        scope.launch {
            isLoading = true
            invalidate()

            try {
                videos = app.youTubeApi.getChannelVideos(channel.id)
                error = null
            } catch (e: Exception) {
                error = "Failed to load videos"
            }

            isLoading = false
            invalidate()
        }
    }

    override fun onGetTemplate(): Template {
        // Loading state
        if (isLoading) {
            return MessageTemplate.Builder("Loading videos...")
                .setTitle(channel.name)
                .setHeaderAction(Action.BACK)
                .setLoading(true)
                .build()
        }

        // Error state
        error?.let { errorMessage ->
            return MessageTemplate.Builder(errorMessage)
                .setTitle(channel.name)
                .setHeaderAction(Action.BACK)
                .addAction(
                    Action.Builder()
                        .setTitle("Retry")
                        .setOnClickListener { loadVideos() }
                        .build()
                )
                .build()
        }

        // Empty state
        if (videos.isEmpty()) {
            return MessageTemplate.Builder("No videos found")
                .setTitle(channel.name)
                .setHeaderAction(Action.BACK)
                .build()
        }

        // Video list
        val listBuilder = ItemList.Builder()

        videos.forEach { video ->
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(video.title)
                    .setOnClickListener {
                        screenManager.push(VideoPlayingScreen(carContext, video, channel.name))
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setTitle(channel.name)
            .setHeaderAction(Action.BACK)
            .setSingleList(listBuilder.build())
            .build()
    }

    override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
        super.onDestroy(owner)
        scope.cancel()
    }
}
