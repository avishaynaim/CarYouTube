package com.caryoutube

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthManager(private val context: Context) {

    companion object {
        private const val YOUTUBE_SCOPE = "https://www.googleapis.com/auth/youtube.readonly"
    }

    private val signInClient: GoogleSignInClient by lazy {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(BuildConfig.GOOGLE_OAUTH_CLIENT_ID)
            .requestScopes(Scope(YOUTUBE_SCOPE))
            .build()
        GoogleSignIn.getClient(context, options)
    }

    fun getSignInIntent(): Intent = signInClient.signInIntent

    fun handleSignInResult(data: Intent?): GoogleSignInAccount? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            e.printStackTrace()
            null
        }
    }

    fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null && GoogleSignIn.hasPermissions(account, Scope(YOUTUBE_SCOPE))
    }

    fun getCurrentAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        val account = GoogleSignIn.getLastSignedInAccount(context)?.account ?: return@withContext null
        try {
            GoogleAuthUtil.getToken(context, account, "oauth2:$YOUTUBE_SCOPE")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun signOut(onComplete: () -> Unit = {}) {
        signInClient.signOut().addOnCompleteListener { onComplete() }
    }
}
