package io.mindhouse.idee.ui.idea.list

import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.ViewState

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class IdeaListViewState(
        val board: Board?,
        val shareStatus: Int,
        val ideas: List<Idea>,
        val isLoading: Boolean,
        override val errorMessage: String? = null
) : ViewState