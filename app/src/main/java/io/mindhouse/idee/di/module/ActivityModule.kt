package io.mindhouse.idee.di.module

import android.app.Activity
import dagger.Module
import dagger.Provides

/**
 * Created by kmisztal on 15.06.2017.
 *
 * @author Krzysztof Misztal
 */
@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }
}