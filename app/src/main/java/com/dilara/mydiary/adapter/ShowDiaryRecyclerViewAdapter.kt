package com.dilara.mydiary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.EMOJI
import com.dilara.mydiary.R
import com.dilara.mydiary.databinding.ShowDiaryRecyclerRowBinding
import com.dilara.mydiary.model.Diary
import com.dilara.mydiary.view.ShowDiaryFragmentDirections

class ShowDiaryRecyclerViewAdapter(private val diaryList: List<Diary>, val context: Context) :
    RecyclerView.Adapter<ShowDiaryRecyclerViewAdapter.DiaryHolder>() {

    class DiaryHolder(val binding: ShowDiaryRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryHolder {
        val binding =
            ShowDiaryRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryHolder(binding)
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    override fun onBindViewHolder(holder: DiaryHolder, position: Int) {
        holder.binding.title.text = diaryList[position].title
        holder.binding.content.text = diaryList[position].content

        val emoji = when (diaryList[position].mood.toInt()) {
            EMOJI.VERY_HAPPY.ordinal -> R.drawable.emoji_veryhappy
            EMOJI.HAPPY.ordinal -> R.drawable.emoji_happy
            EMOJI.EXPRESSIONLESS.ordinal -> R.drawable.emoji_expressionless
            EMOJI.SAD.ordinal -> R.drawable.emoji_sad
            EMOJI.CRY.ordinal -> R.drawable.emoji_cry
            EMOJI.ANGRY.ordinal -> R.drawable.emoji_angry
            else -> R.drawable.emoji_cool
        }

        holder.binding.mood.setImageDrawable(context.getDrawable(emoji))

        holder.binding.diaryCardView.setOnClickListener {
            val action = ShowDiaryFragmentDirections.actionShowDiaryFragmentToDiaryDetailsFragment(
                diaryList[position].date,
                diaryList[position].title,
                diaryList[position].content,
                diaryList[position].downloadUrl,
                diaryList[position].mood,
                diaryList[position].id
            )
            Navigation.findNavController(it).navigate(action)
        }

    }
}