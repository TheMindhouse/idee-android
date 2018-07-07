package io.mindhouse.idee.ui.board

import io.mindhouse.idee.ExceptionHandler
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.UsersRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.User
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
        private val usersRepository: UsersRepository,
        private val exceptionHandler: ExceptionHandler,
        @IOScheduler private val ioScheduler: Scheduler
) : BaseViewModel<EditBoardViewState>() {

    var board = Board()
        set(value) {
            field = value
            loadBoardData()
        }

    override val initialState = EditBoardViewState(false, false, emptyList(), null)

    private val cachedUsers: MutableMap<String, User> = HashMap()
    private val defaultRole = Board.EDITOR

    fun createNewBoard(name: String) {
        val board = board.copy(name = name)
        boardsRepository.createBoard(board)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onSuccess = { Timber.i("Successfully created board: $name") },
                        onError = { onError(it, "creating board") }
                )

        postState(state.copy(isSaved = true))
    }

    fun updateBoard(updatedName: String) {
        board = board.copy(name = updatedName)
        boardsRepository.updateBoard(board)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onComplete = { Timber.i("Successfully updated board: $board") },
                        onError = { onError(it, "updating board") }
                )

        postState(state.copy(isSaved = true))
    }

    fun addEmail(email: String) {
        val roles = board.roles.toMutableMap()
        roles[email] = defaultRole

        board = board.copy(roles = roles)
    }

    fun removeEmail(email: String) {
        val roles = board.roles.toMutableMap()
        roles.remove(email)

        board = board.copy(roles = roles)
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun loadBoardData() {
        clearDisposables()
        val attendees = ArrayList<BoardAttendee>()
        val toFetch = ArrayList<String>()

        board.roles.forEach { email, role ->
            val user = cachedUsers[email]
            if (user != null) {
                val attendee = BoardAttendee(user.id, user.name, email, user.avatarUrl, role)
                attendees.add(attendee)
            } else {
                val attendee = BoardAttendee(null, null, email, null, role)
                attendees.add(attendee)
                toFetch.add(email)
            }
        }

        postState(initialState.copy(attendees = attendees))
        toFetch.forEach { fetchUserByEmail(it) }
    }

    private fun fetchUserByEmail(email: String) {
        val disposable = usersRepository.findUserByEmail(email)
                .subscribeOn(ioScheduler)
                .subscribeBy(
                        onSuccess = ::onUserFetched,
                        onError = { onError(it, "fetching user") }
                )

        addDisposable(disposable)
    }

    private fun onUserFetched(user: User) {
        if (user.email == null) {
            Timber.e("Error, fetched user has null email: $user")
            return
        }

        cachedUsers[user.email] = user

        val attendees = state.attendees.toMutableList()
        attendees.removeAll { it.email == user.email }

        val attendee = BoardAttendee(user.id, user.name, user.email, user.avatarUrl, defaultRole)
        attendees.add(attendee)
        postState(state.copy(attendees = attendees))
    }

    private fun onError(throwable: Throwable, action: String) {
        Timber.e(throwable, "Error while $action!")
        val msg = exceptionHandler.getErrorMessage(throwable)
        val newState = state.copy(isLoading = false, errorMessage = msg)
        postState(newState)
    }
}