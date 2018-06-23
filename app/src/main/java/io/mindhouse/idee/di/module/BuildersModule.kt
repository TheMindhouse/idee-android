package io.mindhouse.idee.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.mindhouse.idee.ui.SplashActivity

/**
 * Binds all sub-components withing the app
 *
 * @author Krzysztof Misztal
 */
@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun bindSpashActivity(): SplashActivity

}