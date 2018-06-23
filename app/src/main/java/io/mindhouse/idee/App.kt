package io.mindhouse.idee

import android.app.Activity
import android.app.Application
import io.mindhouse.idee.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
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

    companion object {

    }

    override fun onCreate() {
        super.onCreate()
        initDI()
        initTimber()
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
}