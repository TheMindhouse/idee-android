package io.mindhouse.idee.ui.account

import io.mindhouse.idee.data.model.User
import io.mindhouse.idee.ui.base.ViewState

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class MyAccountViewState(
        val me: User?,
        val boards: List<BoardViewState>,
        override val errorMessage: String? = null
) : ViewState