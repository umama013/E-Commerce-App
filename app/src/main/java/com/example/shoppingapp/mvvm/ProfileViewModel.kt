package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Users
import com.example.shoppingapp.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth
) : ViewModel() {

  private val _user = MutableStateFlow<Resources<Users>>(Resources.Unspecified())
  val user = _user.asStateFlow()

  init {
    getUser()
  }

  fun getUser() {
    viewModelScope.launch {
      _user.emit(Resources.Loading())
    }
    firestore.collection("users").document(auth.uid!!).addSnapshotListener { value, error ->
      if (error != null) {
        viewModelScope.launch {
          _user.emit(Resources.Error(error.message.toString()))
        }
      } else {
        val user = value?.toObject(Users::class.java)

        user?.let {
          viewModelScope.launch {
            _user.emit(Resources.Success(user))
          }
        }

      }
    }
  }

  fun logout(){
    auth.signOut()
  }

}