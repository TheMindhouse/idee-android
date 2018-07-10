package io.mindhouse.idee.ui.idea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.DefaultFragment
import kotlinx.android.synthetic.main.fragment_idea.view.*

/**
 * Created by kmisztal on 10/07/2018.
 *
 * @author Krzysztof Misztal
 */
class IdeaFragment : DefaultFragment() {

    companion object {
        private const val KEY_IDEA = "idea"

        fun newInstance(idea: Idea): IdeaFragment {
            val fragment = IdeaFragment()
            val args = Bundle()

            args.putParcelable(KEY_IDEA, idea)
            fragment.arguments = args

            return fragment
        }
    }

    private val idea: Idea by lazy { arguments?.getParcelable(KEY_IDEA) as Idea }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_idea, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.ideaName.text = idea.name
        view.description.text = idea.description
        view.easeValue.text = idea.ease.toString()
        view.confidenceValue.text = idea.confidence.toString()
        view.impactValue.text = idea.impact.toString()
        view.totalScore.text = Math.round(idea.average).toString()

        val easePhrases = resources.getStringArray(R.array.ease_description)
        val confidencePhrases = resources.getStringArray(R.array.confidence_description)
        val impactPhrases = resources.getStringArray(R.array.impact_description)

        view.easeDescription.text = easePhrases[idea.ease]
        view.confidenceDescription.text = confidencePhrases[idea.confidence]
        view.impactDescription.text = impactPhrases[idea.impact]
    }

}