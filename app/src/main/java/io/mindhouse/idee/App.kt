package io.mindhouse.idee

import android.app.Activity
import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.mindhouse.idee.di.component.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
open class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        initDI()
        initTimber()
        initFirebase()
        // TODO: 23/06/2018 Crashlytics
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    private fun initDI() {
        val component = DaggerAppComponent.builder()
                .application(this)
                .build()

        component.inject(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }
    }
}