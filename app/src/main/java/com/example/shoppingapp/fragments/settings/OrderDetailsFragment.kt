package com.example.shoppingapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.adapters.BillingProductsAdapter
import com.example.shoppingapp.data.order.OrderStatus
import com.example.shoppingapp.data.order.getOrderStatus
import com.example.shoppingapp.databinding.FragmentOrderDetailBinding
import com.example.shoppingapp.util.VerticalItemDecoration
import hilt_aggregated_deps._com_example_shoppingapp_mvvm_CartViewModel_HiltModules_BindsModule

class OrderDetailsFragment : Fragment() {

  private lateinit var binding: FragmentOrderDetailBinding
   private val billingAdapter by lazy { BillingProductsAdapter() }
  private val args by navArgs<OrderDetailsFragmentArgs>()
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentOrderDetailBinding.inflate(layoutInflater)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val order = args.order

    binding.apply {

    tvOrderId.text = "Order #${order.orderId}"

      stepView.setSteps(
        mutableListOf(
          OrderStatus.Ordered.status,
          OrderStatus.Confirmed.status,
          OrderStatus.Shipped.status,
          OrderStatus.Delivered.status
        )
      )
       val currentOrderStatus = when(getOrderStatus(order.orderStatus)){
         is OrderStatus.Ordered ->0
         is OrderStatus.Confirmed ->1
         is OrderStatus.Shipped -> 2
         is OrderStatus.Delivered -> 3
         else -> 0
       }
      stepView.go(currentOrderStatus,false)
      if(currentOrderStatus == 3)
      {
        stepView.done(true)
      }
      tvAddress.text = "${order.address.street} ${order.address.city}"
      tvFullName.text = order.address.fullName
      tvPhoneNumber.text = order.address.phone
      tvTotalPrice.text = order.totalPrice.toString()


     billingAdapter.differ.submitList(order.products)

    }

    setUpRecyclerView()
  }

  private fun setUpRecyclerView() {
    binding.rvProducts.apply {
      adapter = billingAdapter
      layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
      addItemDecoration(VerticalItemDecoration())
    }
  }
}