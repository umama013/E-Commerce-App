package com.example.shoppingapp.mvvm

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
class OrderViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth
): ViewModel() {

  private val _order = MutableStateFlow<Resources<Order>>(Resources.Unspecified())
  val order = _order.asStateFlow()

  fun placeOrder(order: Order){
    viewModelScope.launch {
      _order.emit(Resources.Loading())
    }
    firestore.runBatch { batch ->
      //TODO: add order into user order collection
      //TODO: add order into orders collection
      //TODO: delete the products from user cart collection

      firestore.collection("user")
        .document(auth.uid!!)
        .collection("orders")
        .document()
        .set(order)
      firestore.collection("orders").document().set(order)

      firestore.collection("user").document(auth.uid!!).collection("cart").get()
        .addOnSuccessListener {
          it.documents.forEach {
            it.reference.delete()
          }
        }
    }.addOnSuccessListener {
      viewModelScope.launch {
        _order.emit(Resources.Success(order))
      }
    }.addOnFailureListener {
      viewModelScope.launch {
        _order.emit(Resources.Error(it.message.toString()))
      }
    }

  }
}