package io.mindhouse.idee.ui.base

import android.arch.lifecycle.ViewModelProviders

/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
abstract class DefaultActivity : MvvmActivity<ViewState, DefaultViewModel>() {

    override fun render(state: ViewState) {
        // do nothing
    }

    override fun createViewModel(): DefaultViewModel {
        return ViewModelProviders.of(this).get(DefaultViewModel::class.java)
    }
}