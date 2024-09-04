package com.example.shoppingapp.mvvm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shoppingapp.data.Categories
import com.example.shoppingapp.mvvm.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelFactory(
  private val firestore: FirebaseFirestore,
  private val category: Categories
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return CategoryViewModel(firestore,category) as T
  }
}