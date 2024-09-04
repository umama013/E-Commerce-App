package com.example.shoppingapp.util

import android.util.Patterns
import java.util.regex.Pattern

fun validationEmail( email:String):RegisterValidation{
  if(email.isEmpty())
    return RegisterValidation.Failed("Email cannot be empty")
  if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() )
    return RegisterValidation.Failed("Email format not supported")
  return RegisterValidation.Success
}
fun validatePassword(password:String):RegisterValidation{
  if(password.isEmpty())
    return RegisterValidation.Failed("Password cannot be empty")
  if(password.length<6)
    return RegisterValidation.Failed("Password should contain 6 characters")
  return RegisterValidation.Success
}