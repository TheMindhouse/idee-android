package io.mindhouse.idee.ui.auth

import io.mindhouse.idee.ui.base.ViewState

/**
 * Created by kmisztal on 23/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class AuthViewState(
        val isLoading: Boolean,
        val isLoggedId: Boolean,
        override val errorMessage: String?
) : ViewState