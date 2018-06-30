package io.mindhouse.idee.ui.idea.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_idea, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ideaName.text = data[position].name
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ideaName: TextView = view.ideaName

        init {
            view.setOnClickListener {
                onPositionClicked(adapterPosition)
            }
        }
    }
}