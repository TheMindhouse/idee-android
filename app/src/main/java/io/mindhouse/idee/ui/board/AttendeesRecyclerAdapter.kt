package io.mindhouse.idee.ui.board

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.mindhouse.idee.R
import io.mindhouse.idee.ui.base.recycler.ArrayRecyclerAdapter
import kotlinx.android.synthetic.main.item_user.view.*

/**
 * Created by kmisztal on 03/07/2018.
 *
 * @author Krzysztof Misztal
 */
class AttendeesRecyclerAdapter(
        private val hasAdminRole: Boolean
) : ArrayRecyclerAdapter<BoardAttendee, AttendeesRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attendee = data[position]
        val context = holder.view.context

        if (attendee.avatar != null) {
            Picasso.get().load(attendee.avatar).into(holder.avatar)
        } else {
            holder.avatar.setImageResource(R.drawable.ic_user_default)
        }

        holder.userName.text = attendee.displayName ?: context.getString(R.string.unknown_user)
        holder.email.text = attendee.email
        holder.role.text = attendee.role

        if (!hasAdminRole) {
            holder.removeButton.visibility = View.INVISIBLE
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.avatar
        val userName: TextView = view.userName
        val email: TextView = view.userEmail
        val role: TextView = view.userRole
        val removeButton: ImageButton = view.removeButton

        init {
            removeButton.setOnClickListener {
                onPositionClicked(adapterPosition)
            }
        }
    }
}