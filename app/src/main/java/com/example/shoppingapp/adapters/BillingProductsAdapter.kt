package com.example.shoppingapp.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.data.CartProduct
import com.example.shoppingapp.databinding.BillingProductsRvItemBinding
import com.example.shoppingapp.helper.getProductPrice

class BillingProductsAdapter :
  RecyclerView.Adapter<BillingProductsAdapter.BillingProductViewHolder>() {

  inner class BillingProductViewHolder(val binding: BillingProductsRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(product: CartProduct) {
      binding.apply {
        Glide.with(itemView).load(product.product.images[0]).into(imageCartProduct)
        tvProductCartName.text = product.product.name
        tvBillingProductQuantity.text = product.quantity.toString()
        val price = product.product.offerPercentage.getProductPrice(product.product.price)
        tvProductCartPrice.text = "$ ${String.format("%.2f", price)}"
        imageCartProductColor.setImageDrawable(
          ColorDrawable(
            product.selectedColor ?: Color.TRANSPARENT
          )
        )
        tvCartProductSize.text = product.selectedSize ?: "".also {
          imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
      }

    }

  }

  private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
    override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
      return oldItem.product == newItem.product
    }

    override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
      return oldItem.product == newItem.product
    }

  }
  val differ = AsyncListDiffer(this, diffUtil)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductViewHolder {
    return BillingProductViewHolder(BillingProductsRvItemBinding.inflate(LayoutInflater.from(parent.context)))
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  override fun onBindViewHolder(holder: BillingProductViewHolder, position: Int) {
    val product = differ.currentList[position]
    holder.bind(product)
  }

}