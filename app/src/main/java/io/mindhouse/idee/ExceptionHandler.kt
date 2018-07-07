package io.mindhouse.idee

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestoreException
import javax.inject.Inject

/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
class ExceptionHandler @Inject constructor(
        private val context: Context
) {

    /**
     * @return string res of error message
     */
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
        // TODO: 07/07/2018 add more
            is FirebaseFirestoreException -> getString(R.string.error_firestore)
            else -> getString(R.string.error_unknown)

        }
    }


    private fun getString(id: Int): String = context.getString(id)

}