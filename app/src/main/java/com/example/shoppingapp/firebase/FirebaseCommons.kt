package com.example.shoppingapp.firebase

import com.example.shoppingapp.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.lang.Exception

class FirebaseCommons(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth
) {

  private val cartCollection = firestore.collection("user").document(auth.uid!!).collection("cart")
  fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
    cartCollection.document().set(cartProduct).addOnSuccessListener {
      onResult(cartProduct, null)
    }.addOnFailureListener {
      onResult(null, it)
    }
  }

  fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
    firestore.runTransaction { transaction ->
      val documentRef = cartCollection.document(documentId)
      val document = transaction.get(documentRef)
      val productObject = document.toObject(CartProduct::class.java)
      productObject?.let { cartProduct ->
        val newQuantity = cartProduct.quantity + 1
        val newProduct = cartProduct.copy(quantity = newQuantity)
        transaction.set(documentRef, newProduct)
      }
    }.addOnSuccessListener {
      onResult(documentId, null)
    }.addOnFailureListener {
      onResult(null, it)
    }

  }

  fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
    firestore.runTransaction { transaction ->
      val documentRef = cartCollection.document(documentId)
      val document = transaction.get(documentRef)
      val productObject = document.toObject(CartProduct::class.java)
      productObject?.let { cartProduct ->
        val newQuantity = cartProduct.quantity - 1
        val newProduct = cartProduct.copy(quantity = newQuantity)
        transaction.set(documentRef, newProduct)
      }
    }.addOnSuccessListener {
      onResult(documentId, null)
    }.addOnFailureListener {
      onResult(null, it)
    }
  }

  enum class QuantityChanging {
    INCREASE, DECREASE
  }
}