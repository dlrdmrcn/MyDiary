package com.dilara.mydiary.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.Navigation
import com.dilara.mydiary.R
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentSignUpBinding
import com.dilara.mydiary.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
open class SignUpFragment : BaseFragment() {

    private var binding: FragmentSignUpBinding? = null
    private val viewModel: SignUpViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }

    private val emailRegexPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    private val passwordRegexPattern = Pattern.compile(
        "(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z\\d].{6,}"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.login?.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.signUp?.setOnClickListener {
            if (isValid()) {
                signUpClicked(it)
            }
        }
    }

    private fun isValid(): Boolean {
        binding?.enterEmail?.isErrorEnabled = false
        binding?.enterPassword?.isErrorEnabled = false
        binding?.repeatPassword?.isErrorEnabled = false

        if (!emailRegexPattern.matcher(binding?.enterEmailText?.text.toString()).matches()) {
            binding?.enterEmail?.error = getString(R.string.signup_fragment_email_warning)
            binding?.enterEmail?.isErrorEnabled = true
            return false
        }
        if (!passwordRegexPattern.matcher(binding?.enterPasswordText?.text.toString()).matches()) {
            binding?.enterPassword?.error = getString(R.string.signup_fragment_password_warning)
            binding?.enterPassword?.isErrorEnabled = true
            return false
        }
        if (binding?.enterPasswordText?.text.toString() != binding?.repeatPasswordText?.text.toString()) {
            binding?.repeatPassword?.error = getString(R.string.signup_fragment_repeat_warning)
            binding?.repeatPassword?.isErrorEnabled = true
            return false
        }
        return true
    }

    private fun signUpClicked(view: View) {
        val email = binding?.enterEmailText?.text.toString()
        val password = binding?.enterPasswordText?.text.toString()
        val repeatPassword = binding?.repeatPasswordText?.text.toString()

        if (email == "" || password == "" || repeatPassword == "") {
            binding?.enterEmail?.isErrorEnabled = true
            binding?.enterPassword?.isErrorEnabled = true
            binding?.repeatPassword?.isErrorEnabled = true
        } else {
            viewModel.register(
                email,
                password,
                onSuccess = {
                    val intent = Intent(activity, HomeActivity::class.java)
                    startActivity(intent)
                },
                onFailure = {
                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
                })
        }
    }

}