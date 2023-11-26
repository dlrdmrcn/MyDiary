package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dilara.mydiary.R
import com.dilara.mydiary.adapter.ShowDiaryRecyclerViewAdapter
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentShowDiaryBinding
import com.dilara.mydiary.viewmodel.HomeViewModel

class ShowDiaryFragment : BaseFragment() {
    private var binding: FragmentShowDiaryBinding? = null
    private val viewModel: HomeViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }
    private lateinit var showDiaryAdapter: ShowDiaryRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowDiaryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: ShowDiaryFragmentArgs by navArgs()
        val date = safeArgs.date
        binding?.date?.text = date

        initObservers(date)
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

        binding?.add?.setOnClickListener {
            val action = ShowDiaryFragmentDirections.actionShowDiaryFragmentToAddDiaryFragment(
                date,
                null,
                null,
                null,
                -1,
                null,
                true
            )
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun initObservers(date: String) {
        viewModel.diaryLiveData.observe(requireActivity()) { it ->
            val selectedDiaries = it.filter { it.date == date }
            showDiaryAdapter = ShowDiaryRecyclerViewAdapter(selectedDiaries, requireContext())
            binding?.showDiaryRecyclerview?.layoutManager = LinearLayoutManager(requireContext())
            binding?.showDiaryRecyclerview?.adapter = showDiaryAdapter

            if (selectedDiaries.isEmpty()) {
                binding?.warning?.visibility = View.VISIBLE
            } else {
                binding?.warning?.visibility = View.GONE
            }
            showDiaryAdapter.notifyDataSetChanged()
        }

        binding?.back?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

}