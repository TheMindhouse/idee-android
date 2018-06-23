package io.mindhouse.idee.di.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Generic ViewModelFactory. ViewModel has to be registered in [io.mindhouse.idee.di.module.ViewModelModule]
 */
@Singleton
class ViewModelFactory @Inject constructor(
        private val creators: Map<Class<out ViewModel>,
                @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val vm = creators[modelClass]?.get()
                ?: throw IllegalStateException("${modelClass.name} is not registered in ViewModelFactory!")

        return vm as T
    }
}