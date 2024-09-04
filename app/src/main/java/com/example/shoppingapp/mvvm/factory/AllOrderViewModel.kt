package com.example.shoppingapp.mvvm.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.order.Order
import com.example.shoppingapp.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AllOrderViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth
) : ViewModel() {

  private val _allOrders = MutableStateFlow<Resources<List<Order>>>(Resources.Unspecified())
  val allOrders = _allOrders.asStateFlow()

  init {
    getAllOrders()
  }

  private fun getAllOrders() {
    viewModelScope.launch {
      _allOrders.emit(Resources.Loading())
    }
    firestore.collection("user").document(auth.uid!!).collection("orders").get()
      .addOnSuccessListener {

        val orders = it.toObjects(Order::class.java)
        viewModelScope.launch {
          _allOrders.emit(Resources.Success(orders))
        }


      }.addOnFailureListener {

        viewModelScope.launch {
          _allOrders.emit(Resources.Error(it.message.toString()))
        }
      }

  }

}