package com.dilara.mydiary.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginOptionsViewModel @Inject constructor() : ViewModel() {

    private var auth: FirebaseAuth = Firebase.auth
    private val currentUser = auth.currentUser

    fun activeUser () : Boolean {
        if (currentUser == null) {
            return false
        }
        return true
    }
}