package io.mindhouse.idee.data

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.mindhouse.idee.data.model.User
import io.mindhouse.idee.di.qualifier.IOScheduler
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kmisztal on 23/06/2018.
 *
 * @author Krzysztof Misztal
 */
@Singleton
class AuthorizeRepository @Inject constructor(
        private val usersRepository: UsersRepository,
        @IOScheduler private val ioScheduler: Scheduler
) {

    private val auth by lazy { FirebaseAuth.getInstance() }

    companion object {
        val FACEBOOK_PERMISSIONS = listOf("email", "public_profile")
    }

    val currentUser: User? get() = auth.currentUser?.toUser()
    val isLoggedIn: Boolean get() = currentUser != null

    init {
        upsertMe()
    }

    fun signInWithFacebook(token: AccessToken): Single<User> {
        val credential = FacebookAuthProvider.getCredential(token.token)
        return signInWithCredential(credential)
    }

    fun signInWithGoogle(account: GoogleSignInAccount): Single<User> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: AuthCredential): Single<User> {
        return RxFirebaseAuth.signInWithCredential(auth, credential)
                .filter { it.user != null }
                .toSingle()
                .map { it.user.toUser() }
                .doOnSuccess {
                    Timber.i("Logged in as: $it")
                    upsertMe()
                }
    }

    private fun upsertMe() {
        currentUser?.let { me ->
            usersRepository.upsertMe(me)
                    .subscribeOn(ioScheduler)
                    .subscribeBy(
                            onComplete = { Timber.d("Have upsert my account: $me") },
                            onError = { Timber.e(it, "Failed to upsert my account: $me") }
                    )
        }
    }

    private fun FirebaseUser.toUser(): User {
        return User(this.uid, this.displayName, this.email, this.photoUrl?.toString(), this.isAnonymous)
    }
}