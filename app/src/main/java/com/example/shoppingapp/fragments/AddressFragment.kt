package com.example.shoppingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shoppingapp.R
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.databinding.FragmentAddressBinding
import com.example.shoppingapp.mvvm.AddressViewModel
import com.example.shoppingapp.util.Resources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment(R.layout.fragment_address) {

  private lateinit var binding: FragmentAddressBinding
  private val viewModel by viewModels<AddressViewModel>()
   val args by navArgs<AddressFragmentArgs>()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentAddressBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val address = args.address
    if(address==null){
      binding.btnDelete.visibility = View.INVISIBLE
    }else{
      binding.apply {
     edtLocation.setText(address.addressTitle)
     edtCity.setText(address.city)
     edtState.setText(address.state)
     edtStreet.setText(address.street)
     edtName.setText(address.fullName)
     edtPhone.setText(address.phone)
      }
    }

    lifecycleScope.launchWhenStarted {
      viewModel.addNewAddress.collectLatest {
        when (it) {
          is Resources.Loading -> {
            binding.progressBar.visibility = View.VISIBLE
          }

          is Resources.Success -> {
            binding.progressBar.visibility = View.GONE
            findNavController().navigateUp()
          }

          is Resources.Error -> {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }
    lifecycleScope.launchWhenStarted {
      viewModel.addressInvalid.collectLatest {
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
      }
    }

    binding.apply {
      btnSave.setOnClickListener {
        val location = edtLocation.text.toString()
        val street = edtStreet.text.toString()
        val name = edtName.text.toString()
        val phone = edtPhone.text.toString()
        val city = edtCity.text.toString()
        val state = edtState.text.toString()
        val address = Address(location, street, name, phone, city, state)
        viewModel.addAddress(address)
      }
      btnClose.setOnClickListener {
        findNavController().popBackStack()
      }
    }
  }
}