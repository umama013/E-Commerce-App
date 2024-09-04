package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.util.Resources
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
  private val firestore: FirebaseFirestore
) : ViewModel() {

  private val _specialProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
  val specialProducts: MutableStateFlow<Resources<List<Product>>> = _specialProducts
  private val _bestDealsProducts =
    MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
  val bestDealsProducts: MutableStateFlow<Resources<List<Product>>> = _bestDealsProducts
  private val _bestProducts = MutableStateFlow<Resources<List<Product>>>(Resources.Unspecified())
  val bestProducts: MutableStateFlow<Resources<List<Product>>> = _bestProducts

  init {
    fetchBestProducts()
    fetchSpecialProducts()
    fetchBestDealsProducts()
  }

  fun fetchSpecialProducts() {
    viewModelScope.launch {
      _specialProducts.emit(Resources.Loading())
    }
    firestore.collection("Products").whereEqualTo("category", "Special Product").get()
      .addOnSuccessListener { result ->
        val specialProductsList = result.toObjects(Product::class.java)
        viewModelScope.launch {
          _specialProducts.emit(Resources.Success(specialProductsList))
        }
      }.addOnFailureListener {
        viewModelScope.launch {
          _specialProducts.emit(Resources.Error(it.message.toString()))
        }
      }

  }

  fun fetchBestDealsProducts() {
    viewModelScope.launch {
      _bestDealsProducts.emit(Resources.Loading())
    }
    firestore.collection("Products").whereEqualTo("category", "Best Deals").get()
      .addOnSuccessListener { result ->
        val bestDealsProducts = result.toObjects(Product::class.java)
        viewModelScope.launch {
          _bestDealsProducts.emit(Resources.Success(bestDealsProducts))
        }
      }.addOnFailureListener {
        viewModelScope.launch {
          _bestDealsProducts.emit(Resources.Error(it.message.toString()))
        }
      }
  }

  fun fetchBestProducts() {
    viewModelScope.launch {
      _bestProducts.emit(Resources.Loading())
    }
   firestore.collection("Products").whereEqualTo("category","Best Product").get().addOnSuccessListener { result->
     val bestProducts  = result.toObjects(Product::class.java)
     viewModelScope.launch {
       _bestProducts.emit(Resources.Success(bestProducts))
     }
   }.addOnFailureListener {
     viewModelScope.launch {
       _bestProducts.emit(Resources.Error(it.message.toString()))
     }
   }
  }

}