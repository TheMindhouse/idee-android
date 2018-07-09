package io.mindhouse.idee.ui.board

import android.support.annotation.StringRes

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
        @StringRes
        val role: Int
)