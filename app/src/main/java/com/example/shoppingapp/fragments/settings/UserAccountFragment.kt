package com.example.shoppingapp.fragments.settings

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingapp.data.Users
import com.example.shoppingapp.databinding.FragmentUserAccountBinding
import com.example.shoppingapp.helper.setUpBottomSheetDialog
import com.example.shoppingapp.mvvm.UserAccountViewModel
import com.example.shoppingapp.util.Resources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

  private lateinit var binding: FragmentUserAccountBinding
  private val viewModel by viewModels<UserAccountViewModel>()
  private var imageUri: Uri? = null
  private lateinit var imageActivityResultLauncher:ActivityResultLauncher<Intent>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
      imageUri = it.data?.data
      Glide.with(this).load(imageUri).into(binding.imageUser)
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launchWhenStarted {
      viewModel.user.collectLatest {
        when (it) {
          is Resources.Success -> {
            hideUserLoading()
            showUserDetails(it.data!!)
          }

          is Resources.Loading -> {
            showUserLoading()
          }

          is Resources.Error -> {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }
    lifecycleScope.launchWhenStarted {
      viewModel.updateInfo.collectLatest {
        when (it) {
          is Resources.Loading -> {
            binding.buttonSave.startAnimation()
          }

          is Resources.Success -> {
            binding.buttonSave.revertAnimation()
            findNavController().navigateUp()
          }

          is Resources.Error -> {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
          }

          else -> {}
        }
      }
    }
    binding.tvUpdatePassword.setOnClickListener {
      setUpBottomSheetDialog{

      }
    }
    binding.buttonSave.setOnClickListener {
      binding.apply {
        val firstName = edFirstName.text.toString().trim()
        val lastName = edLastName.text.toString().trim()
        val email = edEmail.text.toString().trim()
        val user = Users(firstName, lastName, email, "")
        viewModel.updateUser(user, imageUri)
      }
    }
    binding.imageEdit.setOnClickListener {
      val intent = Intent(Intent.ACTION_GET_CONTENT)
      intent.type = "image/*"
      imageActivityResultLauncher.launch(intent)
    }

  }

  private fun showUserDetails(data: Users) {
    binding.apply {
      Glide.with(this@UserAccountFragment).load(data.filePath).into(imageUser)
      edEmail.setText(data.email)
      edFirstName.setText(data.firstName)
      edLastName.setText(data.lastName)
    }
  }

  private fun showUserLoading() {
    binding.progressbarAccount.visibility = View.VISIBLE
    binding.apply {
      imageUser.visibility = View.INVISIBLE
      edEmail.visibility = View.INVISIBLE
      edFirstName.visibility = View.INVISIBLE
      edLastName.visibility = View.INVISIBLE
      imageEdit.visibility = View.INVISIBLE
      tvUpdatePassword.visibility = View.INVISIBLE
      buttonSave.visibility = View.INVISIBLE
    }
  }

  private fun hideUserLoading() {
    binding.progressbarAccount.visibility = View.GONE
    binding.apply {
      imageUser.visibility = View.VISIBLE
      edEmail.visibility = View.VISIBLE
      edFirstName.visibility = View.VISIBLE
      edLastName.visibility = View.VISIBLE
      imageEdit.visibility = View.VISIBLE
      tvUpdatePassword.visibility = View.VISIBLE
      buttonSave.visibility = View.VISIBLE
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentUserAccountBinding.inflate(inflater)
    return binding.root
  }
}
