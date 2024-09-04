package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
  private val firebaseAuth: FirebaseAuth
) : ViewModel() {

  private val _login = MutableSharedFlow<Resources<FirebaseUser>>()
  val login = _login.asSharedFlow()
  fun login(email: String, password: String) {
    viewModelScope.launch {
      _login.emit(Resources.Loading())
    }
    firebaseAuth.signInWithEmailAndPassword(email, password)
      .addOnSuccessListener {
        viewModelScope.launch {
          it.user?.let {
            _login.emit(Resources.Success(it))
          }
        }
      }.addOnFailureListener {
        viewModelScope.launch {
          _login.emit(Resources.Error(it.message.toString()))
        }
      }

  }
}