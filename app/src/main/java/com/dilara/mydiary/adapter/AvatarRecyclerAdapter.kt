package com.dilara.mydiary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.databinding.AvatarRecyclerRowBinding

class AvatarRecyclerAdapter(
    var avatarList: ArrayList<Int>,
    var context: Context,
    private val listener: Listener,
) :
    RecyclerView.Adapter<AvatarRecyclerAdapter.AvatarHolder>() {

    interface Listener {
        fun onAvatarClick(position: Int)
    }

    class AvatarHolder(val binding: AvatarRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(position: Int, listener: Listener) {
            itemView.setOnClickListener {
                listener.onAvatarClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarHolder {
        val binding =
            AvatarRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AvatarHolder(binding)
    }

    override fun getItemCount(): Int {
        return avatarList.size
    }

    override fun onBindViewHolder(holder: AvatarHolder, position: Int) {
        holder.binding.avatar.setImageResource(avatarList[position])
        holder.bindItems(avatarList[position], listener)
    }
}