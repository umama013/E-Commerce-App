package com.example.shoppingapp.shoppingActivityFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppingapp.R
import com.example.shoppingapp.adapters.HomeViewPagerAdapter
import com.example.shoppingapp.databinding.FragmentHome2Binding
import com.example.shoppingapp.fragments.categories.PantryFragment
import com.example.shoppingapp.fragments.categories.FreshProduceFragment
import com.example.shoppingapp.fragments.categories.DairyFragment
import com.example.shoppingapp.fragments.categories.BakeryFragment
import com.example.shoppingapp.fragments.categories.MainCategoryFragment
import com.example.shoppingapp.fragments.categories.SnacksFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home2) {

  lateinit var binding: FragmentHome2Binding
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentHome2Binding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val categoriesFragments = arrayListOf<Fragment>(
      MainCategoryFragment(),
      FreshProduceFragment(),
      DairyFragment(),
      SnacksFragment(),
      BakeryFragment(),
      PantryFragment()
    )
    val viewPagerAdapter = HomeViewPagerAdapter(categoriesFragments,childFragmentManager, lifecycle)
    binding.homeViewPager.adapter = viewPagerAdapter
    TabLayoutMediator(binding.tabLayout,binding.homeViewPager){ tab,position->
      when(position){
        0-> tab.text = "All"
        1-> tab.text = "Fresh Produce"
        2-> tab.text = "Bakery & Bread"
        3-> tab.text = "Dairy&Eggs"
        4-> tab.text = "Snacks,Cookies & Chips"
        5-> tab.text = "Pantry"
      }

    }.attach()

  }

}


