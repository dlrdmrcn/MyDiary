package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import com.dilara.mydiary.R
import com.dilara.mydiary.adapter.HomeRecyclerAdapter
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentHomeBinding
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
        viewModel.getData(firestoreError = {
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
            homeAdapter = HomeRecyclerAdapter(it, requireActivity(), requireContext(), this)
            binding?.myDiariesRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
            binding?.myDiariesRecyclerView?.adapter = homeAdapter
            binding?.totalNumberDiary?.text = viewModel.diaryLiveData.value?.size.toString()

            homeAdapter.notifyDataSetChanged()
        }
    }

    override fun onDeleteClick(id: String) {
        viewModel.deleteDiary(id, onFailure = {
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
}