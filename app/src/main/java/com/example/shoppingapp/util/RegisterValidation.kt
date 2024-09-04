package com.example.shoppingapp.util

sealed class RegisterValidation() {
  object Success : RegisterValidation()
  data class Failed(val message: String ) : RegisterValidation()

}

data class RegisterFields(
  val email: RegisterValidation,
  val password: RegisterValidation
)