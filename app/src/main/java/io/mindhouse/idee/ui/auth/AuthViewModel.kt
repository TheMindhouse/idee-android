package io.mindhouse.idee.ui.auth

import io.mindhouse.idee.ui.base.BaseViewModel
import io.mindhouse.idee.ui.base.ViewState
import javax.inject.Inject

/**
 * Created by kmisztal on 23/06/2018.
 *
 * @author Krzysztof Misztal
 */

class AuthViewModel @Inject constructor(

) : BaseViewModel<ViewState>() {

    override val initialState: ViewState = object : ViewState {
        override val errorMessage: String?
            get() = null
    }

}