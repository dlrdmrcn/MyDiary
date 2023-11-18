package com.dilara.mydiary.viewmodel

import com.dilara.mydiary.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import javax.inject.Inject

class PhotoDetailViewModel @Inject constructor() : BaseViewModel() {
    private var firestore: FirebaseFirestore = Firebase.firestore
    private var storage: FirebaseStorage = Firebase.storage
    private var auth: FirebaseAuth = Firebase.auth

    fun deletePhoto(
        downloadUrl: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val photoRef = storage.getReferenceFromUrl(downloadUrl)
        photoRef.delete().addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure.invoke()
        }
    }

    fun deleteDiary(
        id: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val user = auth.currentUser!!.uid
        val docRef = firestore.collection(user).document("Data").collection("DiaryList").document(id)
        docRef.delete()
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnFailureListener {
                onFailure.invoke()
            }
    }
}