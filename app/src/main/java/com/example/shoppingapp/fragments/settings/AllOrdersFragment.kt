package com.example.shoppingapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingapp.adapters.AllOrdersAdapter
import com.example.shoppingapp.databinding.FragmentOrdersBinding
import com.example.shoppingapp.mvvm.factory.AllOrderViewModel
import com.example.shoppingapp.util.Resources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {

  private lateinit var binding: FragmentOrdersBinding
  private val viewModel by viewModels<AllOrderViewModel>()
  val orderAdapter by lazy {
    AllOrdersAdapter()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentOrdersBinding.inflate(layoutInflater)
    setUpRecyclerView()
    return binding.root
  }

  private fun setUpRecyclerView() {
    binding.rvAllOrders.apply {
      adapter = orderAdapter
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }


  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpRecyclerView()
    lifecycleScope.launchWhenStarted {
      viewModel.allOrders.collectLatest {
        when (it) {
          is Resources.Loading -> {
           binding.progressbarAllOrders.visibility = View.VISIBLE
          }

          is Resources.Success -> {
            binding.progressbarAllOrders.visibility = View.GONE
            orderAdapter.differ.submitList(it.data)
            if(it.data.isNullOrEmpty())
            {
              binding.tvEmptyOrders.visibility = View.VISIBLE
            }

          }

          is Resources.Error -> {
            binding.progressbarAllOrders.visibility = View.GONE
            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }
    orderAdapter.onClick = {
      val action = AllOrdersFragmentDirections.actionAllOrdersFragmentToOrderDetailsFragment(it)
      findNavController().navigate(action)
    }

  }
}