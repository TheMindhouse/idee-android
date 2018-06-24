package io.mindhouse.idee.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import durdinapps.rxfirebase2.RxFirestore
import io.mindhouse.idee.data.model.User
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
@Singleton
class UsersRepository @Inject constructor() {

    private val db by lazy { FirebaseFirestore.getInstance() }

    fun upsertMe(me: User): Completable {
        val doc = db.collection("users").document(me.id)
        return RxFirestore.setDocument(doc, me, SetOptions.merge())
    }

    fun findUser(id: String): Maybe<User> {
        val doc = db.collection("users").document(id)
        return RxFirestore.getDocument(doc, User::class.java)
    }

}