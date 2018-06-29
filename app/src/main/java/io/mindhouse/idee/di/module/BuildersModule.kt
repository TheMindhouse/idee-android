package io.mindhouse.idee.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.mindhouse.idee.ui.MainActivity
import io.mindhouse.idee.ui.SplashActivity
import io.mindhouse.idee.ui.account.MyAccountFragment
import io.mindhouse.idee.ui.auth.AuthActivity
import io.mindhouse.idee.ui.board.BoardActivity
import io.mindhouse.idee.ui.board.EditBoardFragment

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
    abstract fun bindMyAccountFragment(): MyAccountFragment

    @ContributesAndroidInjector
    abstract fun bindEditBoardFragment(): EditBoardFragment

}