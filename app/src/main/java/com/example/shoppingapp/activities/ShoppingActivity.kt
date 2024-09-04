package com.example.shoppingapp.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.shoppingapp.R
import com.example.shoppingapp.databinding.LayoutShoppingActivityBinding
import com.example.shoppingapp.mvvm.CartViewModel
import com.example.shoppingapp.util.Resources
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity :AppCompatActivity() {

  val binding by lazy {
    LayoutShoppingActivityBinding.inflate(layoutInflater)
  }
  val viewModel by viewModels<CartViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    val navController = findNavController(R.id.shopping_host_fragment)
    binding.bottomNavMenu.setupWithNavController(navController)
      lifecycleScope.launchWhenStarted {
    viewModel.cartProducts.collectLatest {
    when(it){
    is Resources.Success ->{
    val count = it.data?.size ?:0
    val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav_menu)
    bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
    number = count
    backgroundColor = resources.getColor(R.color.g_blue)
    }
    }

    else -> {}
    }
    }
    }
    }
  }
