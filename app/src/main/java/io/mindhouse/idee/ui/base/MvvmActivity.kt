package io.mindhouse.idee.ui.base

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.mindhouse.idee.data.AuthorizeRepository
import io.mindhouse.idee.di.utils.ViewModelFactory
import io.mindhouse.idee.ui.auth.AuthActivity
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
abstract class MvvmActivity<S : ViewState, out VM : BaseViewModel<S>> : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var authorizeRepository: AuthorizeRepository

    val viewModel: VM by lazy { createViewModel() }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel.stateData.observeSafe(this) { renderInternal(it) }
    }

    override fun onStart() {
        super.onStart()
        observeAuthState()
    }

    override fun onStop() {
        disposable?.dispose()
        super.onStop()
    }

    open fun renderError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

    //==========================================================================
    // Protected
    //==========================================================================

    protected open fun onLoggedOut() {

        val intent = AuthActivity.newIntent(this)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    //==========================================================================
    // Abstract
    //==========================================================================

    /**
     * Render state. Error message is handled by base class, so you don't have to handle it here.
     * To change rendering of the error message override []
     */
    abstract fun render(state: S)

    /**
     * Function that creates view model. **Should use [android.arch.lifecycle.ViewModelProviders]!**
     */
    abstract fun createViewModel(): VM

    //==========================================================================
    // Private
    //==========================================================================

    private fun observeAuthState() {
        disposable = authorizeRepository.observeAuthState()
                .filter { !it }
                .subscribeBy(
                        onNext = { onLoggedOut() }
                )
    }

    private fun renderInternal(state: S) {
        Timber.d("Rendering state: $state")
        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            renderError(errorMessage)
        }

        render(state)
    }
}