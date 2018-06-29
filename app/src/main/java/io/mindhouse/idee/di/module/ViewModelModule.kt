package io.mindhouse.idee.di.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.mindhouse.idee.di.utils.ViewModelFactory
import io.mindhouse.idee.di.utils.ViewModelKey
import io.mindhouse.idee.ui.account.MyAccountViewModel
import io.mindhouse.idee.ui.auth.AuthViewModel
import io.mindhouse.idee.ui.board.EditBoardViewModel
import io.mindhouse.idee.ui.idea.list.IdeaListViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun authViewModel(viewModel: AuthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyAccountViewModel::class)
    abstract fun myAccountViewModel(viewModel: MyAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditBoardViewModel::class)
    abstract fun editBoardViewModel(viewModel: EditBoardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IdeaListViewModel::class)
    abstract fun ideaListViewModel(viewModel: IdeaListViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}