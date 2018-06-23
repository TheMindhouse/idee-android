package io.mindhouse.idee.data

import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.mindhouse.idee.data.model.User
import io.reactivex.Single
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

) {

    private val auth by lazy { FirebaseAuth.getInstance() }

    companion object {
        val FACEBOOK_PERMISSIONS = listOf("email", "public_profile")
    }

    val currentUser: User? get() = auth.currentUser?.toUser()
    val isLoggedIn: Boolean get() = currentUser != null

    fun signInWithFacebook(token: AccessToken): Single<User> {
        val provider = FacebookAuthProvider.getCredential(token.token)
        return RxFirebaseAuth.signInWithCredential(auth, provider)
                .filter { it.user != null }
                .toSingle()
                .map { it.user.toUser() }
                .doOnSuccess {
                    Timber.i("Logged in as: $it")
                }
    }

    private fun FirebaseUser.toUser(): User {
        return User(this.uid, this.email, this.photoUrl?.toString(), this.isAnonymous)
    }
}