package io.mindhouse.idee.data

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import durdinapps.rxfirebase2.RxCompletableHandler
import durdinapps.rxfirebase2.RxFirestore
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.functions.Function4
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

        val ownedRef = db.collection("boards")
                .whereEqualTo("ownerId", me.id)
        val adminRef = db.collection("boards")
                .whereEqualTo(FieldPath.of("roles", me.email), ROLE_ADMIN)
        val readerRef = db.collection("boards")
                .whereEqualTo(FieldPath.of("roles", me.email), ROLE_READER)
        val editorRef = db.collection("boards")
                .whereEqualTo(FieldPath.of("roles", me.email), ROLE_EDITOR)

        val flowable: Flowable<List<Board>> =  Flowable.combineLatest(
                observeBoardQuery(ownedRef),
                observeBoardQuery(adminRef),
                observeBoardQuery(readerRef),
                observeBoardQuery(editorRef),

                Function4 { r1, r2, r3, r4 ->
                    r1 + r2 + r3 + r4
                }
        )

        return flowable.map { it.distinct() }

    }

    fun getMyBoards(): Single<List<Board>> {
        val me = authorizeRepository.currentUser
                ?: return Single.error(IllegalArgumentException("Not logged in!"))

        val ownedRef = db.collection("boards").whereEqualTo("ownerId", me.id)
        return RxFirestore.getCollection(ownedRef)
                .map {
                    it.documents.mapNotNull {
                        it.toObject(Board::class.java)?.copy(id = it.id)
                    }
                }
                .toSingle(emptyList())
    }

    fun updateBoard(board: Board): Completable {
        val docRef = db.collection("boards").document(board.id)

        return Completable.create {
            //We override fields
            emitter ->
            RxCompletableHandler.assignOnTask<Void>(emitter, docRef.set(board))
        }
    }

    fun createBoard(board: Board): Single<Board> {
        val me = authorizeRepository.currentUser
                ?: return Single.error(IllegalArgumentException("Not logged in!"))

        val docRef = db.collection("boards")
        val toCreate = board.copy(ownerId = me.id)

        return RxFirestore.addDocument(docRef, toCreate)
                .map { toCreate.copy(id = it.id) }
    }

    fun delete(board: Board): Completable {
        val ref = db.collection("boards").document(board.id)
        return RxFirestore.deleteDocument(ref)
    }

    fun findBoardById(boardId: String): Maybe<Board> {
        val docRef = db.collection("boards").document(boardId)
        return RxFirestore.getDocument(docRef)
                .map { it.toObject(Board::class.java)?.copy(id = it.id) }
    }

    //==========================================================================
    // ideas
    //==========================================================================

    fun observeIdeas(boardId: String): Flowable<List<Idea>> {
        val docRef = db.collection("boards").document(boardId).collection("ideas")
        return RxFirestore.observeQueryRef(docRef)
                .map {
                    it.documents.mapNotNull {
                        it.toObject(Idea::class.java)?.copy(id = it.id, boardId = boardId)
                    }
                }
    }

    fun createIdea(idea: Idea): Single<Idea> {
        val ref = db.collection("boards").document(idea.boardId)
                .collection("ideas")

        return RxFirestore.addDocument(ref, idea)
                .map { idea.copy(id = it.id) }
    }

    fun updateIdea(idea: Idea): Completable {
        val ref = db.collection("boards").document(idea.boardId)
                .collection("ideas").document(idea.id)

        return RxFirestore.setDocument(ref, idea, SetOptions.merge())
    }

    //==========================================================================

    private fun observeBoardQuery(query: Query): Flowable<List<Board>> {
        return RxFirestore.observeQueryRef(query)
                .map {
                    it.documents.mapNotNull {
                        it.toObject(Board::class.java)?.copy(id = it.id)
                    }
                }
    }

}