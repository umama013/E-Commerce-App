package com.example.shoppingapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingapp.R
import com.example.shoppingapp.data.Address
import com.example.shoppingapp.databinding.AddressRvItemBinding


class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

  inner class AddressViewHolder(val binding: AddressRvItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(address: Address, isSelected: Boolean) {
      binding.apply {
        buttonAddress.text = address.addressTitle
        if (isSelected) {
          buttonAddress.background =
            ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
        } else {
          buttonAddress.background =
            ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
        }
      }

    }
  }

  private val diffUtil = object : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
      return oldItem == newItem
    }

  }
  val differ = AsyncListDiffer(this@AddressAdapter, diffUtil)
  var selectedAddress = -1
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): AddressAdapter.AddressViewHolder {
    return AddressViewHolder(AddressRvItemBinding.inflate(LayoutInflater.from(parent.context)))
  }


  override fun onBindViewHolder(holder: AddressAdapter.AddressViewHolder, position: Int) {
    val address = differ.currentList[position]
    holder.bind(address, selectedAddress == position)
    holder.binding.buttonAddress.setOnClickListener {

      if (selectedAddress != position) {
        val previousSelected = selectedAddress
        selectedAddress = position
        notifyItemChanged(previousSelected)
        notifyItemChanged(selectedAddress)
        onClick?.invoke(address)
      }
      }
    }
init {
  differ.addListListener{ _,_->
    notifyItemChanged(selectedAddress)


  }

}

  override fun getItemCount(): Int {
    return differ.currentList.size
  }

  var onClick: ((Address) -> Unit)? = null
}