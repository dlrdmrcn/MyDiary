package com.dilara.mydiary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.R
import com.dilara.mydiary.databinding.HomeRecyclerRowBinding
import com.dilara.mydiary.model.Diary
import com.dilara.mydiary.view.HomeActivity
import com.dilara.mydiary.view.HomeFragmentDirections

class HomeRecyclerAdapter(
    private val diaryList: ArrayList<Diary>,
    var activity: Activity,
    var context: Context,
    val listener: Listener
) :
    RecyclerView.Adapter<HomeRecyclerAdapter.DiaryHolder>() {
    interface Listener {
        fun onDeleteClick(diary: Diary)
    }

    class DiaryHolder(val binding: HomeRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryHolder {
        val binding =
            HomeRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryHolder(binding)
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    override fun onBindViewHolder(holder: DiaryHolder, position: Int) {
        holder.binding.dateText.text = diaryList[position].date
        holder.binding.dateText.setOnClickListener { view ->
            val action = HomeFragmentDirections.actionHomeFragmentToDiaryDetailsFragment(
                diaryList[position].date,
                diaryList[position].title,
                diaryList[position].content,
                diaryList[position].downloadUrl,
                diaryList[position].mood,
                diaryList[position].id,
                roomId = diaryList[position].roomId
            )
            Navigation.findNavController(view).navigate(action)
        }
        holder.binding.deleteDiary.setOnClickListener {
            (activity as? HomeActivity)?.showPopUp(
                context.getString(R.string.warning),
                context.getString(R.string.delete_diary_message),
                context.getString(R.string.delete),
                positiveButtonCallBack = {
                    listener.onDeleteClick(diaryList[position])
                },
                context.getString(R.string.cancel)
            )
        }
    }
}