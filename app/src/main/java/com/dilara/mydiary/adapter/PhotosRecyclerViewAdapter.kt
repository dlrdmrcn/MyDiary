package com.dilara.mydiary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.databinding.PhotosRecyclerRowBinding
import com.dilara.mydiary.model.Diary
import com.squareup.picasso.Picasso

class PhotosRecyclerViewAdapter(private val diaryList: ArrayList<Diary>) :
    RecyclerView.Adapter<PhotosRecyclerViewAdapter.PhotosHolder>() {

    class PhotosHolder(val binding: PhotosRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosHolder {
        val binding =
            PhotosRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotosHolder(binding)
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    override fun onBindViewHolder(holder: PhotosHolder, position: Int) {
        Picasso
            .get()
            .load(diaryList[position].downloadUrl)
            .into(holder.binding.myPhoto)
    }
}