package com.example.shoppingapp.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.shoppingapp.data.Categories
import com.example.shoppingapp.mvvm.CategoryViewModel
import com.example.shoppingapp.mvvm.factory.BaseCategoryViewModelFactory
import com.example.shoppingapp.util.Resources
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FreshProduceFragment : BaseCategoryFragment() {

  @Inject
  lateinit var firestore: FirebaseFirestore
  val viewModel by viewModels<CategoryViewModel> {
    BaseCategoryViewModelFactory(firestore,Categories.Fresh_Produce)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    lifecycleScope.launchWhenStarted {
      viewModel.offerProducts.collectLatest {
        when(it){
          is Resources.Loading->{
           showOfferProductLoading()
          }
          is Resources.Success->{
            rvOfferAdapter.differ.submitList(it.data)
            hideOfferProductLoading()
          }
          is Resources.Error->{
          Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).show()
            hideOfferProductLoading()
          }

          else -> {}
        }

      }
    }
    lifecycleScope.launchWhenStarted {
      viewModel.bestProducts.collectLatest {
        when(it){
          is Resources.Loading->{
          showBestProductLoading()
          }
          is Resources.Success->{
            bestProductAdapter.differ.submitList(it.data)
            hideBestProductLoading()
          }
          is Resources.Error->{
            Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_LONG).show()
            hideBestProductLoading()
          }

          else -> {}
        }

      }
    }

  }
}