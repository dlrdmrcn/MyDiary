package com.dilara.mydiary.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.Navigation
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentProfileBinding
import com.dilara.mydiary.viewmodel.ProfileViewModel


class ProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.myPhotos.setOnClickListener {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToPhotosFragment(viewModel.activeFirebaseUser())
            Navigation.findNavController(it).navigate(action)
        }
        if (viewModel.activeFirebaseUser()) {
            val email = viewModel.email()
            if (!email.isNullOrEmpty()) {
                binding.emailAddress.text = email
            } else {
                binding.emailAddress.visibility = View.GONE
            }
            binding.login.visibility = View.GONE
            binding.changePassword.setOnClickListener {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToChangePasswordFragment()
                Navigation.findNavController(it).navigate(action)
            }
        } else {

            binding.changePassword.visibility = View.GONE
            binding.login.setOnClickListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}