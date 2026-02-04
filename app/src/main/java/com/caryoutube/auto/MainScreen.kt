package com.caryoutube.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.caryoutube.CarYouTubeApp
import com.caryoutube.YouTubeChannel
import kotlinx.coroutines.*

/**
 * Main screen showing YouTube channels (subscriptions)
 */
class MainScreen(carContext: CarContext) : Screen(carContext) {

    private val app = CarYouTubeApp.get()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var channels: List<YouTubeChannel> = emptyList()
    private var isLoading = true
    private var error: String? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                scope.cancel()
            }
        })
        loadChannels()
    }

    private fun loadChannels() {
        scope.launch {
            isLoading = true
            invalidate()

            try {
                if (!app.authManager.isSignedIn()) {
                    error = "Please sign in on your phone first"
                    isLoading = false
                    invalidate()
                    return@launch
                }

                val token = app.authManager.getAccessToken()
                if (token == null) {
                    error = "Could not get access token"
                    isLoading = false
                    invalidate()
                    return@launch
                }

                channels = app.youTubeApi.getSubscriptions(token)
                error = null
            } catch (e: Exception) {
                error = "Failed to load channels"
            }

            isLoading = false
            invalidate()
        }
    }

    override fun onGetTemplate(): Template {
        // Loading state
        if (isLoading) {
            return MessageTemplate.Builder("Loading YouTube channels...")
                .setTitle("YouTube")
                .setLoading(true)
                .build()
        }

        // Error state
        error?.let { errorMessage ->
            return MessageTemplate.Builder(errorMessage)
                .setTitle("YouTube")
                .addAction(
                    Action.Builder()
                        .setTitle("Retry")
                        .setOnClickListener { loadChannels() }
                        .build()
                )
                .build()
        }

        // Empty state
        if (channels.isEmpty()) {
            return MessageTemplate.Builder("No channels found.\nSign in to see your subscriptions.")
                .setTitle("YouTube")
                .addAction(
                    Action.Builder()
                        .setTitle("Refresh")
                        .setOnClickListener { loadChannels() }
                        .build()
                )
                .build()
        }

        // Channel list
        val listBuilder = ItemList.Builder()

        channels.forEach { channel ->
            val subscriberText = channel.subscriberCount?.let { count ->
                when {
                    count >= 1_000_000 -> String.format("%.1fM subscribers", count / 1_000_000.0)
                    count >= 1_000 -> String.format("%.1fK subscribers", count / 1_000.0)
                    else -> "$count subscribers"
                }
            } ?: ""

            listBuilder.addItem(
                Row.Builder()
                    .setTitle(channel.name)
                    .addText(subscriberText)
                    .setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(ChannelVideosScreen(carContext, channel))
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setTitle("YouTube")
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(listBuilder.build())
            .build()
    }

}
