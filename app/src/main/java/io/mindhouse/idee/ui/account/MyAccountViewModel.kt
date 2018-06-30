package io.mindhouse.idee.ui.account

import io.mindhouse.idee.data.AuthorizeRepository
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.ui.base.BaseViewModel
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
class MyAccountViewModel @Inject constructor(
        private val authorizeRepository: AuthorizeRepository,
        private val boardsRepository: BoardsRepository,
        @IOScheduler private val ioScheduler: Scheduler
) : BaseViewModel<MyAccountViewState>() {

    override val initialState = MyAccountViewState(authorizeRepository.currentUser, emptyList())
    private var boardsDisposable: Disposable? = null

    init {
        observeBoards()
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun observeBoards() {
        //// TODO: 25/06/2018 retry after delay!
        boardsDisposable?.dispose()
        val disposable = boardsRepository.observeBoards()
                .subscribeOn(ioScheduler)
                .map { it.map { it.toViewState() } }
//                .retryWhen(RetryWithDelay(0, 3000))
                .subscribeBy(
                        onNext = ::onBoards,
                        onError = ::onBoardsObserveError
                )

        boardsDisposable = disposable
        addDisposable(disposable)
    }

    private fun onBoards(boards: List<BoardViewState>) {
        val newState = state.copy(boards = boards)
        postState(newState)
    }

    private fun onBoardsObserveError(throwable: Throwable) {
        Timber.e(throwable, "Error observing boards changes, resubscribing...")
        observeBoards()
    }

    private fun Board.toViewState(): BoardViewState {
        val myId = authorizeRepository.currentUser?.id
        return BoardViewState(this, myId == this.ownerId, !this.roles.isEmpty())
    }
}