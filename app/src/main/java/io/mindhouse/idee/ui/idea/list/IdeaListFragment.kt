package io.mindhouse.idee.ui.idea.list

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.MvvmFragment
import io.mindhouse.idee.ui.board.BoardActivity
import io.mindhouse.idee.ui.idea.IdeaActivity
import io.mindhouse.idee.ui.utils.SwipeOutRecyclerCallback
import kotlinx.android.synthetic.main.fragment_idea_list.*

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class IdeaListFragment : MvvmFragment<IdeaListViewState, IdeaListViewModel>() {

    companion object {
        private const val KEY_BOARD = "board"
        private const val UNDO_DURATION_MS = 2500

        fun newInstance(board: Board? = null): IdeaListFragment {
            val fragment = IdeaListFragment()
            val args = Bundle()
            args.putParcelable(KEY_BOARD, board)

            fragment.arguments = args
            return fragment
        }
    }

    private var menu: Menu? = null
    private val adapter = IdeaRecyclerAdapter()
    var board: Board? = null
        set(value) {
            if (field != value) {
                field = value
                viewModel.board = board
            }
        }

    var fragmentCallbacks: FragmentCallbacks? = null

    //==========================================================================

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel.board = board
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_idea_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_board, menu)
        this.menu = menu
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val context = context ?: return false

        return when (item.itemId) {
            R.id.actionBoardOptions -> {
                val intent = BoardActivity.newIntent(context, board)
                startActivity(intent)
                true
            }
            R.id.actionBoardDelete -> {
                showConfirmationDialog(message = R.string.warning_board_deletion,
                        action = Runnable { viewModel.deleteBoard() })
                true
            }
            R.id.actionBoardLeave -> {
                showConfirmationDialog(message = R.string.warning_board_leave,
                        action = Runnable { viewModel.leaveBoard() })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun render(state: IdeaListViewState) {
        val context = context ?: return
        val name = state.board?.name ?: getString(R.string.app_name)
        activity?.title = name
        adapter.setItems(state.ideas)

        shareStatus.setText(state.shareStatus)
        val color = if (state.shareStatus == R.string.not_shared) {
            shareStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_share_gray, 0)
            ContextCompat.getColor(context, R.color.gray)
        } else {
            shareStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_share, 0)
            ContextCompat.getColor(context, R.color.blue)
        }

        shareStatus.setTextColor(color)

        if (state.isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }

        if (state.board == null) {
            addIdeaButton.visibility = View.GONE
        } else {
            addIdeaButton.visibility = View.VISIBLE
        }

        setHasOptionsMenu(state.role != null)
        menu?.findItem(R.id.actionBoardDelete)?.isVisible = state.role == Board.Companion.Role.OWNER
        menu?.findItem(R.id.actionBoardLeave)?.isVisible = state.role != Board.Companion.Role.OWNER
        menu?.findItem(R.id.actionBoardOptions)?.isVisible =
                state.role in arrayOf(Board.Companion.Role.OWNER, Board.Companion.Role.ADMIN)
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun showSortDialog() {
        val context = context ?: return
        AlertDialog.Builder(context)
                .setTitle(R.string.sort_by)
                .setItems(R.array.sorting_options) { _, which ->
                    onSortingSelected(which)
                }
                .show()
    }

    private fun onSortingSelected(which: Int) {
        val sorting = when (which) {
            0 -> IdeaComparator.Mode.AVERAGE
            1 -> IdeaComparator.Mode.EASE
            2 -> IdeaComparator.Mode.CONFIDENCE
            3 -> IdeaComparator.Mode.IMPACT
            else -> throw IllegalArgumentException("Wrong sorting index: $which")
        }

        val comparator = adapter.comparator.copy(mode = sorting)
        adapter.comparator = comparator
    }

    private fun initViews(view: View) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        configureSwipeToDelete(recyclerView)

        val decoratorDrawable = ContextCompat.getDrawable(view.context, R.drawable.separator_idea_list)
        if (decoratorDrawable != null) {
            val decorator = DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL)
            decorator.setDrawable(decoratorDrawable)
            recyclerView.addItemDecoration(decorator)
        }

        adapter.onItemClickedListener = { idea, _ ->
            fragmentCallbacks?.onIdeaSelected(idea)
        }

        addIdeaButton.setOnClickListener {
            val boardId = board?.id
            if (boardId != null) {
                val intent = IdeaActivity.newIntent(it.context, boardId)
                startActivity(intent)
            }
        }

        sortOrderButton.setOnClickListener {
            val ascending = !adapter.comparator.ascending
            adapter.comparator = adapter.comparator.copy(ascending = ascending)
        }

        sortButton.setOnClickListener {
            showSortDialog()
        }
    }

    private fun configureSwipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = SwipeOutRecyclerCallback()
        val helper = ItemTouchHelper(swipeCallback)
        helper.attachToRecyclerView(recyclerView)

        swipeCallback.onSwipedOut = { position ->
            scheduleIdeaDeletion(adapter.data[position])
        }
    }

    private fun scheduleIdeaDeletion(idea: Idea) {
        viewModel.scheduleDeletion(idea, UNDO_DURATION_MS.toLong())
        view?.let {
            Snackbar.make(it, R.string.idea_deleted, UNDO_DURATION_MS)
                    .setAction(R.string.undo) { viewModel.cancelScheduledDeletion() }
                    .show()
        }
    }

    private fun showConfirmationDialog(title: Int = R.string.irreversible_action_title,
                                       message: Int = R.string.irreversible_action_message,
                                       action: Runnable) {
        context?.let { context ->
            AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        action.run()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    //==========================================================================

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[IdeaListViewModel::class.java]

    interface FragmentCallbacks {
        fun onIdeaSelected(idea: Idea)
    }
}

