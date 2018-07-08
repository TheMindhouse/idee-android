package io.mindhouse.idee.ui.idea.list

import io.mindhouse.idee.ExceptionHandler
import io.mindhouse.idee.R
import io.mindhouse.idee.data.AuthorizeRepository
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.ui.base.BaseViewModel
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class IdeaListViewModel @Inject constructor(
        private val boardsRepository: BoardsRepository,
        private val authorizeRepository: AuthorizeRepository,
        @IOScheduler private val ioScheduler: Scheduler,
        private val exceptionHandler: ExceptionHandler
) : BaseViewModel<IdeaListViewState>() {

    override val initialState = IdeaListViewState(null, null, R.string.not_shared, emptyList(), false)

    private var ideasDisposable: Disposable? = null

    private var deleteDisposable: Disposable? = null
    private var toDelete: Set<Idea>? = null

    var board: Board? = null
        set(value) {
            if (value != field) {
                field = value
                observeIdeas()
            }
        }

    fun deleteBoard() {
        val board = board ?: return

        boardsRepository.delete(board)
                .subscribeBy(
                        onComplete = { Timber.d("Deleted board: $board") },
                        onError = { onError(it, "deleting board") }
                )
    }

    fun leaveBoard() {
        val email = authorizeRepository.currentUser?.email ?: return
        val board = board ?: return

        boardsRepository.removeRole(board, email)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onComplete = { Timber.d("Left board: $board") },
                        onError = { onError(it, "leaving board") }

                )
    }

    fun scheduleDeletion(idea: Idea, millis: Long) {
        val toDelete = HashSet<Idea>()
        toDelete.add(idea)
        val oldToDelete = this.toDelete
        if (oldToDelete != null) {
            toDelete.addAll(oldToDelete)
        }

        this.toDelete = toDelete
        deleteDisposable?.dispose()
        deleteDisposable = Single.just(toDelete)
                .delay(millis, TimeUnit.MILLISECONDS)
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapCompletable { boardsRepository.deleteIdea(it) }
                .subscribeBy(
                        onComplete = {
                            Timber.d("Ideas deleted.")
                            this.toDelete = null
                        },
                        onError = { onError(it, "deleting ideas") }
                )

        //republish ideas
        onIdeas(state.ideas)
    }

    fun cancelScheduledDeletion() {
        deleteDisposable?.dispose()
        val ideas = state.ideas.toMutableList()
        this.toDelete?.let {
            ideas.addAll(it)
        }
        this.toDelete = null

        onIdeas(ideas)
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun observeIdeas() {
        val board = board
        val me = authorizeRepository.currentUser

        ideasDisposable?.dispose()

        if (board == null || me == null) {
            postState(initialState)
            return
        }

        val role = board.roleOf(me)
        var shareStatus = R.string.not_shared
        if (board.isShared && board.ownerId == authorizeRepository.currentUser?.id) {
            shareStatus = R.string.shared
        } else if (board.isShared) {
            shareStatus = R.string.shared_to_you
        }

        postState(state.copy(board = board, role = role, shareStatus = shareStatus, isLoading = true))
        //// TODO: 29/06/2018 retry after delay
        val disposable = boardsRepository.observeIdeas(board.id)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onNext = ::onIdeas,
                        onError = ::onObservingError
                )

        ideasDisposable = disposable
        addDisposable(disposable)
    }

    private fun onIdeas(ideas: List<Idea>) {
        val visible = ideas.toMutableList()
        toDelete?.let {
            visible.removeAll(toDelete as Iterable<Idea>)
        }
        postState(state.copy(isLoading = false, ideas = visible))
    }

    private fun onError(throwable: Throwable, action: String) {
        Timber.e(throwable, "Error while $action!")
        val msg = exceptionHandler.getErrorMessage(throwable)
        postState(state.copy(isLoading = false, errorMessage = msg))
    }

    private fun onObservingError(throwable: Throwable) {
        Timber.e(throwable, "Error observing ideas for board: $board")
        val msg = exceptionHandler.getErrorMessage(throwable)
        postState(state.copy(isLoading = false, errorMessage = msg))
        observeIdeas()
    }

}