package io.mindhouse.idee.ui.board

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Board
import io.mindhouse.idee.ui.base.MvvmFragment
import io.mindhouse.idee.utils.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_edit_board.*

/**
 * Created by kmisztal on 28/06/2018.
 *
 * @author Krzysztof Misztal
 */
class EditBoardFragment : MvvmFragment<EditBoardViewState, EditBoardViewModel>() {

    companion object {
        private const val KEY_BOARD = "board_id"

        fun newInstance(board: Board? = null): EditBoardFragment {
            val fragment = EditBoardFragment()
            val args = Bundle()
            args.putParcelable(KEY_BOARD, board)

            fragment.arguments = args
            return fragment
        }
    }

    private val board: Board? by lazy { arguments?.getParcelable(KEY_BOARD) as? Board }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = board?.name ?: getString(R.string.create_board)
        activity?.title = title
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_edit_board, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        boardNameText.setText(board?.name)

        updateView()
        boardNameText.addTextChangedListener(object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateView()
            }
        })
        saveButton.setOnClickListener {
            val board = board
            if (board == null) {
                viewModel.createNewBoard(boardNameText.text.toString())
            } else {
                val toUpdate = board.copy(name = boardNameText.text.toString())
                viewModel.updateBoard(toUpdate)
            }
        }
    }

    override fun render(state: EditBoardViewState) {
        if (state.isLoading) {
            progressBar.visibility = View.VISIBLE
            saveButton.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            updateView()
        }
    }

    //==========================================================================
    // private
    //==========================================================================

    fun updateView() {
        val enabled = !boardNameText.text.isNullOrBlank()
        saveButton.isEnabled = enabled
    }

    //==========================================================================

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[EditBoardViewModel::class.java]
}