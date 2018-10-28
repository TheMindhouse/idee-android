package io.mindhouse.idee.ui.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import io.mindhouse.idee.ExceptionHandler
import io.mindhouse.idee.R
import io.mindhouse.idee.data.AuthorizeRepository
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.mindhouse.idee.ui.base.BaseViewModel
import io.mindhouse.idee.utils.asSingle
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


/**
 * Created by kmisztal on 23/06/2018.
 *
 * @author Krzysztof Misztal
 */

class AuthViewModel @Inject constructor(
        context: Context,
        private val authorizeRepository: AuthorizeRepository,
        private val boardsRepository: BoardsRepository,
        @IOScheduler private val ioScheduler: Scheduler,
        private val exceptionHandler: ExceptionHandler
) : BaseViewModel<AuthViewState>() {

    override val initialState = AuthViewState(false, authorizeRepository.isLoggedIn, null)

    private val defaultBoardName = context.getString(R.string.default_board_name)

    fun onFacebookToken(fbToken: LoginResult) {
        postState(AuthViewState(true, false, null))

        val disposable = authorizeRepository.signInWithFacebook(fbToken.accessToken)
                .subscribeOn(ioScheduler)
                .flatMapCompletable { createBoardIfNecessary() }
                .subscribeBy(
                        onComplete = ::onLoggedIn,
                        onError = ::onError
                )
        addDisposable(disposable)
    }

    fun onGoogleToken(task: Task<GoogleSignInAccount>) {
        postState(AuthViewState(true, false, null))
        val disposable = task.asSingle()
                .subscribeOn(ioScheduler)
                .flatMap { authorizeRepository.signInWithGoogle(it) }
                .flatMapCompletable { createBoardIfNecessary() }
                .subscribeBy(
                        onComplete = ::onLoggedIn,
                        onError = ::onError
                )
        addDisposable(disposable)
    }

    private fun createBoardIfNecessary(): Completable {
        return boardsRepository.getMyBoards()
                .flatMapCompletable {
                    if (!it.isEmpty()) {
                        Completable.complete()
                    } else {
                        boardsRepository.createBoard(Board(name = defaultBoardName))
                                .toCompletable()
                    }
                }
    }

    private fun onLoggedIn() {
        postState(AuthViewState(false, true, null))
    }

    private fun onError(throwable: Throwable) {
        val message = exceptionHandler.getErrorMessage(throwable)
        postState(AuthViewState(false, false, message))
    }
}