package com.example.shoppingapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizeViewHolder>() {

  var selectedPosition = -1

  inner class SizeViewHolder(val binding: SizeRvItemBinding) : ViewHolder(binding.root) {

    fun bind(size: String, position: Int) {
      binding.selectedSize.text = size
      if (position == selectedPosition) {
        binding.apply {
          imageShadow.visibility = View.VISIBLE
        }
      } else {
        binding.apply {
          imageShadow.visibility = View.GONE
        }
      }
    }
  }

  private val diffUtilCallBack = object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
      return oldItem == newItem
    }

  }
  val differ = AsyncListDiffer(this, diffUtilCallBack)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
    return SizeViewHolder(
      SizeRvItemBinding.inflate(LayoutInflater.from(parent.context))
    )
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
    val size = differ.currentList[position]
    holder.bind(size, position)
    holder.itemView.setOnClickListener {
      if (selectedPosition >= 0)
        notifyItemChanged(selectedPosition)
      selectedPosition = holder.adapterPosition
      notifyItemChanged(selectedPosition)
      onItemClick?.invoke(size)
    }

  }

   var onItemClick: ((String) -> Unit)? = null
}
