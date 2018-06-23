package io.mindhouse.idee.di.module

import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import io.mindhouse.idee.di.utils.ViewModelFactory

@Module
abstract class ViewModelModule {

//    @Binds
//    @IntoMap
//    @ViewModelKey(LoginViewModel::class)
//    abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}