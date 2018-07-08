package io.mindhouse.idee.ui.idea.list

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import io.mindhouse.idee.R
import io.mindhouse.idee.data.model.Idea
import io.mindhouse.idee.ui.base.recycler.ArrayRecyclerAdapter
import kotlinx.android.synthetic.main.item_idea.view.*

/**
 * Created by kmisztal on 29/06/2018.
 *
 * @author Krzysztof Misztal
 */
class IdeaRecyclerAdapter : ArrayRecyclerAdapter<Idea, IdeaRecyclerAdapter.ViewHolder>() {

    var comparator = IdeaComparator()
        set(value) {
            if (field == value) return
            field = value
            setItems(data)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_idea, parent, false)

        view.ease.isActivated = true
        view.ease.isEnabled = false
        view.confidence.isActivated = true
        view.confidence.isEnabled = false
        view.impact.isActivated = true
        view.impact.isEnabled = false

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val idea = data[position]
        holder.ideaName.text = idea.name
        holder.ease.progress = idea.ease
        holder.confidence.progress = idea.confidence
        holder.impact.progress = idea.impact
        holder.average.text = Math.round(idea.average).toString()
    }

    override fun setItems(items: List<Idea>) {
        val sorted = items.sortedWith(comparator)
        super.setItems(sorted)
    }

    //==========================================================================

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foreground: ViewGroup = view.foregroundLayout
        val background: ViewGroup = view.backgroundLayout

        val ideaName: TextView = view.ideaName
        val ease: SeekBar = view.ease
        val confidence: SeekBar = view.confidence
        val impact: SeekBar = view.impact
        val average: TextView = view.average

        init {
            foreground.setOnClickListener {
                onPositionClicked(adapterPosition)
            }
        }
    }

}