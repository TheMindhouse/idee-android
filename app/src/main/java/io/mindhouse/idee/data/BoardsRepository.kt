package io.mindhouse.idee.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import durdinapps.rxfirebase2.RxFirestore
import io.mindhouse.idee.data.model.Board
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
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

    companion object {
        const val ROLE_ADMIN = "admin"
        const val ROLE_READER = "idea_reader"
        const val ROLE_EDITOR = "idea_editor"
    }

    fun observeBoards(): Flowable<List<Board>> {
        val me = authorizeRepository.currentUser
                ?: return Flowable.error(IllegalArgumentException("Not logged in!"))

        val ownedRef = db.collection("boards").whereEqualTo("ownerId", me.id)
        val adminRef = db.collection("boards").whereEqualTo("roles.${me.email}", ROLE_ADMIN)
        val readerRef = db.collection("boards").whereEqualTo("roles.${me.email}", ROLE_READER)
        val editorRef = db.collection("boards").whereEqualTo("roles.${me.email}", ROLE_EDITOR)

        //todo uncomment when firestore security rules get fixed
//        return Flowable.combineLatest(
//                observeBoardQuery(ownedRef),
//                observeBoardQuery(adminRef),
//                observeBoardQuery(readerRef),
//                observeBoardQuery(editorRef),
//
//                Function4 { r1, r2, r3, r4 ->
//                    r1 + r2 + r3 + r4
//                }
//        )

        return observeBoardQuery(ownedRef)
    }

    fun updateBoard(board: Board): Completable {
        val docRef = db.collection("boards").document(board.id)
        return RxFirestore.setDocument(docRef, board, SetOptions.merge())
    }

    fun createBoard(name: String): Single<Board> {
        val me = authorizeRepository.currentUser
                ?: return Single.error(IllegalArgumentException("Not logged in!"))

        val docRef = db.collection("boards")
        val board = Board("", me.id, name, emptyMap())

        return RxFirestore.addDocument(docRef, board)
                .map { board.copy(id = it.id) }
    }

    fun findBoardById(boardId: String): Maybe<Board> {
        val docRef = db.collection("boards").document(boardId)
        return RxFirestore.getDocument(docRef)
                .map { it.toObject(Board::class.java)?.copy(id = it.id) }
    }

    private fun observeBoardQuery(query: Query): Flowable<List<Board>> {
        return RxFirestore.observeQueryRef(query)
                .map {
                    it.documents.mapNotNull {
                        it.toObject(Board::class.java)?.copy(id = it.id)
                    }
                }
    }

}