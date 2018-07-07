package io.mindhouse.idee.data.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
@IgnoreExtraProperties
@Parcelize
data class Idea(
        @get:Exclude
        val id: String = "",

        @get:Exclude
        val boardId: String = "",

        val name: String = "",
        val description: String? = null,
        val ease: Int = 10,
        val confidence: Int = 10,
        val impact: Int = 10

) : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    val average: Float = (ease + confidence + impact).toFloat() / 3

}