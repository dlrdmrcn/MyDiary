package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.dilara.mydiary.enums.EMOJI
import com.dilara.mydiary.R
import com.dilara.mydiary.adapter.HomeRecyclerAdapter
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentHomeBinding
import com.dilara.mydiary.model.Diary
import com.dilara.mydiary.viewmodel.HomeViewModel

class HomeFragment : BaseFragment(), HomeRecyclerAdapter.Listener {

    private val viewModel: HomeViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }

    private var binding: FragmentHomeBinding? = null
    private lateinit var homeAdapter: HomeRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewModel.getData(requireContext(), firestoreError = {
            (activity as? HomeActivity)?.showPopUp(
                getString(R.string.warning),
                getString(R.string.try_again),
                getString(R.string.ok),
                positiveButtonCallBack = {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            )
        })

    }

    private fun initObservers() {
        viewModel.diaryLiveData.observe(requireActivity()) {
            val emojiCountList = ArrayList<Int>()
            homeAdapter = HomeRecyclerAdapter(it, requireActivity(), requireContext(), this)
            binding?.myDiariesRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
            binding?.myDiariesRecyclerView?.adapter = homeAdapter
            binding?.totalNumberDiary?.text = viewModel.diaryLiveData.value?.size.toString()
            EMOJI.values().forEach { emoji ->
                val count = viewModel.diaryLiveData.value?.filter {
                    it.mood.toInt() == emoji.ordinal
                }?.size
                emojiCountList.add(count ?: 0)
            }
            binding?.numberMoodVeryhappy?.text = emojiCountList[0].toString()
            binding?.numberMoodHappy?.text = emojiCountList[1].toString()
            binding?.numberMoodExpressionless?.text = emojiCountList[2].toString()
            binding?.numberMoodSad?.text = emojiCountList[3].toString()
            binding?.numberMoodCry?.text = emojiCountList[4].toString()
            binding?.numberMoodAngry?.text = emojiCountList[5].toString()
            binding?.numberMoodCool?.text = emojiCountList[6].toString()

            homeAdapter.notifyDataSetChanged()
        }
    }

    override fun onDeleteClick(diary: Diary) {
        viewModel.deleteDiary(diary, onFailure = {
            (activity as? HomeActivity)?.showPopUp(
                getString(R.string.warning),
                getString(R.string.try_again),
                getString(R.string.ok),
                positiveButtonCallBack = {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            )
        }, requireContext())
    }
}