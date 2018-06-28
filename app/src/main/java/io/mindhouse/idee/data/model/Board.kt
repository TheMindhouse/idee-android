package io.mindhouse.idee.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
@IgnoreExtraProperties
data class Board(
        @get:Exclude
        val id: String = "",
        val ownerId: String = "",
        val name: String = "",
        val roles: Map<String, String> = emptyMap()
)