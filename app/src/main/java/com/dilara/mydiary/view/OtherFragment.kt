package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentOtherBinding

class OtherFragment : BaseFragment() {
    private var binding: FragmentOtherBinding? = null

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

        }

    }

}