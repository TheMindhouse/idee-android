package io.mindhouse.idee.ui.idea

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.DefaultActivity
import io.mindhouse.idee.ui.idea.edit.EditIdeaFragment

class IdeaActivity : DefaultActivity(), EditIdeaFragment.FragmentCallbacks {

    companion object {
        private val KEY_IDEA = "idea"
        private val KEY_BOARD_ID = "board_id"

        fun newIntent(context: Context, boardId: String, idea: Idea? = null): Intent {
            val intent = Intent(context, IdeaActivity::class.java)
            intent.putExtra(KEY_IDEA, idea)
            intent.putExtra(KEY_BOARD_ID, boardId)

            return intent
        }
    }

    private var idea: Idea? = null
    private val boardId: String by lazy { intent.getStringExtra(KEY_BOARD_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea)

        val idea = intent.getParcelableExtra(KEY_IDEA) as? Idea
        this.idea = idea
        showEditFragment(false)
//        if (idea != null) {
//            showIdeaFragment(idea)
//        } else {
//            showEditFragment(false)
//        }

        val title = if (idea == null) getString(R.string.create_idea) else getString(R.string.edit_idea)
        setTitle(title)
    }

    override fun onIdeaSaved(idea: Idea) {
        this.idea = idea
        finish()
    }

    //==========================================================================

    private fun showEditFragment(addToBackStack: Boolean) {
        val fragment = supportFragmentManager.findFragmentById(R.id.container) as? EditIdeaFragment
                ?: EditIdeaFragment.newInstance(boardId, idea)

        replace(fragment, addToBackStack)

        fragment.fragmentCallbacks = this
    }

    private fun showIdeaFragment(idea: Idea) {
        val fragment = supportFragmentManager.findFragmentById(R.id.container) as? IdeaFragment
                ?: IdeaFragment.newInstance(idea)

        replace(fragment, false)
    }

    private fun replace(fragment: Fragment, addToBackStack: Boolean) {
        if (fragment.isAdded) return

        var transaction = supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)

        if (addToBackStack) {
            transaction = transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}
