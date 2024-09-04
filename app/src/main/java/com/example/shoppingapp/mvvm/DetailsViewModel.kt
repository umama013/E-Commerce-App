package com.example.shoppingapp.mvvm

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.CartProduct
import com.example.shoppingapp.firebase.FirebaseCommons
import com.example.shoppingapp.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth,
  private val firebaseCommons: FirebaseCommons
) : ViewModel() {

  private val _addToCart = MutableStateFlow<Resources<CartProduct>>(Resources.Unspecified())
  val addToCart = _addToCart.asStateFlow()

  fun addUpdateProductInCart(cartProduct: CartProduct) {
    viewModelScope.launch {
      _addToCart.emit(Resources.Loading())
    }
    firestore.collection("user").document(auth.uid!!).collection("cart")
      .whereEqualTo("product.id", cartProduct.product.id).get().addOnSuccessListener {
        it.documents.let {
          if(it.isEmpty()){ // add new product
            addNewProduct(cartProduct)
          }else{
            val product = it.first().toObject(CartProduct::class.java)
            if(product == cartProduct){ // update the quantity
              val documentId = it.first().id
              updateProduct(cartProduct ,documentId)
            }else{ // add new product
              addNewProduct(cartProduct)
            }
          }
        }

    }.addOnFailureListener {
        viewModelScope.launch {
          _addToCart.emit(Resources.Error(it.message.toString()))
        }
      }
  }
  private fun addNewProduct(cartProduct: CartProduct){
    firebaseCommons.addProductToCart(cartProduct){ addedProduct,e->
      viewModelScope.launch {
        if(e == null)
          _addToCart.emit(Resources.Success(addedProduct!!))
        else
          _addToCart.emit(Resources.Error(e.message.toString()))
      }

    }
  }
 private fun updateProduct(cartProduct: CartProduct,documentId:String){
   firebaseCommons.increaseQuantity(documentId){_,e ->
     viewModelScope.launch {
       if(e == null)
         _addToCart.emit(Resources.Success(cartProduct))
       else
         _addToCart.emit(Resources.Error(e.message.toString()))
     }

   }

   }
 }
