package io.mindhouse.idee.ui.account

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.mindhouse.idee.R
import io.mindhouse.idee.ui.base.recycler.ArrayRecyclerAdapter
import kotlinx.android.synthetic.main.item_board.view.*

/**
 * Created by kmisztal on 24/06/2018.
 *
 * @author Krzysztof Misztal
 */
class BoardsRecyclerAdapter : ArrayRecyclerAdapter<BoardViewState, BoardsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board = data[position].board
        holder.boardName.text = board.name
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val boardName: TextView = view.boardName

        init {
            view.content.setOnClickListener {
                onPositionClicked(adapterPosition)
            }
        }
    }
}