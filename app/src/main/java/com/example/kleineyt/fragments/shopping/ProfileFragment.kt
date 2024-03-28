package com.example.kleineyt.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import br.com.simplepass.loadingbutton.BuildConfig
import com.bumptech.glide.Glide
import com.example.kleineyt.R
import com.example.kleineyt.activities.LoginRegisterActivity
import com.example.kleineyt.databinding.FragmentProfileBinding
import com.example.kleineyt.util.Resource
import com.example.kleineyt.util.showNavigationView
import com.example.kleineyt.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment(){

    lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        // passing boolean variable by bundle to All orders fragment
        // to determine either you are here to say order details or track it

        binding.linearTrackOrder.setOnClickListener {

            // using bundle to put variable value and then pass it via navController
            val bundle = Bundle().apply {
                putBoolean("showDetails", false)
            }
            findNavController().navigate(
                R.id.action_profileFragment_to_ordersFragment,
                bundle
            )
        }

        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f,
                emptyArray(),false
            )

            findNavController().navigate(action)

        }

        binding.linearLogOut.setOnClickListener {
            onLogoutClick()
        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

        lifecycleScope.launchWhenStarted {

            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(
                            Color.BLACK)).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }

        }


    }

    override fun onResume() {
        super.onResume()

        showNavigationView()
    }


    private fun onLogoutClick() {
        binding.linearLogOut.setOnClickListener {
            viewModel.logOut()
            val intent = Intent(context, LoginRegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }


}