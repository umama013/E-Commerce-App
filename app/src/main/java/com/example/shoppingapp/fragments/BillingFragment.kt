package com.example.shoppingapp.fragments

import android.app.AlertDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.adapters.AddressAdapter
import com.example.shoppingapp.adapters.BillingProductsAdapter
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.data.CartProduct
import com.example.shoppingapp.data.order.Order
import com.example.shoppingapp.data.order.OrderStatus
import com.example.shoppingapp.databinding.FragmentBillingBinding
import com.example.shoppingapp.mvvm.BillingViewModel
import com.example.shoppingapp.mvvm.OrderViewModel
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.horizontalItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment : Fragment(R.layout.fragment_billing) {

  private lateinit var binding: FragmentBillingBinding
  private val addressAdapter by lazy {
    AddressAdapter()
  }
  private val billingProductsAdapter by lazy {
    BillingProductsAdapter()
  }
  private var selectedAddress: Address? = null
  private val orderViewModel by viewModels<OrderViewModel>()
  private val billingViewModel by viewModels<BillingViewModel>()
  private val args by navArgs<BillingFragmentArgs>()
  private var products = emptyList<CartProduct>()
  private var totalPrice = 0f

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    products = args.cartProducts.asList()
    totalPrice = args.TotalPrice

  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentBillingBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpAddressRv()
    setUpCartProductsRv()

    if(!args.payment){
      binding.apply {
        buttonPlaceOrder.visibility = View.INVISIBLE
        totalBoxContainer.visibility = View.INVISIBLE
        middleLine.visibility = View.INVISIBLE
        bottomLine.visibility = View.INVISIBLE
      }
    }




    lifecycleScope.launchWhenStarted {
      billingViewModel.getAddresses.collectLatest {
        when (it) {
          is Resources.Loading -> {
            binding.progressbarAddress.visibility = View.VISIBLE
          }

          is Resources.Success -> {
            binding.progressbarAddress.visibility = View.GONE
            addressAdapter.differ.submitList(it.data)
          }

          is Resources.Error -> {
            binding.progressbarAddress.visibility = View.GONE
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }

    lifecycleScope.launchWhenStarted {
      orderViewModel.order.collectLatest {
        when (it) {
          is Resources.Loading -> {
            binding.buttonPlaceOrder.startAnimation()
          }

          is Resources.Success -> {
            binding.buttonPlaceOrder.revertAnimation()
            Toast.makeText(requireContext(), "Your order is placed", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
          }

          else -> {}
        }
      }
    }
    billingProductsAdapter.differ.submitList(products)
    binding.tvTotalPrice.text = "$ $totalPrice"
    binding.imageAddAddress.setOnClickListener {
      findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
    }
    binding.imageCloseBilling.setOnClickListener {
      findNavController().popBackStack()
    }


    addressAdapter.onClick =
      {
        selectedAddress = it
        if(!args.payment){
          val b = Bundle().apply {
            putParcelable("address",it)
          }
          findNavController().navigate(R.id.action_billingFragment_to_addressFragment,b)
        }
      }
    binding.buttonPlaceOrder.setOnClickListener {
      if (selectedAddress == null) {
        Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_LONG).show()
        return@setOnClickListener
      }
      showOrderConfirmationDialog()
    }

  }

  private fun showOrderConfirmationDialog() {
    val alertDialog = AlertDialog.Builder(requireContext()).apply {
      setTitle("Place Order")
      setMessage("Do you want to order the cart products")
      setPositiveButton("Yes") { dialog, _ ->
        val order = Order(
          OrderStatus.Ordered.status,
          totalPrice,
          products,
          selectedAddress!!
        )
        orderViewModel.placeOrder(order)
        dialog.dismiss()
      }
      setNegativeButton("Cancel") { dialog, _ ->
        dialog.dismiss()
      }
    }
    alertDialog.create()
    alertDialog.show()
  }

  private fun setUpAddressRv() {
    binding.rvAddress.apply {
      layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
      adapter = addressAdapter
      addItemDecoration(horizontalItemDecoration())
    }
  }

  private fun setUpCartProductsRv() {
    binding.rvProducts.apply {
      layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
      adapter = billingProductsAdapter
      addItemDecoration(horizontalItemDecoration())
    }

  }

}