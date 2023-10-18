package com.dilara.mydiary.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.dilara.mydiary.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddDiaryViewModel @Inject constructor() : BaseViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private var firestore: FirebaseFirestore = Firebase.firestore
    private var storage: FirebaseStorage = Firebase.storage
    var popUpLiveData = MutableLiveData<Boolean>()

    fun upload(
        date: String,
        title: String,
        content: String,
        mood: Int,
        selectedPicture: Uri? = null,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if (auth.currentUser != null) {
            val postMap = hashMapOf<String, Any>()
            postMap["title"] = title
            postMap["content"] = content
            postMap["mood"] = mood
            postMap["date"] = date

            if (selectedPicture != null) {
                imageReference.putFile(selectedPicture).addOnSuccessListener {
                    val uploadPictureReference = storage.reference.child("images").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        postMap["downloadUrl"] = downloadUrl
                        addDiaryToFirebase(postMap, onSuccess, onFailure)
                    }
                }.addOnFailureListener {
                    popUpLiveData.value = !(popUpLiveData.value ?: false)
                }
            } else {
                addDiaryToFirebase(postMap, onSuccess, onFailure)
            }
        }
    }

    private fun addDiaryToFirebase(
        postMap: HashMap<String, Any>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        firestore.collection("DiaryList").add(postMap).addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure.invoke()
        }
    }

}