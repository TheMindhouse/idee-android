package io.mindhouse.idee.ui.idea

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.DefaultActivity

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

    private val idea: Idea? by lazy { intent.getParcelableExtra(KEY_IDEA) as? Idea }
    private val boardId: String by lazy { intent.getStringExtra(KEY_BOARD_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea)
        initFragment()

        val title = if (idea == null) getString(R.string.create_idea) else getString(R.string.edit_idea)
        setTitle(title)
    }

    override fun onIdeaSaved() {
        finish()
    }

    //==========================================================================

    private fun initFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container) as? EditIdeaFragment
                ?: EditIdeaFragment.newInstance(boardId, idea)

        if (!fragment.isAdded) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit()
        }

        fragment.fragmentCallbacks = this
    }
}
