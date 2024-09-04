package com.example.shoppingapp.data

data class Users(
  val firstName :String,
  val lastName : String,
  val email : String,
  val filePath : String
)
{
  constructor() : this("","","","")
}