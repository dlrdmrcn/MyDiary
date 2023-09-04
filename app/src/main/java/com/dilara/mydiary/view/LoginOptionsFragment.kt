package com.dilara.mydiary.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dilara.mydiary.databinding.FragmentLoginOptionsBinding


class LoginOptionsFragment : Fragment() {

    private var binding: FragmentLoginOptionsBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginOptionsBinding.inflate(inflater, container, false)
        val view = binding?.root
        return view
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
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

}