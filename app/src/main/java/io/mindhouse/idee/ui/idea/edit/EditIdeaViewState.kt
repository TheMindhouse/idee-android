package io.mindhouse.idee.ui.idea.edit

import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.ViewState

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class EditIdeaViewState(
        val isLoading: Boolean,
        val savedIdea: Idea?,
        override val errorMessage: String? = null
) : ViewState