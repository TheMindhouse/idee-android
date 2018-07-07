package io.mindhouse.idee.utils

import android.util.Patterns

/**
 * Created by kmisztal on 03/07/2018.
 *
 * @author Krzysztof Misztal
 */
val String.isEmail: Boolean
    get() {
        if (this.isEmpty()) return false
        return Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }