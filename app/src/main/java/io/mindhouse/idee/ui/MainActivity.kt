package io.mindhouse.idee.ui

import android.os.Bundle
import io.mindhouse.idee.R
import io.mindhouse.idee.ui.base.DefaultActivity

class MainActivity : DefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
