    package com.dilara.mydiary.viewmodel

import com.dilara.mydiary.base.BaseViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class ChangePasswordViewModel @Inject constructor() : BaseViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private val currentUser = auth.currentUser
    private val email = currentUser?.email

    fun changePassword(
        password: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        onFailureOfAuth: () -> Unit
    ) {
        if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(email!!, password)
            currentUser.reauthenticate(credential).addOnCompleteListener {
                currentUser.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            currentUser.updatePassword(newPassword)
                                .addOnCompleteListener { it2 ->
                                    if (!it2.isSuccessful) {
                                        onFailure.invoke()
                                    } else {
                                        onSuccess.invoke()
                                    }
                                }
                        } else {
                            onFailureOfAuth.invoke()
                        }
                    }
            }
        }

    }
}