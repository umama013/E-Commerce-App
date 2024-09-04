package com.example.shoppingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingapp.data.Product
import com.example.shoppingapp.databinding.BestDealsRvItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

  inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) :
    RecyclerView.ViewHolder(binding.root){
      fun bind(product: Product){
        binding.apply {
          Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
          tvDealProductName.text = product.name
          product.offerPercentage?.let {
            val leftPercent = 1f - product.offerPercentage
            val priceAfterOffer = leftPercent * product.price
            tvNewPrice.text ="$ ${String.format("%.2f",priceAfterOffer)}"
          }
          tvOldPrice.text = product.price.toString()
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

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
   return BestDealsViewHolder(
     BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
  }

  override fun getItemCount(): Int {
  return differ.currentList.size
  }

  override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
    val product = differ.currentList[position]
    holder.bind(product)
  //  holder.itemView.setOnClickListener {
      holder.itemView.setOnClickListener {
        onClick?.invoke(product)
      }
    }


  var onClick:((Product)->Unit)? = null
}