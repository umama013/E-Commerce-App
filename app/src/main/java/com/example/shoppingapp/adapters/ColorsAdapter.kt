package com.example.shoppingapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.shoppingapp.databinding.ColorRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsAdapterViewHolder>() {

  private var selectedPosition = -1

  inner class ColorsAdapterViewHolder(private val binding: ColorRvItemBinding) :
    ViewHolder(binding.root) {

    fun bind(color: Int, position: Int) {
      val imageDrawable = ColorDrawable(color)
      binding.colorImage.setImageDrawable(imageDrawable)
      if (position == selectedPosition) {
        binding.apply {
          imageShadow.visibility = View.VISIBLE
          selectedColor.visibility = View.VISIBLE
        }
      }
      else{
        binding.apply {
          imageShadow.visibility = View.GONE
          selectedColor.visibility = View.GONE
        }
      }
    }
  }

  private val diffUtilCallBack = object : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
      return oldItem == newItem
    }

  }
  val differ = AsyncListDiffer(this, diffUtilCallBack)
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsAdapterViewHolder {
    return ColorsAdapterViewHolder(
      ColorRvItemBinding.inflate(LayoutInflater.from(parent.context))
    )

  }

  override fun getItemCount(): Int {
   return differ.currentList.size
  }

  override fun onBindViewHolder(holder: ColorsAdapterViewHolder, position: Int) {
    val color = differ.currentList[position]
    holder.bind(color, position)
    holder.itemView.setOnClickListener {
      if(selectedPosition>=0)
        notifyItemChanged(selectedPosition)
      selectedPosition = holder.adapterPosition
      notifyItemChanged(selectedPosition)
      onItemClick?.invoke(color)
    }

  }
 var onItemClick:((Int) ->Unit)? = null
}