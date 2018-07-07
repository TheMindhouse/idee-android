package io.mindhouse.idee.ui.idea.list

import io.mindhouse.idee.data.model.Idea
import java.util.*

/**
 * Created by kmisztal on 07/07/2018.
 *
 * @author Krzysztof Misztal
 */
data class IdeaComparator(
        val mode: Mode = Mode.AVERAGE,
        val ascending: Boolean = false
) : Comparator<Idea> {

    enum class Mode { AVERAGE, EASE, CONFIDENCE, IMPACT }

    private val sign = if (ascending) 1 else -1

    override fun compare(idea1: Idea?, idea2: Idea?): Int {
        if (idea1 == null || idea2 == null) {
            throw NullPointerException("Cannot compare null ideas!")
        }

        return sign * when (mode) {
            Mode.AVERAGE -> Math.signum(idea1.average - idea2.average).toInt()
            Mode.EASE -> idea1.ease - idea2.ease
            Mode.CONFIDENCE -> idea1.confidence - idea2.confidence
            Mode.IMPACT -> idea1.impact - idea2.impact
        }
    }
}