package io.mindhouse.idee.ui.idea.list

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.MvvmFragment
import io.mindhouse.idee.ui.idea.IdeaActivity

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class IdeaListFragment : MvvmFragment<IdeaListViewState, IdeaListViewModel>() {

    companion object {
        private const val KEY_BOARD = "board"

        fun newInstance(board: Board? = null): IdeaListFragment {
            val fragment = IdeaListFragment()
            val args = Bundle()
            args.putParcelable(KEY_BOARD, board)

            fragment.arguments = args
            return fragment
        }
    }

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
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)

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
    }

    override fun render(state: IdeaListViewState) {
        val name = state.board?.name ?: getString(R.string.app_name)
        activity?.title = name
        adapter.setItems(state.ideas)

        if (state.isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }

        if (state.board == null) {
            addIdeaButton.hide()
        } else {
            addIdeaButton.show()
        }
    }

    //==========================================================================

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[IdeaListViewModel::class.java]

    interface FragmentCallbacks {
        fun onIdeaSelected(idea: Idea)
    }
}