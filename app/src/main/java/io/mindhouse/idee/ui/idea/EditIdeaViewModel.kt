package io.mindhouse.idee.ui.idea

import io.mindhouse.idee.ExceptionHandler
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.ui.base.BaseViewModel
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class EditIdeaViewModel @Inject constructor(
        private val boardsRepository: BoardsRepository,
        private val exceptionHandler: ExceptionHandler,
        @IOScheduler private val ioScheduler: Scheduler
) : BaseViewModel<EditIdeaViewState>() {

    override val initialState = EditIdeaViewState(false, false)

    fun createIdea(idea: Idea) {
        //todo result takes long when slow internet. figure it out
        postState(state.copy(isLoading = true))
        val disposable = boardsRepository.createIdea(idea)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onSuccess = {
                            Timber.d("Successfully created idea: $idea")
                            postState(state.copy(isLoading = false, isSaved = true))
                        },
                        onError = { onError(it, "creating idea") }
                )
        addDisposable(disposable)
    }

    fun updateIdea(idea: Idea) {
        postState(state.copy(isLoading = true))
        val disposable = boardsRepository.updateIdea(idea)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onComplete = {
                            Timber.d("Successfully updated idea: $idea")
                            postState(state.copy(isLoading = false, isSaved = true))
                        },
                        onError = { onError(it, "updating idea") }
                )
        addDisposable(disposable)
    }

    //==========================================================================

    private fun onError(throwable: Throwable, action: String) {
        Timber.e(throwable, "Error while $action!")
        val msg = exceptionHandler.getErrorMessage(throwable)
        val newState = state.copy(isLoading = false, errorMessage = msg)
        postState(newState)
    }
}