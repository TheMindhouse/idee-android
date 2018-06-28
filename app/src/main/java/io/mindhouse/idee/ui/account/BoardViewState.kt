package io.mindhouse.idee.ui.account

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class BoardViewState(
        val id: String,
        val name: String,
        val isOwned: Boolean,
        val isShared: Boolean
)