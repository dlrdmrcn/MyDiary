package com.dilara.mydiary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.R

class EmojiRecyclerViewAdapter(
    var context: Context,
    var list: ArrayList<Int>,
    private val listener: Listener
) :
    RecyclerView.Adapter<EmojiRecyclerViewAdapter.RowHolder>() {
    interface Listener {
        fun onItemClick(resourceId: Int, itemView: View)
    }


    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(item: Int, listener: Listener) {
            itemView.setOnClickListener {
                listener.onItemClick(item, itemView)
            }
            val menuImage = itemView.findViewById<ImageButton>(R.id.emoji)
            menuImage.setImageResource(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.emoji_row_layout, parent, false)
        return RowHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bindItems(list[position], listener)
    }
}