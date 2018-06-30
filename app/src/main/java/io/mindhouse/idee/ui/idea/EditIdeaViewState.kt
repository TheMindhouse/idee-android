package io.mindhouse.idee.ui.idea

import io.mindhouse.idee.ui.base.ViewState

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class EditIdeaViewState(
        val isLoading: Boolean,
        val isSaved: Boolean,
        override val errorMessage: String? = null
) : ViewState