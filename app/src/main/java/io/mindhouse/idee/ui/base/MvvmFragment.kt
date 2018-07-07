package io.mindhouse.idee.ui.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import dagger.android.support.AndroidSupportInjection
import io.mindhouse.idee.di.utils.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
abstract class MvvmFragment<S : ViewState, out VM : BaseViewModel<S>> : Fragment() {

    @Inject
    protected lateinit var viewModelFactory: ViewModelFactory

    protected val viewModel: VM by lazy { createViewModel() }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.stateData.observeSafe(this, { renderInternal(it) })
    }

    open fun renderError(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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

    private fun renderInternal(state: S) {
        Timber.d("Rendering state: $state")
        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            renderError(errorMessage)
        }

        render(state)
    }

}