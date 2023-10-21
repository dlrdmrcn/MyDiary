package com.dilara.mydiary.viewmodel

import androidx.lifecycle.MutableLiveData
import com.dilara.mydiary.base.BaseViewModel
import com.dilara.mydiary.model.Diary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class HomeViewModel @Inject constructor() : BaseViewModel() {

    private var firestore: FirebaseFirestore = Firebase.firestore
    var diaryArrayList: ArrayList<Diary>? = null
    val diaryLiveData = MutableLiveData<ArrayList<Diary>>()

    fun getData(firestoreError: () -> Unit) {
        firestore.collection("DiaryList").orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                diaryArrayList = ArrayList()
                if (error != null) {
                    firestoreError.invoke()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            for (document in documents) {
                                val date = document.get("date") as String
                                val content = document.get("content") as String
                                val title = document.get("title") as String
                                val mood = document.get("mood") as Long
                                val downloadUrl = document.get("downloadUrl") as? String
                                val documentId = document.id

                                val diary =
                                    Diary(date, content, title, mood, downloadUrl, documentId)
                                diaryArrayList?.add(diary)
                            }
                        }
                        diaryLiveData.value = diaryArrayList
                    }
                }
            }
    }

    fun deleteDiary(id: String, onFailure: () -> Unit) {
        val docRef = firestore.collection("DiaryList").document(id)
        docRef.delete()
            .addOnSuccessListener {
                getData(onFailure)
            }
            .addOnFailureListener {
                onFailure.invoke()
            }
    }

}