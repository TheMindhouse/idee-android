package io.mindhouse.idee.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Looper

/**
 * Wrapper for LiveData that makes sure that null values will never occur.
 *
 * You should use method [observeSafe].
 *
 * @author Krzysztof Misztal
 */
class NonNullableMutableLiveData<T>(
        private val initialValue: T
) : MutableLiveData<T>() {

    init {
        if (Looper.myLooper() === Looper.getMainLooper()) {
            value = initialValue
        } else {
            postValue(initialValue)
        }
    }

    @Suppress("RedundantOverride")
    override fun postValue(value: T) {
        //we don't accept null here
        super.postValue(value)
    }

    @Suppress("RedundantOverride")
    override fun setValue(value: T) {
        //we don't accept null here
        super.setValue(value)
    }

    override fun getValue(): T {
        //as class doesn't accept null values, that only means that initial value
        //hasn't been posted yet (in case constructor wasn't called in the main
        //thread, and new value "waits" in the message queue).
        return super.getValue() ?: initialValue
    }

    fun observeSafe(owner: LifecycleOwner, observer: ((T) -> Unit)) {
        observe(owner, Observer {
            val value = it
                    ?: throw IllegalStateException("${this::class.java.name} published null value!!")

            observer.invoke(value)
        })
    }
}