package com.example.shoppingapp.mvvm

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.R
import com.example.shoppingapp.util.Collection.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
  private val sharedPreferences: SharedPreferences,
  private val firebaseAuth: FirebaseAuth
) : ViewModel() {

  private val _navigate = MutableStateFlow(0)
  val navigate: MutableStateFlow<Int> = _navigate

  companion object {

    const val SHOPPING_ACTIVITY = 23
    const val ACCOUNT_OPTION_FRAGMENT = 21

  }

  init {
    val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
    val user = firebaseAuth.currentUser
    if (user != null) {
      viewModelScope.launch {
        _navigate.emit(SHOPPING_ACTIVITY)
      }

    } else
      if (isButtonClicked) {
        viewModelScope.launch {
          _navigate.emit(R.id.action_introductionFragment_to_accountOptionFragment)
        }
      } else {
        Unit
      }

  }
  fun startButtonClicked(){
    sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
  }

}