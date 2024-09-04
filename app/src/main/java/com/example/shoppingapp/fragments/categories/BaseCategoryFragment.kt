package com.example.shoppingapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.adapters.BestProductAdapter
import com.example.shoppingapp.databinding.BaseCategoryFragmentBinding
import com.example.shoppingapp.util.showBottomNavigationView

open class BaseCategoryFragment : Fragment(R.layout.base_category_fragment) {

  private lateinit var binding: BaseCategoryFragmentBinding
  protected val rvOfferAdapter: BestProductAdapter by lazy { BestProductAdapter() }
  protected val bestProductAdapter: BestProductAdapter by lazy { BestProductAdapter() }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = BaseCategoryFragmentBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpRvOffer()
    setUpBestProductRv()

    binding.offerRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (!recyclerView.canScrollVertically(1) && dx != 0) {
          onOfferPagingRequest()
        }
      }

    })

    rvOfferAdapter.onClick = {
      val b = Bundle().apply {
        putParcelable("product", it)
      }
      findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment, b)
    }
    bestProductAdapter.onClick = {
      val b = Bundle().apply {
        putParcelable("product", it)
      }
      findNavController().navigate(R.id.action_homeFragment_to_productsDetailsFragment, b)
    }

    binding.baseCategoryNestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
      if (v.getChildAt(0).bottom <= v.height + scrollY) {
        onBestProductPagingRequest()
      }
    })
  }

  fun showOfferProductLoading() {
    binding.offerProductsProgressBar.visibility = View.VISIBLE
  }

  fun hideOfferProductLoading() {
    binding.offerProductsProgressBar.visibility = View.GONE
  }

  fun showBestProductLoading() {
    binding.bestProductsProgressBar.visibility = View.VISIBLE
  }

  fun hideBestProductLoading() {
    binding.bestProductsProgressBar.visibility = View.GONE
  }

  open fun onOfferPagingRequest() {

  }

  open fun onBestProductPagingRequest() {

  }

  private fun setUpBestProductRv() {
    binding.rvBestProducts.apply {
      layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
      adapter = bestProductAdapter
    }
  }

  private fun setUpRvOffer() {
    binding.offerRv.apply {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
      adapter = rvOfferAdapter
    }
  }

  override fun onResume() {
    super.onResume()
    showBottomNavigationView()
  }
}