package com.example.shoppingapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.shoppingapp.databinding.ViewPagerImagesBinding

class ViewPager2Images : RecyclerView.Adapter<ViewPager2Images.ViewPager2ImagesViewHolder>() {

  inner class ViewPager2ImagesViewHolder(val binding: ViewPagerImagesBinding) :
    ViewHolder(binding.root) {

    fun bind(imagePath: String) {
      Glide.with(itemView).load(imagePath).into(binding.ivProductDetails)

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

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImagesViewHolder {
    return ViewPager2ImagesViewHolder(
      ViewPagerImagesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    )
  }

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  override fun onBindViewHolder(holder: ViewPager2ImagesViewHolder, position: Int) {
    val image = differ.currentList[position]
    holder.bind(image)
  }
}