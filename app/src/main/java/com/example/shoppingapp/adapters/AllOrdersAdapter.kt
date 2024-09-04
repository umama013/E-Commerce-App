package com.example.shoppingapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.R
import com.example.shoppingapp.data.order.Order
import com.example.shoppingapp.data.order.OrderStatus
import com.example.shoppingapp.data.order.getOrderStatus
import com.example.shoppingapp.databinding.OrderItemBinding

class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {
  inner class OrdersViewHolder(private val binding: OrderItemBinding) : ViewHolder(binding.root) {

    fun bind(order: Order) {
      binding.apply {
        tvOrderDate.text = order.date
        tvOrderId.text = order.orderId.toString()
        val resources = itemView.resources

        val colorDrawable = when(getOrderStatus(order.orderStatus)){
          is OrderStatus.Ordered ->{
             ColorDrawable(resources.getColor(R.color.g_orange_yellow))
          }
          is OrderStatus.Confirmed ->{
            ColorDrawable(resources.getColor(R.color.green))
          }
          is OrderStatus.Shipped ->{
            ColorDrawable(resources.getColor(R.color.green))
          }
          is OrderStatus.Delivered ->{
            ColorDrawable(resources.getColor(R.color.green))
          }
          is OrderStatus.Canceled ->{
            ColorDrawable(resources.getColor(R.color.g_red))
          }

          else -> {
            ColorDrawable(resources.getColor(R.color.g_red))
          }
        }
        imageOrderState.setImageDrawable(colorDrawable)
      }

    }
  }

  private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
      return oldItem.products == newItem.products
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
      return oldItem == newItem
    }

  }
  val differ = AsyncListDiffer(this, diffUtil)
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
    return OrdersViewHolder(
      OrderItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      )
    )
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
    val order = differ.currentList[position]
    holder.bind(order)
    holder.itemView.setOnClickListener {
      onClick?.invoke(order)
    }
  }

  var onClick: ((Order) -> Unit)? = null
}