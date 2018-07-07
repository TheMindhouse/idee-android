package io.mindhouse.idee.ui.board

/**
 * Created by kmisztal on 03/07/2018.
 *
 * @author Krzysztof Misztal
 */
data class BoardAttendee(
        val id: String?,
        val displayName: String?,
        val email: String,
        val avatar: String?,
        val role: String
)