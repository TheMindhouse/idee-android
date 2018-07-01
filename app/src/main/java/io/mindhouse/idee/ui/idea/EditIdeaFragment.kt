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
import io.mindhouse.idee.utils.SimpleOnSeekBarChangeListener
import io.mindhouse.idee.utils.SimpleTextWatcher
import kotlinx.android.synthetic.main.fragment_edit_idea.*

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class EditIdeaFragment : MvvmFragment<EditIdeaViewState, EditIdeaViewModel>() {

    companion object {
        private const val SEEK_BAR_FACTOR = 10
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

    private val seekBarListener = SnappingSeekBarListener()
    private val easeArray by lazy { resources.getStringArray(R.array.ease_description) }
    private val confidenceArray by lazy { resources.getStringArray(R.array.confidence_description) }
    private val impactArray by lazy { resources.getStringArray(R.array.impact_description) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_edit_idea, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        updateUI()
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
                ideaDescription.text.toString(), ease.progress / 10,
                confidence.progress / 10, impact.progress / 10)

        if (idea == null) {
            viewModel.createIdea(newIdea)
        } else {
            viewModel.updateIdea(newIdea)
        }
    }

    private fun updateUI() {
        saveButton.isEnabled = !ideaName.text.isNullOrBlank()

        easeValue.text = ease.adjustedProgress().toString()
        impactValue.text = impact.adjustedProgress().toString()
        confidenceValue.text = confidence.adjustedProgress().toString()

        easeDesc.text = easeArray[ease.adjustedProgress()]
        confidenceDesc.text = confidenceArray[confidence.adjustedProgress()]
        impactDesc.text = impactArray[impact.adjustedProgress()]
    }

    private fun initViews() {
        val idea = idea
        if (idea != null) {
            ideaName.setText(idea.name)
            ideaDescription.setText(idea.description)

            ease.progress = idea.ease * SEEK_BAR_FACTOR
            impact.progress = idea.impact * SEEK_BAR_FACTOR
            confidence.progress = idea.confidence * SEEK_BAR_FACTOR
        }

        ease.setOnSeekBarChangeListener(seekBarListener)
        impact.setOnSeekBarChangeListener(seekBarListener)
        confidence.setOnSeekBarChangeListener(seekBarListener)

        ideaName.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                updateUI()
            }
        })

        saveButton.setOnClickListener {
            saveChanges(idea)
        }
    }

    //==========================================================================
    // private
    //==========================================================================

    override fun createViewModel() =
            ViewModelProviders.of(this, viewModelFactory)[EditIdeaViewModel::class.java]

    private fun SeekBar.adjustedProgress() = this.progress / SEEK_BAR_FACTOR

    interface FragmentCallbacks {
        fun onIdeaSaved()
    }

    private inner class SnappingSeekBarListener : SimpleOnSeekBarChangeListener() {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            updateUI()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            //round down, for example: 94 -> 90
            seekBar.progress = seekBar.progress / SEEK_BAR_FACTOR * SEEK_BAR_FACTOR
        }
    }
}