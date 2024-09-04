package com.example.shoppingapp.mvvm

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.KlieneApplication
import com.example.shoppingapp.KlieneApplication_GeneratedInjector
import com.example.shoppingapp.data.Users
import com.example.shoppingapp.util.RegisterValidation
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.validationEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
  private val storage: StorageReference,
  app: Application
) : AndroidViewModel(app) {

  private val _user = MutableStateFlow<Resources<Users>>(Resources.Unspecified())
  val user = _user.asStateFlow()
  private val _updateInfo = MutableStateFlow<Resources<Users>>(Resources.Unspecified())
  val updateInfo = _updateInfo.asStateFlow()

  init {
    getUser()
    Log.d("get user", "get user ")
  }

  fun getUser() {
    viewModelScope.launch {
      _user.emit(Resources.Loading())
    }
    firestore.collection("user").document(auth.uid!!).get()
      .addOnSuccessListener {
        val user = it.toObject(Users::class.java)
        user?.let {
          viewModelScope.launch {
            _user.emit(Resources.Success(user))
          }
        }
      }.addOnFailureListener {
        viewModelScope.launch {
          _user.emit(Resources.Error(it.message.toString()))
        }
      }
  }

  fun updateUser(user: Users, imageUri: Uri?) {
    val areInputsValid =
      validationEmail(user.email) is RegisterValidation.Success && user.firstName.trim()
        .isNotEmpty()
          && user.lastName.trim().isNotEmpty()

    if (!areInputsValid) {
      viewModelScope.launch {
        _updateInfo.emit(Resources.Error("Check your Inputs"))
      }
      return
    }
    viewModelScope.launch {
      _updateInfo.emit(Resources.Loading())
    }
    if (imageUri == null) {
      saveUserInformation(user, true)
    } else {
      saveUserInformationWithNewImage(user, imageUri)
    }
  }

  private fun saveUserInformationWithNewImage(user: Users, imageUri: Uri) {
    viewModelScope.launch {
      try {
        val imageBitmap = MediaStore.Images.Media.getBitmap(
          getApplication<KlieneApplication>().contentResolver,
          imageUri
        )
        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
        val imageByteArray = byteArrayOutputStream.toByteArray()
        val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
        val result = imageDirectory.putBytes(imageByteArray).await()
        val imageUrl = result.storage.downloadUrl.await().toString()
        saveUserInformation(user.copy(filePath = imageUrl), false)
      } catch (e: Exception) {
        viewModelScope.launch {
          _updateInfo.emit(Resources.Error(e.message.toString()))
        }

      }
    }

  }

  private fun saveUserInformation(user: Users, shouldRetrieveOldImage: Boolean) {
    firestore.runTransaction { transaction ->
      val documentRef = firestore.collection("user").document(auth.uid!!)
      if (shouldRetrieveOldImage) {
        val currentUser = transaction.get(documentRef).toObject(Users::class.java)
        val newUser = user.copy(filePath = currentUser?.filePath ?: "")
        transaction.set(documentRef, newUser)
      } else {
        transaction.set(documentRef, user)
      }
    }.addOnSuccessListener {
      viewModelScope.launch {
        _updateInfo.emit(Resources.Success(user))
      }
    }.addOnFailureListener {
      viewModelScope.launch {
        _updateInfo.emit(Resources.Error(it.message.toString()))
      }
    }

  }
}
