package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment() {
    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.changePassword?.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.myPhotos?.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToPhotosFragment()
            Navigation.findNavController(it).navigate(action)

        }
    }
}