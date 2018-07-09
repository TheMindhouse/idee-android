package io.mindhouse.idee.ui.board

import io.mindhouse.idee.ExceptionHandler
import io.mindhouse.idee.R
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.UsersRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.User
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.ui.base.BaseViewModel
import io.mindhouse.idee.utils.isEmail
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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

    override val initialState = EditBoardViewState(false, false, emptyList())

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
        val attendees = ArrayList<BoardAttendee>()
        val toFetch = ArrayList<String>()

        val owner = cachedUsers[board.ownerId]
        if (owner?.email != null) {
            attendees.add(BoardAttendee(owner.id, owner.name, owner.email, owner.avatarUrl, R.string.role_owner))
        } else {
            toFetch.add(board.ownerId)
        }

        board.roles.forEach { entry ->
            val email = entry.key
            val roleRes = parseRole(entry.value).nameRes

            val user = cachedUsers[email]
            if (user != null) {
                val attendee = BoardAttendee(user.id, user.name, email, user.avatarUrl, roleRes)
                attendees.add(attendee)
            } else {
                val attendee = BoardAttendee(null, null, email, null, roleRes)
                attendees.add(attendee)
                toFetch.add(email)
            }
        }

        postState(initialState.copy(attendees = attendees))
        fetchUsers(toFetch)
    }

    private fun fetchUsers(identifiers: List<String>) {
        val sources = ArrayList<Maybe<User>>()
        identifiers.forEach {
            val publisher = if (it.isEmail) {
                usersRepository.findUserByEmail(it)
            } else {
                usersRepository.findUserById(it)
            }
            sources.add(publisher)
        }

        val disposable = Maybe.merge(sources)
                .subscribeOn(ioScheduler)
                .doOnNext { cacheUser(it) }
                .subscribeBy(
                        onError = { onError(it, "fetching users") },
                        onComplete = { rebuildAttendees() }
                )

        addDisposable(disposable)
    }

    private fun rebuildAttendees() {
        val attendees = LinkedHashSet<BoardAttendee>()

        val owner = cachedUsers[board.ownerId]
        if (owner?.email != null) {
            attendees.add(BoardAttendee(owner.id, owner.name, owner.email, owner.avatarUrl, R.string.role_owner))
        }

        state.attendees.forEach { attendee ->
            val cached = cachedUsers[attendee.email]
            if (cached != null) {
                attendees.add(BoardAttendee(cached.id, cached.name, attendee.email, cached.avatarUrl, attendee.role))
            } else {
                attendees.add(attendee)
            }
        }

        postState(state.copy(attendees = attendees.toList()))
    }

    private fun cacheUser(user: User) {
        if (user.email != null)
            cachedUsers[user.email] = user
        cachedUsers[user.id] = user
    }

    private fun onError(throwable: Throwable, action: String) {
        Timber.e(throwable, "Error while $action!")
        val msg = exceptionHandler.getErrorMessage(throwable)
        val newState = state.copy(isLoading = false, errorMessage = msg)
        postState(newState)
    }

    private fun parseRole(role: String): Board.Companion.Role {
        return when (role) {
            Board.ADMIN -> Board.Companion.Role.ADMIN
            Board.READER -> Board.Companion.Role.READER
            Board.EDITOR -> Board.Companion.Role.EDITOR
            else -> Board.Companion.Role.UNKNOWN
        }

    }
}