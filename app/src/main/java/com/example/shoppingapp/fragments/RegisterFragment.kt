package com.example.shoppingapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.data.Users
import com.example.shoppingapp.databinding.FragmentRegisterBinding
import com.example.shoppingapp.mvvm.RegisterViewModel
import com.example.shoppingapp.util.RegisterValidation
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.validationEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "Register Fragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {

  private lateinit var binding: FragmentRegisterBinding
  private val viewModel by viewModels<RegisterViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentRegisterBinding.inflate(inflater)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.apply {
      btnRegister.setOnClickListener {
        val user = Users(
          edtFirstName.text.toString().trim(),
          edtLastName.text.toString().trim(),
          edtEmail.text.toString().trim(),
          ""
        )
        val password = edtPassword.text.toString()
        viewModel.createAccountWithEmailAndPassword(user, password)
      }
    }
    binding.tvSubtitle.setOnClickListener {
      findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }
    lifecycleScope.launchWhenStarted {
      viewModel.register.collect {
        when (it) {
          is Resources.Loading -> {
            binding.btnRegister.startAnimation()

          }

          is Resources.Success -> {
            Log.d("test", "onViewCreated:${it.data.toString()} ")
            binding.btnRegister.revertAnimation()
          }

          is Resources.Error -> {
            Log.e(TAG, "onViewCreated: ${it.message.toString()}")
            binding.btnRegister.revertAnimation()
          }

          else -> Unit
        }
      }
    }
    lifecycleScope.launchWhenStarted {
      viewModel.validation.collect {
        if (it.email is RegisterValidation.Failed) {
          withContext(Dispatchers.Main) {
            binding.edtEmail.apply {
              requestFocus()
              error = it.email.message
            }
          }

        }
        if (it.password is RegisterValidation.Failed) {
          if (it.email is RegisterValidation.Failed) {
            withContext(Dispatchers.Main) {
              binding.edtPassword.apply {
                requestFocus()
                error = it.password.message
              }
            }
          }
        }
      }
    }
  }
}




