package io.mindhouse.idee.ui.base

import android.arch.lifecycle.ViewModelProviders

/**
 * Created by kmisztal on 10/07/2018.
 *
 * @author Krzysztof Misztal
 */
open class DefaultFragment : MvvmFragment<ViewState, DefaultViewModel>() {

    override fun render(state: ViewState) {
        // do nothing
    }

    override fun createViewModel(): DefaultViewModel {
        return ViewModelProviders.of(this).get(DefaultViewModel::class.java)
    }
}