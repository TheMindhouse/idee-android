package io.mindhouse.idee.data.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

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

    companion object {
        const val ADMIN = "admin"
        const val READER = "idea_reader"
        const val EDITOR = "idea_editor"

        enum class Role { OWNER, ADMIN, READER, EDITOR }
    }

    @IgnoredOnParcel
    @get:Exclude
    val isShared = !roles.isEmpty()

    fun roleOf(user: User): Role? {
        if (user.id == id) return Role.ADMIN
        if (user.email == null) return null

        val role = roles[user.email] ?: return null
        return when (role) {
            ADMIN -> Role.ADMIN
            READER -> Role.READER
            EDITOR -> Role.EDITOR
            else -> {
                Timber.e("Found not supported role: $role")
                null
            }
        }
    }

}