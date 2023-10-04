package com.dilara.mydiary.viewmodel

import com.dilara.mydiary.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel() {

    private var auth: FirebaseAuth = Firebase.auth

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (e: Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure.invoke(it)
        }
    }
}