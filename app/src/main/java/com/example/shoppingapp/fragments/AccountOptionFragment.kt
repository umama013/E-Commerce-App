package com.example.shoppingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.FragmentAccountOptionBinding

class AccountOptionFragment : Fragment(R.layout.fragment_account_option) {
  private lateinit var binding : FragmentAccountOptionBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    binding =  FragmentAccountOptionBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
   binding.btnRegister.setOnClickListener {
     findNavController().navigate(R.id.action_accountOptionFragment_to_registerFragment)
   }

    binding.btnLogin.setOnClickListener {
      findNavController().navigate(R.id.action_accountOptionFragment_to_loginFragment)
    }
  }

}