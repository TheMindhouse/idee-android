package io.mindhouse.idee.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import durdinapps.rxfirebase2.RxFirestore
import io.mindhouse.idee.data.model.Board
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
@Singleton
class BoardsRepository @Inject constructor(
        private val authorizeRepository: AuthorizeRepository
) {

    private val db by lazy { FirebaseFirestore.getInstance() }

    fun observeBoards(): Flowable<List<Board>> {
        val me = authorizeRepository.currentUser
                ?: return Flowable.error(IllegalArgumentException("Not logged in!"))

        val ownedRef = db.collection("boards").whereEqualTo("ownerId", me.id)
        val invitedRef = db.collection("boards")
                .whereGreaterThan("roles.${me.id}", "")

        return Flowable.combineLatest(
                RxFirestore.observeQueryRef(ownedRef),
                RxFirestore.observeQueryRef(invitedRef),

                BiFunction { q1: QuerySnapshot, q2: QuerySnapshot ->
                    val docs = q1.documents + q2.documents
                    docs.mapNotNull {
                        it.toObject(Board::class.java)?.copy(id = it.id)
                    }
                }
        )
    }

    fun createBoard(name: String): Single<Board> {
        val me = authorizeRepository.currentUser
                ?: return Single.error(IllegalArgumentException("Not logged in!"))

        val docRef = db.collection("boards")
        val board = Board("", me.id, name, emptyMap())

        return RxFirestore.addDocument(docRef, board)
                .map { board.copy(id = it.id) }
    }


}