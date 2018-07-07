package io.mindhouse.idee.di.component

import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import io.mindhouse.idee.App
import io.mindhouse.idee.di.module.*
import javax.inject.Singleton


/**
 * Created by kmisztal on 02.06.2017.
 *
 * @author Krzysztof Misztal
 */
@Singleton
@Component(modules = [BuildersModule::class, AndroidSupportInjectionModule::class, AppModule::class,
    SchedulerModule::class, ViewModelModule::class, DataModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

}