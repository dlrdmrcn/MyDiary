package com.dilara.mydiary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.databinding.ItemHomeRecyclerBinding
import com.dilara.mydiary.model.Diary

class HomeRecyclerAdapter(private val diaryList: ArrayList<Diary>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.DiaryHolder>() {
    class DiaryHolder(val binding: ItemHomeRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryHolder {
        val binding =
            ItemHomeRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryHolder(binding)
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    override fun onBindViewHolder(holder: DiaryHolder, position: Int) {
        holder.binding.dateText.text = diaryList[position].diaryDate
    }
}