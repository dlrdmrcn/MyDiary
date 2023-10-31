package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import com.dilara.mydiary.R
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentChangePasswordBinding
import com.dilara.mydiary.viewmodel.ChangePasswordViewModel
import java.util.regex.Pattern

class ChangePasswordFragment : BaseFragment() {
    private var binding: FragmentChangePasswordBinding? = null
    private val viewModel: ChangePasswordViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }
    private val passwordRegexPattern = Pattern.compile(
        "(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z\\d].{6,}"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.changeButton?.setOnClickListener {
            if (isValid()) {
                changeClicked()
            }
        }
    }

    private fun isValid(): Boolean {
        binding?.currentPassword?.isErrorEnabled = false
        binding?.newPassword?.isErrorEnabled = false
        binding?.newPasswordRepeat?.isErrorEnabled = false

        if (!passwordRegexPattern.matcher(binding?.newPasswordText?.text.toString()).matches()) {
            binding?.newPassword?.error = getString(R.string.signup_fragment_password_warning)
            binding?.newPassword?.isErrorEnabled = true
            return false
        }
        if (binding?.newPasswordText?.text.toString() != binding?.newPasswordRepeatText?.text.toString()) {
            binding?.newPasswordRepeat?.error = getString(R.string.signup_fragment_repeat_warning)
            binding?.newPasswordRepeat?.isErrorEnabled = true
            return false
        }
        return true
    }

    private fun changeClicked() {
        val currentPassword = binding?.currentPasswordText?.text.toString()
        val newPassword = binding?.newPasswordText?.text.toString()
        val repeatPassword = binding?.newPasswordRepeatText?.text.toString()

        if (currentPassword == "" || newPassword == "" || repeatPassword == "") {
            binding?.currentPassword?.isErrorEnabled = true
            binding?.newPassword?.isErrorEnabled = true
            binding?.newPasswordRepeat?.isErrorEnabled = true
        } else {
            viewModel.changePassword(currentPassword, newPassword, onSuccess = {
                (activity as HomeActivity).showPopUp(
                    getString(R.string.app_name),
                    getString(R.string.your_password_has_been_changed),
                    getString(R.string.ok),
                    positiveButtonCallBack = {
                        requireActivity().supportFragmentManager.popBackStack()
                    })
            }, onFailure = {
                (activity as HomeActivity).showPopUp(
                    getString(R.string.app_name),
                    getString(R.string.try_again),
                    getString(R.string.ok)
                )
            },
                onFailureOfAuth = {
                    (activity as HomeActivity).showPopUp(
                        getString(R.string.app_name),
                        getString(R.string.authentication_failed),
                        getString(R.string.ok)
                    )

                })
        }
    }
}