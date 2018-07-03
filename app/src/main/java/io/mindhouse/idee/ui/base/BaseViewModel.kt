package io.mindhouse.idee.ui.base

import android.arch.lifecycle.ViewModel
import io.mindhouse.idee.utils.NonNullableMutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base ViewModel class.
 *
 * @author Krzysztof Misztal
 */
abstract class BaseViewModel<T : ViewState> : ViewModel() {

    protected abstract val initialState: T

    val stateData: NonNullableMutableLiveData<T> by lazy { NonNullableMutableLiveData(initialState) }
    val state: T get() = stateData.value

    private val disposables = CompositeDisposable()

    protected fun postState(state: T) {
        stateData.postValue(state)
    }

    protected fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    protected fun clearDisposables() {
        disposables.clear()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}