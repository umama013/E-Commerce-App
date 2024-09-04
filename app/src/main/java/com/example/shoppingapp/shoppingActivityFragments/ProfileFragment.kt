package com.example.shoppingapp.shoppingActivityFragments

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingapp.R
import com.example.shoppingapp.activities.LoginAndRegisterActivity
import com.example.shoppingapp.databinding.FragmentProfileBinding
import com.example.shoppingapp.mvvm.ProfileViewModel
import com.example.shoppingapp.util.Resources
import com.example.shoppingapp.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.grpc.android.BuildConfig
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ProfileFragment : Fragment() {

  private lateinit var binding : FragmentProfileBinding
 private val viewModel by viewModels<ProfileViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentProfileBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.constraintProfile.setOnClickListener {
      findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)

    }
    binding.linearAllOrders.setOnClickListener {
      findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
    }
    binding.linearBilling.setOnClickListener {
     val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f,
       emptyArray(),false
     )
      findNavController().navigate(action)
    }
    binding.linearLogOut.setOnClickListener {
     viewModel.logout()
      val intent = Intent(requireActivity(),LoginAndRegisterActivity::class.java)
      startActivity(intent)
      requireActivity().finish()
    }
    binding.tvVersion.text = "Version ${com.google.firebase.BuildConfig.VERSION_NAME}"


    lifecycleScope.launchWhenStarted {
      viewModel.user.collectLatest {
        when(it){
          is Resources.Loading ->{
            binding.progressbarSettings.visibility = View.VISIBLE
          }
          is Resources.Success ->{
            binding.progressbarSettings.visibility = View.GONE
            Glide.with(requireView()).load(it.data!!.filePath).error(ColorDrawable(android.graphics.Color.BLACK)).into(binding.imageUser)

            binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"


          }
          is Resources.Error ->{
            binding.progressbarSettings.visibility = View.GONE
            Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    showBottomNavigationView()
  }

}