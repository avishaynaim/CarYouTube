import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Load API credentials from local.properties
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

android {
    namespace = "com.caryoutube"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.caryoutube"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // YouTube API credentials
        buildConfigField("String", "YOUTUBE_API_KEY",
            "\"${localProperties.getProperty("youtube.api.key", "")}\"")
        buildConfigField("String", "GOOGLE_OAUTH_CLIENT_ID",
            "\"${localProperties.getProperty("google.oauth.client.id", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Android Auto Car App Library
    implementation("androidx.car.app:app:1.4.0")
    implementation("androidx.car.app:app-projected:1.4.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Network (YouTube API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}
