package com.caryoutube.auto

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator

class CarYouTubeService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return CarYouTubeSession()
    }
}

class CarYouTubeSession : Session() {

    override fun onCreateScreen(intent: Intent): Screen {
        return MainScreen(carContext)
    }
}
