package io.mindhouse.idee.ui.board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.ui.base.DefaultActivity

class BoardActivity : DefaultActivity() {

    companion object {
        private const val KEY_BOARD = "board_id"

        /**
         * @param board - board, if null activity will start in creation mode
         */
        fun newIntent(context: Context, board: Board? = null): Intent {
            val intent = Intent(context, BoardActivity::class.java)
            intent.putExtra(KEY_BOARD, board)

            return intent
        }

    }

    private val board: Board? by lazy { intent.getParcelableExtra(KEY_BOARD) as? Board }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        initFragment()
    }

    private fun initFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
                ?: EditBoardFragment.newInstance(board)

        if (!fragment.isAdded) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit()
        }
    }
}
