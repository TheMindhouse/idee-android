package io.mindhouse.idee.ui.account

import io.mindhouse.idee.data.model.Board

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class BoardViewState(
        val board: Board,
        val isOwned: Boolean,
        val isShared: Boolean
)