package io.mindhouse.idee.ui.account

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.ui.base.MvvmFragment
import io.mindhouse.idee.ui.board.BoardActivity
import kotlinx.android.synthetic.main.fragment_my_account.*

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
class MyAccountFragment : MvvmFragment<MyAccountViewState, MyAccountViewModel>() {

    private val adapter = BoardsRecyclerAdapter()

    companion object {
        fun newInstance(): MyAccountFragment = MyAccountFragment()
    }

    private var initialSelected = false
    var onBoardSelectedListener: ((Board) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_my_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lm = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = lm

        adapter.onItemClickedListener = { boardViewState, _ ->
            onBoardSelectedListener?.invoke(boardViewState.board)
        }

        addBoardButton.setOnClickListener {
            val intent = BoardActivity.newIntent(view.context)
            startActivity(intent)
        }

        moreButton.setOnClickListener {
            showOverflowMenu(it)
        }
    }

    override fun render(state: MyAccountViewState) {
        userName.text = state.me?.name ?: getString(R.string.user_no_name)
        if (state.me?.avatarUrl != null) {
            Picasso.get().load(state.me.avatarUrl).into(avatar)
        }

        adapter.setItems(state.boards)
        if (!initialSelected && state.boards.isNotEmpty()) {
            initialSelected = true
            onBoardSelectedListener?.invoke(state.boards[0].board)
        }
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun showOverflowMenu(moreButton: View) {
        val context = context ?: return

        val menu = PopupMenu(context, moreButton, Gravity.LEFT)
        menu.inflate(R.menu.menu_my_account)
        menu.show()
    }

    //==========================================================================

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[MyAccountViewModel::class.java]

}