package com.caryoutube.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import com.caryoutube.YouTubeVideo

/**
 * Screen showing video info (actual playback would need MediaSession)
 */
class VideoPlayingScreen(
    carContext: CarContext,
    private val video: YouTubeVideo,
    private val channelName: String
) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val pane = Pane.Builder()
            .addRow(
                Row.Builder()
                    .setTitle(video.title)
                    .addText(channelName)
                    .build()
            )
            .addRow(
                Row.Builder()
                    .setTitle("Video ID: ${video.id}")
                    .addText("Use this to play on YouTube app")
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setTitle("Back")
                    .setOnClickListener { screenManager.pop() }
                    .build()
            )
            .build()

        return PaneTemplate.Builder(pane)
            .setTitle("Now Playing")
            .setHeaderAction(Action.BACK)
            .build()
    }
}
