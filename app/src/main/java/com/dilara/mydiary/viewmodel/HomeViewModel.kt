package com.dilara.mydiary.viewmodel

import androidx.lifecycle.MutableLiveData
import com.dilara.mydiary.base.BaseViewModel
import com.dilara.mydiary.model.Diary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class HomeViewModel @Inject constructor() : BaseViewModel() {

    private var firestore: FirebaseFirestore = Firebase.firestore
    var diaryArrayList: ArrayList<Diary>? = null
    val diaryLiveData = MutableLiveData<ArrayList<Diary>>()

    fun getData(firestoreError: () -> Unit) {
        diaryArrayList = ArrayList()
        firestore.collection("DiaryList").addSnapshotListener { value, error ->
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

                            val diary =
                                Diary(date, content, title, mood, downloadUrl)
                            diaryArrayList?.add(diary)

                        }
                        diaryLiveData.value = diaryArrayList

                    }
                }
            }
        }
    }

}