package io.mindhouse.idee.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import io.mindhouse.idee.R
import io.mindhouse.idee.data.BoardsRepository
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.account.MyAccountFragment
import io.mindhouse.idee.ui.base.DefaultActivity
import io.mindhouse.idee.ui.board.BoardActivity
import io.mindhouse.idee.ui.idea.IdeaActivity
import io.mindhouse.idee.ui.idea.list.IdeaListFragment
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : DefaultActivity(), IdeaListFragment.FragmentCallbacks {

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    @Inject
    lateinit var boardsRepository: BoardsRepository

    private lateinit var ideaListFragment: IdeaListFragment
    private var selectedBoard: Board? = null
        set(value) {
            field = value
            ideaListFragment.board = value
            adjustMenu()
        }

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initNavigation()
        initFragment()
        adjustMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_board, menu)
        this.menu = menu
        adjustMenu()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onIdeaSelected(idea: Idea) {
        val intent = IdeaActivity.newIntent(this, idea.boardId, idea)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionBoardOptions -> {
                val intent = BoardActivity.newIntent(this, selectedBoard)
                startActivity(intent)
                true
            }
            R.id.actionBoardDelete -> {
                deleteSelectedBoard()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    //==========================================================================
    // private
    //==========================================================================

    private fun deleteSelectedBoard() {
        //we don't save disposable -> Firestore doesn't inform about success if no internet
        val board = selectedBoard
        if (board != null) {
            boardsRepository.delete(board)
                    .subscribeBy(
                            onComplete = { Timber.d("Deleted board: $board") },
                            onError = { Timber.e(it, "Error deleting board!!") }
                    )
        }
    }

    private fun adjustMenu() {
        val me = authorizeRepository.currentUser
        val deleteItem = menu?.findItem(R.id.actionBoardDelete)
        deleteItem?.isVisible = !(me != null && selectedBoard?.roleOf(me) != Board.Companion.Role.OWNER)
    }

    private fun initFragment() {
        ideaListFragment = supportFragmentManager.findFragmentById(R.id.container) as? IdeaListFragment
                ?: IdeaListFragment.newInstance(selectedBoard)

        if (!ideaListFragment.isAdded) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, ideaListFragment)
                    .commit()
        }

        ideaListFragment.fragmentCallbacks = this
    }

    private fun initNavigation() {
        val fragment = supportFragmentManager.findFragmentById(R.id.navContainer) as? MyAccountFragment
                ?: MyAccountFragment.newInstance()

        if (!fragment.isAdded) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.navContainer, fragment)
                    .commit()
        }

        fragment.onBoardSelectedListener = { board ->
            selectedBoard = board
            drawerLayout.closeDrawers()
        }

        toolbar.setNavigationIcon(R.drawable.ic_hamburger)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(Gravity.START) }
    }
}
