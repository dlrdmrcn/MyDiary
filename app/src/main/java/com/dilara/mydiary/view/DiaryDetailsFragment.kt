package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.dilara.mydiary.EMOJI
import com.dilara.mydiary.R
import com.dilara.mydiary.databinding.FragmentDiaryDetailsBinding
import com.squareup.picasso.Picasso

class DiaryDetailsFragment : Fragment() {
    private var binding: FragmentDiaryDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: DiaryDetailsFragmentArgs by navArgs()
        val title = safeArgs.title
        val date = safeArgs.date
        val content = safeArgs.content
        val mood = safeArgs.mood
        val downloadUrl = safeArgs.downloadUrl
        val id = safeArgs.id

        val emoji = when (mood.toInt()) {
            EMOJI.VERY_HAPPY.ordinal -> R.drawable.emoji_veryhappy
            EMOJI.HAPPY.ordinal -> R.drawable.emoji_happy
            EMOJI.EXPRESSIONLESS.ordinal -> R.drawable.emoji_expressionless
            EMOJI.SAD.ordinal -> R.drawable.emoji_sad
            EMOJI.CRY.ordinal -> R.drawable.emoji_cry
            EMOJI.ANGRY.ordinal -> R.drawable.emoji_angry
            else -> R.drawable.emoji_cool
        }

        Picasso
            .get()
            .load(downloadUrl)
            .into(binding?.selecedPicture)

        binding?.date?.text = date
        binding?.title?.text = title
        binding?.content?.text = content
        binding?.mood?.setImageDrawable(requireActivity().getDrawable(emoji))
        binding?.edit?.setOnClickListener {
            val action =
                DiaryDetailsFragmentDirections.actionDiaryDetailsFragmentToAddDiaryFragment(
                    date,
                    title,
                    content,
                    downloadUrl,
                    mood,
                    id,
                    true
                )
            Navigation.findNavController(view).navigate(action)
        }
    }
}