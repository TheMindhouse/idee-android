package io.mindhouse.idee.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.mindhouse.idee.ui.MainActivity
import io.mindhouse.idee.ui.SplashActivity
import io.mindhouse.idee.ui.account.MyAccountFragment
import io.mindhouse.idee.ui.auth.AuthActivity
import io.mindhouse.idee.ui.base.DefaultFragment
import io.mindhouse.idee.ui.board.BoardActivity
import io.mindhouse.idee.ui.board.EditBoardFragment
import io.mindhouse.idee.ui.idea.IdeaActivity
import io.mindhouse.idee.ui.idea.IdeaFragment
import io.mindhouse.idee.ui.idea.edit.EditIdeaFragment
import io.mindhouse.idee.ui.idea.list.IdeaListFragment

/**
 * Binds all sub-components withing the app
 *
 * @author Krzysztof Misztal
 */
@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    abstract fun bindSpashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun bindAuthActivity(): AuthActivity

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindBoardActivity(): BoardActivity

    @ContributesAndroidInjector
    abstract fun bindIdeaActivity(): IdeaActivity

    @ContributesAndroidInjector
    abstract fun bindMyAccountFragment(): MyAccountFragment

    @ContributesAndroidInjector
    abstract fun bindEditBoardFragment(): EditBoardFragment

    @ContributesAndroidInjector
    abstract fun bindEditIdeaFragment(): EditIdeaFragment

    @ContributesAndroidInjector
    abstract fun bindIdeaListFragment(): IdeaListFragment

    @ContributesAndroidInjector
    abstract fun bindIdeaFragment(): IdeaFragment

    @ContributesAndroidInjector
    abstract fun bindDefaultFragment(): DefaultFragment

}