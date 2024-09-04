package com.example.shoppingapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.shoppingapp.R
import com.example.shoppingapp.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.showBottomNavigationView(){
  val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottom_nav_menu)
  bottomNavigationView.visibility = View.VISIBLE
}
fun Fragment.hideBottomNavigationView(){
  val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottom_nav_menu)
  bottomNavigationView.visibility = View.GONE
}