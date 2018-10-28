package io.mindhouse.idee.utils

import android.util.Patterns
import com.google.android.gms.tasks.Task
import io.reactivex.Single


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

//==========================================================================
// RX
//==========================================================================

fun <TResult> Task<TResult>.asSingle(): Single<TResult> = Single.create { emitter ->
    this.addOnSuccessListener { result -> emitter.onSuccess(result) }
    this.addOnFailureListener { e -> emitter.onError(e) }
}