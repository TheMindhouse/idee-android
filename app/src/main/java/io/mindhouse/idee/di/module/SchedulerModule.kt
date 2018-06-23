package io.mindhouse.idee.di.module

import io.mindhouse.idee.di.qualifier.ComputationScheduler
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.di.qualifier.MainScheduler
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class SchedulerModule {

    @Provides
    @MainScheduler
    fun provideMainThreadScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    @Provides
    @IOScheduler
    fun provideIoScheduler(): Scheduler {
        return Schedulers.io()
    }

    @Provides
    @ComputationScheduler
    fun provideComputationScheduler(): Scheduler {
        return Schedulers.computation()
    }
}

