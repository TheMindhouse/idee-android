package io.mindhouse.idee.data.model

/**
 * Created by kmisztal on 23/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class User(
        val id: String,
        val email: String?,
        val avatarUrl: String?,
        val isAnonymous: Boolean = false
)