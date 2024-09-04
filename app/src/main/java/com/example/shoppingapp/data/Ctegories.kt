package com.example.shoppingapp.data

sealed class Categories(val category: String) {
  object Fresh_Produce : Categories("Fresh Produce")
  object Bakery_Bread : Categories("Bakery & Bread")
  object Dairy_Eggs : Categories("Dairy&Eggs")
  object Snacks_Cookies_Chips : Categories("Snacks,Cookies&Chips")
  object Pantry : Categories("Pantry")

}