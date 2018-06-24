package io.mindhouse.idee.data.model

import com.google.firebase.firestore.Exclude

/**
 * Created by kmisztal on 23/06/2018.
 *
 * @author Krzysztof Misztal
 */
data class User(
        val id: String,
        val name: String?,
        val email: String?,
        val avatarUrl: String?,

        @Exclude
        val isAnonymous: Boolean = false
)