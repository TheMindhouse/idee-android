package io.mindhouse.idee.ui.idea

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.transition.TransitionManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.MvvmFragment
import io.mindhouse.idee.utils.SimpleOnSeekbarChangeListner
import io.mindhouse.idee.utils.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_edit_idea.*

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class EditIdeaFragment : MvvmFragment<EditIdeaViewState, EditIdeaViewModel>() {

    companion object {
        private const val KEY_IDEA = "idea"
        private const val KEY_BOARD_ID = "board_id"

        fun newInstance(boardId: String, idea: Idea? = null): EditIdeaFragment {
            val fragment = EditIdeaFragment()
            val args = Bundle()

            args.putParcelable(KEY_IDEA, idea)
            args.putString(KEY_BOARD_ID, boardId)
            fragment.arguments = args

            return fragment
        }
    }

    var fragmentCallbacks: FragmentCallbacks? = null
    private val idea: Idea? by lazy { arguments?.getParcelable(KEY_IDEA) as? Idea }
    private val boardId: String by lazy {
        arguments?.getString(KEY_BOARD_ID)
                ?: throw IllegalStateException("Fragment was not initialized with boardId!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_edit_idea, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        validateInput()
    }

    override fun render(state: EditIdeaViewState) {
        TransitionManager.beginDelayedTransition(view as ViewGroup)
        if (state.isLoading) {
            saveButton.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        } else {
            saveButton.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
        }

        if (state.isSaved) {
            fragmentCallbacks?.onIdeaSaved()
        }
    }

    //==========================================================================
    // private
    //==========================================================================

    private fun saveChanges(idea: Idea?) {
        val newIdea = Idea(idea?.id ?: "", boardId, ideaName.text.toString(),
                ideaDescription.text.toString(), ease.progress, confidence.progress, impact.progress)

        if (idea == null) {
            viewModel.createIdea(newIdea)
        } else {
            viewModel.updateIdea(newIdea)
        }
    }

    private fun validateInput() {
        saveButton.isEnabled = !ideaName.text.isNullOrBlank()
    }

    private fun initViews() {
        val idea = idea
        if (idea != null) {
            ideaName.setText(idea.name)
            ideaDescription.setText(idea.description)

            ease.progress = idea.ease
            impact.progress = idea.impact
            confidence.progress = idea.confidence
        }

        ease.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListner() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                easeValue.text = progress.toString()
            }
        })
        impact.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListner() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                impactValue.text = progress.toString()
            }
        })
        confidence.setOnSeekBarChangeListener(object : SimpleOnSeekbarChangeListner() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                confidenceValue.text = progress.toString()
            }
        })

        easeValue.text = ease.progress.toString()
        impactValue.text = impact.progress.toString()
        confidenceValue.text = confidence.progress.toString()

        ideaName.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                validateInput()
            }
        })

        saveButton.setOnClickListener {
            saveChanges(idea)
        }
    }

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[EditIdeaViewModel::class.java]

    interface FragmentCallbacks {
        fun onIdeaSaved()
    }
}