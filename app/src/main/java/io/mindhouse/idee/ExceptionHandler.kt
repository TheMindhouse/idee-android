package io.mindhouse.idee

import android.content.Context
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
        //todo firestore permission denied!
            else -> getString(R.string.error_unknown)

        }
    }


    private fun getString(id: Int): String = context.getString(id)

}