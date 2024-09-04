package com.example.shoppingapp.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.util.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
  private val firestore: FirebaseFirestore,
  private val auth: FirebaseAuth
) : ViewModel() {

  private val _addNewAddress = MutableStateFlow<Resources<Address>>(Resources.Unspecified())
  val addNewAddress = _addNewAddress.asStateFlow()

  private val _addressInvalid = MutableSharedFlow<String>()
  val addressInvalid = _addressInvalid.asSharedFlow()

  fun addAddress(address: Address) {
    val validateInputs = validateInputs(address)
    if (validateInputs) {
      viewModelScope.launch {
        _addNewAddress.emit(Resources.Loading())
      }
      firestore.collection("user").document(auth.uid!!).collection("address").document()
        .set(address).addOnSuccessListener {
          viewModelScope.launch {
            _addNewAddress.emit(Resources.Success(address))
          }
        }.addOnFailureListener {
          viewModelScope.launch {
            _addNewAddress.emit(Resources.Error(it.message.toString()))
          }
        }
    }else{
     viewModelScope.launch {
      _addressInvalid.emit("All fields are required")
     }
    }
  }

  private fun validateInputs(address: Address): Boolean {
    return (address.addressTitle.trim().isNotEmpty() &&
        address.fullName.trim().isNotEmpty() &&
        address.phone.trim().isNotEmpty() &&
        address.city.trim().isNotEmpty() &&
        address.state.trim().isNotEmpty() &&
        address.street.trim().isNotEmpty())

  }
}