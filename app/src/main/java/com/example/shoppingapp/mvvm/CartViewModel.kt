package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.CartProduct
import com.example.shoppingapp.firebase.FirebaseCommons
import com.example.shoppingapp.helper.getProductPrice
import com.example.shoppingapp.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
  private val firebaseCommons: FirebaseCommons
) : ViewModel() {

  private val _cartProducts =
    MutableStateFlow<Resources<List<CartProduct>>>(Resources.Unspecified())
  val cartProducts = _cartProducts.asStateFlow()
  private var cartProductsDocuments = emptyList<DocumentSnapshot>()
  private val _deleteDialog = MutableSharedFlow<CartProduct>()
  val deleteDialog = _deleteDialog.asSharedFlow()

  val productPrice = _cartProducts.map {
    when (it) {
      is Resources.Success -> {
        calculatePrice(it.data!!)
      }

      else -> null
    }
  }

  fun deleteCartProduct(cartProduct: CartProduct) {
    val index = cartProducts.value.data?.indexOf(cartProduct)
    if (index != null && index != -1) {
      val documentId = cartProductsDocuments[index].id
      firestore.collection("user").document(auth.uid!!).collection("cart").document(documentId)
        .delete()
    }
  }

  private fun calculatePrice(data: List<CartProduct>): Float {
    return data.sumByDouble { cartProduct ->
      (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
    }.toFloat()
  }

  init {
    getCartProducts()
  }

  private fun getCartProducts() {
    viewModelScope.launch {
      _cartProducts.emit(Resources.Loading())
      firestore.collection("user").document(auth.uid!!).collection("cart")
        .addSnapshotListener { value, error ->
          if (error != null || value == null) {
            viewModelScope.launch {
              _cartProducts.emit(Resources.Error(error?.message.toString()))
            }

          } else {
            cartProductsDocuments = value.documents
            val cartProducts = value.toObjects(CartProduct::class.java)
            viewModelScope.launch {
              _cartProducts.emit(Resources.Success(cartProducts))
            }
          }
        }
    }
  }

  fun updateQuantity(cartProduct: CartProduct, quantityChanging: FirebaseCommons.QuantityChanging) {
    val index = cartProducts.value.data?.indexOf(cartProduct)
    if (index != null && index != -1) {
      val documentId = cartProductsDocuments[index].id
      when (quantityChanging) {
        FirebaseCommons.QuantityChanging.INCREASE -> {
          viewModelScope.launch {
            _cartProducts.emit(Resources.Loading())
          }
          increaseQuantity(documentId)
        }

        FirebaseCommons.QuantityChanging.DECREASE -> {
          if (cartProduct.quantity == 1) {
            viewModelScope.launch {
              _deleteDialog.emit(cartProduct)
            }
            return
          }
          viewModelScope.launch {
            _cartProducts.emit(Resources.Loading())
          }
          decreaseQuantity(documentId)
        }
      }
    }
  }

  private fun decreaseQuantity(documentId: String) {
    firebaseCommons.decreaseQuantity(documentId) { result, execption ->
      if (execption != null) {
        viewModelScope.launch {
          _cartProducts.emit(Resources.Error(execption.message.toString()))
        }
      }
    }
  }

  private fun increaseQuantity(documentId: String) {
    firebaseCommons.increaseQuantity(documentId) { result, exception ->
      if (exception != null) {
        viewModelScope.launch {
          _cartProducts.emit(Resources.Error(exception.message.toString()))
        }
      }
    }
  }

}

