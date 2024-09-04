package com.example.shoppingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.databinding.ProductRvItemBinding
import com.example.shoppingapp.helper.getProductPrice

class BestProductAdapter : RecyclerView.Adapter<BestProductAdapter.BestProductViewHolder>() {

  inner class BestProductViewHolder(private val binding: ProductRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product) {
      binding.apply {
        Glide.with(itemView).load(product.images[0]).into(imgProduct)
        tvName.text = product.name
        val priceAfterOffer = product.offerPercentage.getProductPrice(product.price)
        tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"

        if (product.offerPercentage == null)
          tvNewPrice.visibility = View.GONE
      }
    }
  }

  private val diffUtilCallBack = object : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
      return oldItem == newItem
    }

  }
  val differ = AsyncListDiffer(this, diffUtilCallBack)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
    return BestProductViewHolder(
      ProductRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
    val product = differ.currentList[position]
    holder.bind(product)
    holder.itemView.setOnClickListener {
      onClick?.invoke(product)
    }

  }

  var onClick: ((Product) -> Unit)? = null
}