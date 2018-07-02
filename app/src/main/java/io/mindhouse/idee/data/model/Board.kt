package io.mindhouse.idee.data.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
@IgnoreExtraProperties
@Parcelize
data class Board(
        @get:Exclude
        val id: String = "",
        val ownerId: String = "",
        val name: String = "",
        val roles: Map<String, String> = emptyMap()
) : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    val isShared = !roles.isEmpty()

}