package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import com.example.shoppingapp.data.Users
import com.example.shoppingapp.util.Collection.USER_COLLECTION
import com.example.shoppingapp.util.RegisterFields
import com.example.shoppingapp.util.RegisterValidation
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.validatePassword
import com.example.shoppingapp.util.validationEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
  private val firebaseAuth: FirebaseAuth,
  private val db: FirebaseFirestore,
) : ViewModel() {

  private val _register = MutableStateFlow<Resources<Users>>(Resources.Unspecified())
  val register: Flow<Resources<Users>> = _register
  private val _validation = Channel<RegisterFields>()
  val validation = _validation.receiveAsFlow()
  fun createAccountWithEmailAndPassword(user: Users, password: String) {
    if (checkValidation(user, password)) {
      runBlocking {
        _register.emit(Resources.Loading())
      }
      firebaseAuth.createUserWithEmailAndPassword(user.email, password)
        .addOnSuccessListener { it ->
          it.user?.let {
            saveUserInDb(it.uid, user)
          }
        }.addOnFailureListener {
          _register.value = Resources.Error(it.message.toString())
        }

    } else {
      val registerFieldState = RegisterFields(
        validationEmail(user.email), validatePassword(password)
      )
      runBlocking {
        _validation.send(registerFieldState)
      }

    }
  }

  private fun saveUserInDb(userId: String, user: Users) {
    db.collection(USER_COLLECTION)
      .document(userId)
      .set(user)
      .addOnSuccessListener {
        _register.value = Resources.Success(user)
      }.addOnFailureListener {
        _register.value = Resources.Error(it.message.toString())
      }
  }

  private fun checkValidation(user: Users, password: String): Boolean {
    val emailValidation = validationEmail(user.email)
    val passwordValidation = validatePassword(password)
    val shouldRegister = emailValidation is RegisterValidation.Success &&
        passwordValidation is RegisterValidation.Success
    return shouldRegister
  }

}