package com.dilara.mydiary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.R

class EmojiRecyclerViewAdapter(
    var context: Context,
    var list: ArrayList<Int>,
    private val listener: Listener,
    var mood: Int? = null
) :
    RecyclerView.Adapter<EmojiRecyclerViewAdapter.RowHolder>() {

    interface Listener {
        fun onItemClick(resourceId: Int, itemView: View?)
    }


    class RowHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItems(item: Int, listener: Listener, mood: Int? = null, context: Context) {

            itemView.setOnClickListener {
                listener.onItemClick(item, itemView)
                firstSelectedItem?.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.transparent
                    )
                )
                firstSelectedItem = null
            }
            val menuImage = itemView.findViewById<ImageButton>(R.id.emoji)
            menuImage.setImageResource(item)

            if (item == mood) {
                firstSelectedItem = itemView
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.emoji_background
                    )
                )
            }
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
        holder.bindItems(list[position], listener, mood, context)
    }

    companion object {
        var firstSelectedItem: View? = null
    }
}