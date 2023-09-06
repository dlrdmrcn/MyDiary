package com.dilara.mydiary.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.Navigation
import com.dilara.mydiary.databinding.FragmentLoginOptionsBinding
import com.dilara.mydiary.viewmodel.LoginOptionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginOptionsFragment : Fragment() {

    private var binding: FragmentLoginOptionsBinding?=null
    private val viewModel: LoginOptionsViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.activeUser()){
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginOptionsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.login?.setOnClickListener {
            val action = LoginOptionsFragmentDirections.actionLoginOptionsFragmentToLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding?.signUp?.setOnClickListener {
            val action = LoginOptionsFragmentDirections.actionLoginOptionsFragmentToSignUpFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding?.continueWithoutLogin?.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }
    }

}