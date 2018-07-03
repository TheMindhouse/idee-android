package io.mindhouse.idee.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Created by kmisztal on 23/06/2018.
 *
 * @author Krzysztof Misztal
 */
@IgnoreExtraProperties
data class User(
        @get:Exclude
        val id: String = "",
        val name: String? = null,
        val email: String? = null,
        val avatarUrl: String? = null,

        @get:Exclude
        val isAnonymous: Boolean = false
)