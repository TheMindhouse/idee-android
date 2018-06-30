package io.mindhouse.idee.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.account.MyAccountFragment
import io.mindhouse.idee.ui.base.DefaultActivity
import io.mindhouse.idee.ui.idea.IdeaActivity
import io.mindhouse.idee.ui.idea.list.IdeaListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DefaultActivity(), IdeaListFragment.FragmentCallbacks {

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    private lateinit var ideaListFragment: IdeaListFragment
    private var selectedBoard: Board? = null
        set(value) {
            field = value
            ideaListFragment.board = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initNavigation()
        initFragment()
    }

    override fun onIdeaSelected(idea: Idea) {
        val intent = IdeaActivity.newIntent(this, idea.boardId, idea)
        startActivity(intent)
    }

    //==========================================================================
    // private
    //==========================================================================

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
    }
}
