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
import com.dilara.mydiary.databinding.FragmentLoginBinding
import com.dilara.mydiary.viewmodel.LoginViewModel
import java.util.regex.Pattern


class LoginFragment : BaseFragment() {

    private var binding: FragmentLoginBinding? = null

    private val viewModel: LoginViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }

    val emailRegexPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.back?.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToLoginOptionsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding?.enter?.setOnClickListener {
            if (isValid()) {
                signInClicked()
            }
        }
    }

    private fun isValid(): Boolean {
        binding?.emailLayout?.isErrorEnabled = false
        if (!emailRegexPattern.matcher(binding?.emailText?.text.toString()).matches()) {
            binding?.emailLayout?.error = getString(R.string.login_fragment_email_warning)
            binding?.emailLayout?.isErrorEnabled = true
            return false
        }
        return true
    }

    private fun signInClicked() {
        val email = binding?.emailText?.text.toString()
        val password = binding?.passwordText?.text.toString()
        binding?.emailLayout?.isErrorEnabled = false
        binding?.passwordLayout?.isErrorEnabled = false

        if (email == "" || password == "") {
            binding?.emailLayout?.isErrorEnabled = true
            binding?.passwordLayout?.isErrorEnabled = true
            binding?.passwordLayout?.error = getString(R.string.login_fragment_password_warning)
        } else {
            viewModel.login(
                email,
                password,
                onSuccess = {
                    val intent = Intent(activity, HomeActivity::class.java)
                    activity?.finish()
                    startActivity(intent)
                },
                onFailure = {
                    binding?.emailLayout?.isErrorEnabled = true
                    binding?.passwordLayout?.error =
                        getString(R.string.login_fragment_onfailure_warning)
                    binding?.passwordLayout?.isErrorEnabled = true
                    Toast.makeText(activity, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

}