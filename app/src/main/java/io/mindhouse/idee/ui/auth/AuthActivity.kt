package io.mindhouse.idee.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.mindhouse.idee.R
import io.mindhouse.idee.ui.base.DefaultActivity

class AuthActivity : DefaultActivity() {

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, AuthActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}
