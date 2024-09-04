package com.example.shoppingapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.R
import com.example.shoppingapp.adapters.BestDealsAdapter
import com.example.shoppingapp.adapters.BestProductAdapter
import com.example.shoppingapp.adapters.SpecialProductsAdapter
import com.example.shoppingapp.databinding.MainCategoryFragmentBinding
import com.example.shoppingapp.mvvm.MainCategoryViewModel
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.main_category_fragment) {

 private lateinit var binding : MainCategoryFragmentBinding
 private lateinit var specialProductsAdapter : SpecialProductsAdapter
 private lateinit var bestProductAdapter: BestProductAdapter
 private lateinit var bestDealsAdapter: BestDealsAdapter
 private val viewModel by viewModels<MainCategoryViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
   binding = MainCategoryFragmentBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpSpecialProductsRv()
    setUPBestDealsRv()
    setUpBestProductsRv()
    specialProductsAdapter.onClick ={
      val b = Bundle().apply {
        putParcelable("product",it)
      }
      findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,b)
    }
    bestProductAdapter.onClick ={
      val b = Bundle().apply {
        putParcelable("product",it)
      }
      findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,b)
    }
    bestDealsAdapter.onClick ={
      val b = Bundle().apply {
        putParcelable("product",it)
      }
      findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment,b)
    }
    lifecycleScope.launchWhenStarted {
      viewModel.specialProducts.collectLatest {
        when(it){
          is Resources.Loading->{
            showLoading()
          }
            is Resources.Success->{
              hideLoading()
              specialProductsAdapter.differ.submitList(it.data)
            }
            is Resources.Error->{
              hideLoading()
              Log.e("Main Category Frafmnet", "onViewCreated: ${it.message.toString()}", )
              Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
            }

          else -> {}
        }
      }
    }
    lifecycleScope.launchWhenStarted {
      viewModel.bestDealsProducts.collectLatest {
        when(it){
          is Resources.Loading ->{
            showLoading()
          }
          is Resources.Success ->{
            hideLoading()
            bestDealsAdapter.differ.submitList(it.data)
          }
          is Resources.Error ->{
            hideLoading()
            Log.e("Main Category Fragment", "onViewCreated: ${it.message.toString()}", )
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
          }

          else -> {}
        }

        }
      }

    lifecycleScope.launchWhenStarted {
      viewModel.bestProducts.collectLatest {
        when(it){
          is Resources.Loading ->{
            showLoading()
          }
          is Resources.Success ->{
            hideLoading()
            bestProductAdapter.differ.submitList(it.data)
          }
          is Resources.Error ->{
            hideLoading()
            Log.e("Main Category Fragment", "onViewCreated: ${it.message.toString()}", )
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
          }

          else -> {}
        }

      }
    }
    }


  private fun setUpBestProductsRv() {
   bestProductAdapter = BestProductAdapter()
    binding.rvBestProducts.apply {
      layoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
      adapter = bestProductAdapter
    }
  }

  private fun setUPBestDealsRv() {
    bestDealsAdapter = BestDealsAdapter()
    binding.rvBestDealsProducts.apply {
      layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
      adapter = bestDealsAdapter
    }

  }

  private fun hideLoading() {
   binding.specialProductsProgressBar.visibility = View.GONE
  }

  private fun showLoading() {
    binding.specialProductsProgressBar.visibility = View.VISIBLE
  }

  private fun setUpSpecialProductsRv() {
   specialProductsAdapter = SpecialProductsAdapter()
    binding.rvSpecialProducts.apply {
      layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
      adapter = specialProductsAdapter
    }
  }

  override fun onResume() {
    super.onResume()
    showBottomNavigationView()
  }

}