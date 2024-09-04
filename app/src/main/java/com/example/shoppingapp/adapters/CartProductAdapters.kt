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
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.databinding.CartProductRvItemBinding
import com.example.shoppingapp.helper.getProductPrice

class CartProductAdapters : RecyclerView.Adapter<CartProductAdapters.CartProductViewHolder>() {

  inner class CartProductViewHolder(val binding: CartProductRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(cartProduct: CartProduct) {
      binding.apply {
        Glide.with(itemView).load(cartProduct.product.images[0]).into(productImg)
        tvProductName.text = cartProduct.product.name
        productQuantity.text = cartProduct.quantity.toString()
        val price = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
        tvProductPrice.text = "$ ${String.format("%.2f", price)}"
        ivProductColor.setImageDrawable(
          ColorDrawable(
            cartProduct.selectedColor ?: Color.TRANSPARENT
          )
        )
        tvProductSize.text = cartProduct.selectedSize ?: "".also {
          ivProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        }
      }
    }
  }

  private val diffUtilCallBack = object : DiffUtil.ItemCallback<CartProduct>() {
    override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
      return oldItem.product.id == newItem.product.id
    }

    override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
      return oldItem == newItem
    }

  }
  val differ = AsyncListDiffer(this, diffUtilCallBack)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
    return CartProductViewHolder(CartProductRvItemBinding.inflate(LayoutInflater.from(parent.context)))
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
    val product = differ.currentList[position]
    holder.bind(product)
    holder.itemView.setOnClickListener {
      onCartProductClick?.invoke(product)
    }
    holder.binding.ivMinus.setOnClickListener {
      onMinusClick?.invoke(product)
    }
    holder.binding.icPlus.setOnClickListener {
      onPlusClick?.invoke(product)
    }
  }

  var onCartProductClick: ((CartProduct) -> Unit)? = null
  var onPlusClick: ((CartProduct) -> Unit)? = null
  var onMinusClick: ((CartProduct) -> Unit)? = null

}