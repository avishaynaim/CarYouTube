package com.caryoutube

import android.app.Application

class CarYouTubeApp : Application() {

    val youTubeApi: YouTubeApi by lazy { YouTubeApi() }
    val authManager: AuthManager by lazy { AuthManager(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: CarYouTubeApp

        fun get(): CarYouTubeApp = instance
    }
}
