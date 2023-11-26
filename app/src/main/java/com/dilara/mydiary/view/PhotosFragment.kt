package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.dilara.mydiary.R
import com.dilara.mydiary.adapter.PhotosRecyclerViewAdapter
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentPhotosBinding
import com.dilara.mydiary.viewmodel.HomeViewModel

class PhotosFragment : BaseFragment() {
    private var binding: FragmentPhotosBinding? = null
    private val viewModel: HomeViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }
    private lateinit var photosAdapter: PhotosRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)
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
            val safeArgs: PhotosFragmentArgs by navArgs()
            val diaryListWithPhoto = it.filter { !it.downloadUrl.isNullOrEmpty() }
            photosAdapter = PhotosRecyclerViewAdapter(diaryListWithPhoto, safeArgs.activeFirebaseUser)
            binding?.myPhotosRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 3)
            binding?.myPhotosRecyclerView?.adapter = photosAdapter

            photosAdapter.notifyDataSetChanged()
        }
    }

}