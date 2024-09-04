package com.example.shoppingapp.shoppingActivityFragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.adapters.CartProductAdapters
import com.example.shoppingapp.databinding.FragmentCartBinding
import com.example.shoppingapp.firebase.FirebaseCommons
import com.example.shoppingapp.mvvm.CartViewModel
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.VerticalItemDecoration
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment(R.layout.fragment_cart) {

  private lateinit var binding: FragmentCartBinding
  private val cartAdapter by lazy {
    CartProductAdapters()
  }
  private val viewModels by activityViewModels<CartViewModel>()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentCartBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpRecyclerView()
    var totalPrice = 0f
    lifecycleScope.launchWhenStarted {
      viewModels.productPrice.collectLatest { price ->
        price?.let {
          totalPrice = it
          binding.tvTotalPrice.text = "$ $price"
        }
      }
    }
    cartAdapter.onCartProductClick = {
      val b = Bundle().apply {
        putParcelable("product", it.product)
      }
      findNavController().navigate(R.id.action_cartFragment_to_productsDetailsFragment, b)
    }


    binding.btnCheckout.setOnClickListener {
      val action = CartFragmentDirections.actionCartFragmentToBillingFragment(
        totalPrice,
        cartAdapter.differ.currentList.toTypedArray(),true
      )
      findNavController().navigate(action)
    }
    cartAdapter.onPlusClick = {
      viewModels.updateQuantity(it, FirebaseCommons.QuantityChanging.INCREASE)
    }

    cartAdapter.onMinusClick = {
      viewModels.updateQuantity(it, FirebaseCommons.QuantityChanging.DECREASE)
    }
    lifecycleScope.launchWhenStarted {
      viewModels.deleteDialog.collectLatest {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
          setTitle("Delete item from cart")
          setMessage("Do you want to delete the item from your cart")
          setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
          }
          setPositiveButton("Yes") { dialog, _ ->
            viewModels.deleteCartProduct(it)
            dialog.dismiss()
          }
        }
        alertDialog.create()
        alertDialog.show()
      }
    }
    lifecycleScope.launchWhenStarted {
      viewModels.cartProducts.collectLatest {
        when (it) {
          is Resources.Loading -> {
            binding.cartProgressBar.visibility = View.VISIBLE
          }

          is Resources.Success -> {
            binding.cartProgressBar.visibility = View.INVISIBLE
            if (it.data!!.isEmpty()) {
              showEmptyCart()
              hideOtherViews()
            } else {
              hideEmtyCart()
              showOtherViews()
              cartAdapter.differ.submitList(it.data)
            }

          }

          is Resources.Error -> {
            binding.cartProgressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }

  }

  private fun showOtherViews() {
    binding.apply {
      rvCart.visibility = View.VISIBLE
      footer.visibility = View.VISIBLE
      btnCheckout.visibility = View.VISIBLE
    }
  }

  private fun hideOtherViews() {
    binding.apply {
      rvCart.visibility = View.GONE
      footer.visibility = View.GONE
      btnCheckout.visibility = View.GONE
    }

  }

  private fun hideEmtyCart() {
    binding.emptyCartLayout.visibility = View.GONE
  }

  private fun showEmptyCart() {
    binding.emptyCartLayout.visibility = View.VISIBLE
  }

  private fun setUpRecyclerView() {
    binding.rvCart.apply {
      layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
      adapter = cartAdapter
      addItemDecoration(VerticalItemDecoration())
    }

  }
}