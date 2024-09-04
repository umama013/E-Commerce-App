package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Categories
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.util.Resources
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel constructor(
  private val firestore: FirebaseFirestore,
  private val category: Categories
) : ViewModel() {

  private val _offerProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
  val offerProducts = _offerProducts.asStateFlow()
  private val _bestProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
  val bestProducts = _bestProducts.asStateFlow()
  init {
    fetchOfferProducts()
    fetchBestProducts()

  }

  fun fetchOfferProducts() {
    viewModelScope.launch {
      _offerProducts.emit(Resources.Loading())
    }
    firestore.collection("Products").whereEqualTo("category", category.category).whereNotEqualTo(
      "offerPercentage", null
    ).get()
      .addOnSuccessListener {
        val products = it.toObjects(Product::class.java)
        viewModelScope.launch {
          _offerProducts.emit(Resources.Success(products))
        }
      }.addOnFailureListener {
        viewModelScope.launch {
          _offerProducts.emit(Resources.Error(it.message.toString()))
        }
      }
  }
  fun fetchBestProducts() {
    viewModelScope.launch {
      _bestProducts.emit(Resources.Loading())
    }
    firestore.collection("Products").whereEqualTo("category", category.category).whereEqualTo(
      "offerPercentage", null
    ).get()
      .addOnSuccessListener {
        val products = it.toObjects(Product::class.java)
        viewModelScope.launch {
          _bestProducts.emit(Resources.Success(products))
        }
      }.addOnFailureListener {
        viewModelScope.launch {
          _bestProducts.emit(Resources.Error(it.message.toString()))
        }
      }
  }
}