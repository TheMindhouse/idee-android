package io.mindhouse.idee.di.module

import android.content.Context
import io.mindhouse.idee.App
import dagger.Module
import dagger.Provides

/**
 * Created by kmisztal on 15.06.2017.
 *
 * @author Krzysztof Misztal
 */
@Module
class AppModule {

    @Provides
    fun provideApplicationContext(app: App): Context {
        return app.applicationContext
    }
}