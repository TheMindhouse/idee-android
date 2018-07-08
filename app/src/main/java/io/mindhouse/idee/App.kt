package io.mindhouse.idee

import android.app.Activity
import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import io.mindhouse.idee.di.component.DaggerAppComponent
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
open class App : Application(), HasActivityInjector {

    private lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        initDI()
        initTimber()
        initFirebase()
        initCrashlytics()
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    private fun initCrashlytics() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }
    }

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

        } else {
            analytics = FirebaseAnalytics.getInstance(this)
        }
    }
}