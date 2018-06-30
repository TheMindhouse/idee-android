package io.mindhouse.idee.ui.board

import io.mindhouse.idee.ExceptionHandler
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.ui.base.BaseViewModel
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kmisztal on 28/06/2018.
 *
 * @author Krzysztof Misztal
 */
class EditBoardViewModel @Inject constructor(
        private val boardsRepository: BoardsRepository,
        private val exceptionHandler: ExceptionHandler,
        @IOScheduler private val ioScheduler: Scheduler
) : BaseViewModel<EditBoardViewState>() {

    override val initialState = EditBoardViewState(false, false, null)

    fun createNewBoard(name: String) {
        val state = state.copy(isLoading = true)
        postState(state)
        boardsRepository.createBoard(name)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onSuccess = {
                            //Change should be observed, no need to do anything
                            postState(state.copy(isLoading = false, isSaved = true))
                            Timber.i("Successfully created board: $name")
                        },
                        onError = { onError(it, "creating board") }
                )
    }

    fun updateBoard(board: Board) {
        val state = state.copy(isLoading = true)
        postState(state)
        boardsRepository.updateBoard(board)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onComplete = { postState(state.copy(isLoading = false, isSaved = true)) },
                        onError = { onError(it, "updating board") }
                )
    }

    private fun onError(throwable: Throwable, action: String) {
        Timber.e(throwable, "Error while $action!")
        val msg = exceptionHandler.getErrorMessage(throwable)
        val newState = state.copy(isLoading = false, errorMessage = msg)
        postState(newState)
    }
}