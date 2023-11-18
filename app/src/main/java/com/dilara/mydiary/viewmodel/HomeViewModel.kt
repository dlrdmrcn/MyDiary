package com.dilara.mydiary.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.dilara.mydiary.base.BaseViewModel
import com.dilara.mydiary.model.Diary
import com.dilara.mydiary.roomdb.DiaryDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeViewModel @Inject constructor() : BaseViewModel() {

    private var firestore: FirebaseFirestore = Firebase.firestore
    private var auth: FirebaseAuth = Firebase.auth
    var diaryArrayList: ArrayList<Diary>? = null
    val diaryLiveData = MutableLiveData<ArrayList<Diary>>()

    fun getData(context: Context?, firestoreError: () -> Unit?) {
        if (auth.currentUser != null) {
            getDataFromFirebase(firestoreError)
        } else {
            context?.let { getDataFromRoom(it) }
        }
    }

    fun getDataFromRoom(context: Context) {
        val diaryDb = Room.databaseBuilder(context, DiaryDatabase::class.java, "Diaries")
            .fallbackToDestructiveMigration().build()
        val diaryDao = diaryDb.diaryDao()
        CoroutineScope(Dispatchers.IO).launch {
            val flowList = diaryDao.getAll()
//                val diaryArrayList = flowList.first()

            withContext(Dispatchers.Main) {
                diaryLiveData.value = flowList as ArrayList<Diary>
            }
        }


    }

    fun getDataFromFirebase(firestoreError: () -> Unit?) {
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
        if (auth.currentUser != null) {
            val docRef = firestore.collection("DiaryList").document(id)
            docRef.delete()
                .addOnSuccessListener {
                    getDataFromFirebase(onFailure)
                }
                .addOnFailureListener {
                    onFailure.invoke()
                }
        } else {
            //delete diary with room
        }

    }

}
