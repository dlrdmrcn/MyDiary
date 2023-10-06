package com.dilara.mydiary.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dilara.mydiary.R
import com.dilara.mydiary.base.BaseActivity
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentOtherBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OtherFragment : BaseFragment() {
    private var binding: FragmentOtherBinding? = null
    private var auth: FirebaseAuth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.exportText?.setOnClickListener {
            val action = OtherFragmentDirections.actionOtherFragmentToExportFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.importText?.setOnClickListener {
            val action = OtherFragmentDirections.actionOtherFragmentToImportFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.donateText?.setOnClickListener {
            val action = OtherFragmentDirections.actionOtherFragmentToDonateFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.rateText?.setOnClickListener {
            val action = OtherFragmentDirections.actionOtherFragmentToRateFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.shareText?.setOnClickListener {
            val action = OtherFragmentDirections.actionOtherFragmentToShareFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.privacyPolicyText?.setOnClickListener {
            val action = OtherFragmentDirections.actionOtherFragmentToPrivacyPolicyFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding?.logoutText?.setOnClickListener {
            (activity as BaseActivity).showPopUp(
                getString(R.string.app_name),
                getString(R.string.logout_popup_message),
                getString(R.string.logout),
                {
                    auth.signOut()
                    val intent = Intent(this.activity, LoginActivity::class.java)
                    activity?.finish()
                    startActivity(intent)
                },
                getString(R.string.cancel)
            )

        }

    }

}