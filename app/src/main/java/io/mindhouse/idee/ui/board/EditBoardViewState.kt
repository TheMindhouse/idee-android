package io.mindhouse.idee.ui.board

import io.mindhouse.idee.ui.base.ViewState

/**
 * Created by kmisztal on 28/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class EditBoardViewState(
        val isLoading: Boolean,
        val isSaved: Boolean,
        val attendees: List<BoardAttendee>,
        override val errorMessage: String? = null
) : ViewState