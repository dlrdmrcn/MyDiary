package com.dilara.mydiary.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.Navigation
import com.dilara.mydiary.R
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentProfileBinding
import com.dilara.mydiary.viewmodel.ProfileViewModel


class ProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }
    private val avatarList = ArrayList<Int>()
    lateinit var sharedPref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = requireContext().getSharedPreferences("com.dilara.mydiary.view", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        avatarList.add(R.drawable.woman_1)
        avatarList.add(R.drawable.woman_2)
        avatarList.add(R.drawable.woman_3)
        avatarList.add(R.drawable.woman_4)
        avatarList.add(R.drawable.woman_5)
        avatarList.add(R.drawable.man_1)
        avatarList.add(R.drawable.man_2)
        avatarList.add(R.drawable.man_3)
        avatarList.add(R.drawable.man_4)
        avatarList.add(R.drawable.man_5)

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
        binding.selectAvatar.setOnClickListener {
            val imageButton = binding.selectAvatar
            AvatarDialog(requireContext(), avatarList, imageButton).show()
        }
        val defaultAvatar = R.drawable.woman_1
        val avatar = sharedPref.getInt("avatar", defaultAvatar)
        binding.selectAvatar.setImageResource(avatar)
    }
}