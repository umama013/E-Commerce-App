package com.example.shoppingapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.activities.LoginAndRegisterActivity
import com.example.shoppingapp.activities.ShoppingActivity
import com.example.shoppingapp.databinding.FragmentLoginBinding
import com.example.shoppingapp.mvvm.LoginViewModel
import com.example.shoppingapp.util.Resources
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

  private lateinit var binding: FragmentLoginBinding
  private val viewModel by viewModels<LoginViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentLoginBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.apply {
      btnLogin.setOnClickListener {
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString()
        viewModel.login(email, password)
      }
    }
    binding.tvRegister.setOnClickListener {
      findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

    }

    lifecycleScope.launchWhenStarted {
      viewModel.login.collect {
        when (it) {
          is Resources.Loading -> {
            binding.btnLogin.startAnimation()
          }

          is Resources.Success -> {
            binding.btnLogin.revertAnimation()
            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
              intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
              )
              startActivity(intent)
            }
          }

          is Resources.Error -> {
            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
            binding.btnLogin.revertAnimation()
          }

          else -> Unit
        }
      }
    }

  }
}


