package io.mindhouse.idee.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.mindhouse.idee.di.utils.ViewModelFactory
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
abstract class BaseActivity<S : ViewState, out VM : BaseViewModel<S>> : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val viewModel: VM by lazy { createViewModel() }

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        viewModel.stateData.observeSafe(this) { renderInternal(it) }
    }

    override fun onPause() {
        disposable?.dispose()
        super.onPause()
    }

    open fun renderError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

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

    private fun renderInternal(state: S) {
        Timber.d("Rendering state: $state")
        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            renderError(errorMessage)
        }

        render(state)
    }
}