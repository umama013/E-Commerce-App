package com.example.shoppingapp.shoppingActivityFragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.shoppingapp.adapters.ColorsAdapter
import com.example.shoppingapp.adapters.SizesAdapter
import com.example.shoppingapp.adapters.ViewPager2Images
import com.example.shoppingapp.data.CartProduct
import com.example.shoppingapp.databinding.FragmentProductDetailsBinding
import com.example.shoppingapp.mvvm.DetailsViewModel
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.hideBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ProductsDetailsFragment : Fragment() {

  private lateinit var binding: FragmentProductDetailsBinding
  private val sizesAdapter by lazy {
    SizesAdapter()
  }
  private val colorsAdapter by lazy { ColorsAdapter() }
  private val viewPagerAdapter by lazy { ViewPager2Images() }
  private val args by navArgs<ProductsDetailsFragmentArgs>()
  private var selectedColor: Int? = null
  private var selectedSize: String? = null
  private val viewModels by viewModels<DetailsViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentProductDetailsBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    hideBottomNavigationView()
    val product = args.product


    binding.apply {
      productName.text = product.name
      productPrice.text = "$ ${product.price}"
      productDesc.text = product.description
      viewPagerAdapter.differ.submitList(product.images)
      product.colors?.let {
        colorsAdapter.differ.submitList(it)
      }
    }

    product.sizes?.let {
      sizesAdapter.differ.submitList(it)
    }
    setUpViewPagerRv()
    setUpColorsRv()
    setUpSizesRv()
    binding.closeBtn.setOnClickListener {
      findNavController().navigateUp()
    }
    sizesAdapter.onItemClick = {
      selectedSize = it
    }
    colorsAdapter.onItemClick = {
      selectedColor = it
    }
    binding.btnAddToCart.setOnClickListener {
      viewModels.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
    }
    lifecycleScope.launchWhenStarted {
      viewModels.addToCart.collectLatest {
        when (it) {
          is Resources.Loading ->
            binding.btnAddToCart.startAnimation()

          is Resources.Success ->{
            binding.btnAddToCart.revertAnimation()
            Toast.makeText(requireContext(), "Product is successfully added", Toast.LENGTH_LONG).show()

          }

          is Resources.Error -> {
            binding.btnAddToCart.stopAnimation()

            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }
  }

  private fun setUpSizesRv() {
    binding.rvSize.adapter = sizesAdapter
    binding.rvSize.layoutManager =
      LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

  }

  private fun setUpColorsRv() {
    binding.apply {
      rvColor.adapter = colorsAdapter
      rvColor.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }
  }

  private fun setUpViewPagerRv() {
    binding.viewPagerProductImages.adapter = viewPagerAdapter
  }
}