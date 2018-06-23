package io.mindhouse.idee.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import io.mindhouse.idee.data.AuthorizeRepository
import io.mindhouse.idee.ui.auth.AuthActivity
import timber.log.Timber
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var authorizeRepository: AuthorizeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        startProperActivity()
        finish()
    }

    private fun startProperActivity() {
        val intent = if (!authorizeRepository.isLoggedIn) {
            AuthActivity.newIntent(this)
        } else {
            val user = authorizeRepository.currentUser
            Timber.d("Logged in as: $user")

            MainActivity.newIntent(this)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
