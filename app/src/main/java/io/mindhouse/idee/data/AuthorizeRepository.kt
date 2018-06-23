package io.mindhouse.idee.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

    //todo don't expose FirebaseUser class
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isLoggedIn() = getCurrentUser() != null

}