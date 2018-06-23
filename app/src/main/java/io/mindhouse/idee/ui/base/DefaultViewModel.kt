package io.mindhouse.idee.ui.base

/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
class DefaultViewModel : BaseViewModel<ViewState>() {
    override val initialState: ViewState = SimpleViewState()


}