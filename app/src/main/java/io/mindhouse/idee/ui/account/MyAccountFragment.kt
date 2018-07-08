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

    var selected: Board? = null
        private set(value) {
            if (value != field) {
                field = value
                onBoardSelectedListener?.invoke(field)
            }
        }
    var onBoardSelectedListener: ((Board?) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_my_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lm = LinearLayoutManager(view.context)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = lm

        adapter.onItemClickedListener = { boardViewState, _ ->
            selected = boardViewState.board
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
        email.text = state.me?.email ?: ""
        if (state.me?.avatarUrl != null) {
            Picasso.get().load(state.me.avatarUrl).into(avatar)
        }

        adapter.setItems(state.boards)
        reselectBoard(state)
    }

    private fun reselectBoard(state: MyAccountViewState) {
        //We have 4 scenarios:
        //nothing was selected and we select first board
        //board was selected but got updated
        //board was selected but got deleted
        //board was selected and nothing changed

        val selected = selected

        if (selected == null) {
            this.selected = state.boards.firstOrNull()?.board

        } else {
            val matching = state.boards.find { it.board.id == selected.id }
            if (matching == null) {
                //it means that for some reason user lost access to the selected board
                this.selected = state.boards.firstOrNull()?.board

            } else if (matching.board != selected) {
                //it means selector board has been updated
                this.selected = matching.board
            }
            //else -> something changed, but selection remains the same
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

        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.actionLogOut -> {
                    viewModel.logOut()
                    true
                }
                else -> false
            }
        }
    }

    //==========================================================================

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[MyAccountViewModel::class.java]

}