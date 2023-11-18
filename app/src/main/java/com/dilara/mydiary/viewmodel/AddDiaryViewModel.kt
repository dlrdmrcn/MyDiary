package com.dilara.mydiary.viewmodel

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.dilara.mydiary.base.BaseViewModel
import com.dilara.mydiary.model.Diary
import com.dilara.mydiary.roomdb.DiaryDao
import com.dilara.mydiary.roomdb.DiaryDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddDiaryViewModel @Inject constructor() : BaseViewModel() {
    var auth: FirebaseAuth = Firebase.auth
    private var firestore: FirebaseFirestore = Firebase.firestore
    private var storage: FirebaseStorage = Firebase.storage
    var popUpLiveData = MutableLiveData<Boolean>()
    private lateinit var diaryDb: DiaryDatabase
    private lateinit var diaryDao: DiaryDao

    fun upload(
        date: String,
        title: String,
        content: String,
        mood: Int,
        selectedPicture: Uri? = null,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        context: Context?,
        diaryList: Diary?,
        bitmap: Bitmap? = null
    ) {
        if (auth.currentUser != null) {
            firebaseUpload(date, title, content, mood, selectedPicture, onSuccess, onFailure)
        } else {
            context?.let {
                diaryList?.let { diaryList ->
                    saveToRoom(
                        context,
                        diaryList,
                        bitmap,
                        onSuccess,
                        onFailure
                    )
                }
            }
        }
    }

    fun firebaseUpload(
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
        val user = auth.currentUser!!.uid
        val imageReference = reference.child("images").child(user).child("Diary").child(imageName)

        val postMap = hashMapOf<String, Any>()
        postMap["title"] = title
        postMap["content"] = content
        postMap["mood"] = mood
        postMap["date"] = date

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture).addOnSuccessListener {
                val uploadPictureReference = storage.reference.child("images").child(user).child("Diary").child(imageName)
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

    private fun addDiaryToFirebase(
        postMap: HashMap<String, Any>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val user = auth.currentUser!!.uid
        firestore.collection(user).document("Data").collection("DiaryList")
            .add(postMap).addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure.invoke()
        }
    }

    fun saveToRoom(
        context: Context,
        diary: Diary,
        bitmap: Bitmap? = null,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        diaryDb = Room.databaseBuilder(
            context,
            DiaryDatabase::class.java,
            "Diaries"
        ).fallbackToDestructiveMigration().build()
        bitmap?.let {
            diary.downloadUrl = saveImageToInternalStorage(it, context, diary.id)
        }
        diaryDao = diaryDb.diaryDao()
        var insertId: Long = -1
        try {
            CoroutineScope(Dispatchers.IO).launch {
                insertId = diaryDao.insert(diary)
                withContext(Dispatchers.Main) {
                    if (insertId > 0) {
                        onSuccess.invoke()
                    } else {
                        onFailure.invoke()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            onFailure.invoke()
        }
    }

    fun update(
        diaryId: String,
        date: String,
        title: String,
        content: String,
        mood: Int,
        selectedPicture: Uri? = null,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (auth.currentUser != null) {
            firebaseUpdate(
                diaryId,
                date,
                title,
                content,
                mood,
                selectedPicture,
                onSuccess,
                onFailure
            )
        } else {
            //update diary with room
        }
    }

    fun firebaseUpdate(
        diaryId: String,
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
        val user = auth.currentUser!!.uid
        val imageReference = reference.child("images").child(user).child(imageName)

        val postMap = hashMapOf<String, Any>()
        postMap["title"] = title
        postMap["content"] = content
        postMap["mood"] = mood
        postMap["date"] = date

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture).addOnSuccessListener {
                val uploadPictureReference = storage.reference.child("images").child(user).child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    postMap["downloadUrl"] = downloadUrl
                    updateToFirebase(diaryId, postMap, onSuccess, onFailure)
                }
            }.addOnFailureListener {
                popUpLiveData.value = !(popUpLiveData.value ?: false)
            }
        } else {
            updateToFirebase(diaryId, postMap, onSuccess, onFailure)
        }

    }

    private fun updateToFirebase(
        diaryId: String,
        postMap: HashMap<String, Any>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val user = auth.currentUser!!.uid
        firestore.collection(user).document("Data").collection("DiaryList").document(diaryId).update(postMap).addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure.invoke()
        }
    }

    private fun saveImageToInternalStorage(
        bitmapImage: Bitmap,
        context: Context,
        name: String
    ): String {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val imageName = "$name.jpg"
        val mypath = File(directory, imageName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath + "/$name.jpg"
    }
}