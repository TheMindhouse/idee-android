package io.mindhouse.idee.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.mindhouse.idee.R
import io.mindhouse.idee.ui.base.DefaultActivity

class MainActivity : DefaultActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
