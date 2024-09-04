package com.example.shoppingapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.activities.LoginAndRegisterActivity
import com.example.shoppingapp.activities.ShoppingActivity
import com.example.shoppingapp.databinding.FragmentIntroductionBinding
import com.example.shoppingapp.databinding.FragmentLoginBinding
import com.example.shoppingapp.mvvm.IntroductionViewModel
import com.example.shoppingapp.mvvm.IntroductionViewModel.Companion.ACCOUNT_OPTION_FRAGMENT
import com.example.shoppingapp.mvvm.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

      lifecycleScope.launchWhenStarted {
        viewModel.navigate.collect(){
          when(it){
            SHOPPING_ACTIVITY ->{
              Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                intent.addFlags(
                  Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                )
                startActivity(intent)
              }
          }
            ACCOUNT_OPTION_FRAGMENT ->{
              findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)
            }
      else -> Unit
        }
        }
      }


      binding.btnStart.setOnClickListener {
        viewModel.startButtonClicked()
        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)

      }
    }
}


