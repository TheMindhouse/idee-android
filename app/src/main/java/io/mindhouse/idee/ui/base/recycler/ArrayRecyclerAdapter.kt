package io.mindhouse.idee.ui.base.recycler

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

/**
 * Created by kmisztal on 29/03/2018.
 *
 * @author Krzysztof Misztal
 */
abstract class ArrayRecyclerAdapter<T : Any, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    protected var data: MutableList<T> = ArrayList()

    var onItemClickedListener: ((T, Int) -> Unit)? = null

    fun addAll(items: Collection<T>) {
        val oldSize = data.size
        data.addAll(items)

        notifyItemRangeInserted(oldSize, items.size)
    }

    fun add(item: T) {
        data.add(item)
        notifyItemInserted(data.size - 1)
    }

    fun remove(item: T) {
        val index = data.indexOf(item)
        if (index != -1) {
            data.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    open fun setItems(items: List<T>) {
        val oldData = data
        this.data = ArrayList(items)

        if (oldData.isEmpty()) {
            notifyDataSetChanged()

        } else {

            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldData.size

                override fun getNewListSize(): Int = data.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        areItemsTheSame(oldData[oldItemPosition], data[newItemPosition])

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        areContentsTheSame(oldData[oldItemPosition], data[newItemPosition])

            }, true).dispatchUpdatesTo(this)
        }
    }

    open fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem === newItem) {
            return true
        }

        return oldItem == newItem
    }

    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    override fun getItemCount(): Int = data.size

    /**
     * Implementation should call that when VH is clicked.
     */
    fun onPositionClicked(position: Int) {
        onItemClickedListener?.invoke(data[position], position)
    }
}