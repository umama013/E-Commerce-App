package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class BillingViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth
) : ViewModel() {

  private val _getAddresses = MutableStateFlow<Resources<List<Address>>>(Resources.Unspecified())
  val getAddresses = _getAddresses.asStateFlow()

  init {

    getAddress()
  }
  fun getAddress() {
    viewModelScope.launch { _getAddresses.emit(Resources.Loading()) }
    firestore.collection("user").document(auth.uid!!).collection("address")
      .addSnapshotListener{ value,error ->
        if(error != null){
          viewModelScope.launch {
            _getAddresses.emit(Resources.Error(error.message.toString()))
          }
          return@addSnapshotListener
        }
        val address = value?.toObjects(Address::class.java)
        viewModelScope.launch {
          _getAddresses.emit(Resources.Success(address!!))
        }
      }

  }

}