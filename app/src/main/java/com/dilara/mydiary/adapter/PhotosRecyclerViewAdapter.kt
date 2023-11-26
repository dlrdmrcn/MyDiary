package com.dilara.mydiary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.databinding.PhotosRecyclerRowBinding
import com.dilara.mydiary.model.Diary
import com.dilara.mydiary.view.PhotosFragmentDirections
import com.squareup.picasso.Picasso
import java.io.File

class PhotosRecyclerViewAdapter(private val diaryList: List<Diary>, private val user: Boolean) :
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
        if (user) {
            Picasso
                .get()
                .load(diaryList[position].downloadUrl)
                .into(holder.binding.myPhoto)
        } else {
            Picasso
                .get()
                .load(File(diaryList[position].downloadUrl ?: ""))
                .into(holder.binding.myPhoto)
        }

        holder.binding.myPhoto.setOnClickListener {
            val action = PhotosFragmentDirections.actionPhotosFragmentToPhotoDetailFragment(
                diaryList[position].date,
                diaryList[position].title,
                diaryList[position].content,
                diaryList[position].downloadUrl,
                diaryList[position].mood,
                diaryList[position].id,
            )
            Navigation.findNavController(it).navigate(action)
        }
    }
}