package com.example.kleineyt.fragments.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kleineyt.adapters.AllOrdersAdapter
import com.example.kleineyt.data.order.OrderStatus
import com.example.kleineyt.databinding.FragmentOrdersBinding
import com.example.kleineyt.util.Resource
import com.example.kleineyt.util.hideNavigationView
import com.example.kleineyt.viewmodel.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    val viewModel by viewModels<AllOrdersViewModel>()
    val allOrdersAdapter by lazy { AllOrdersAdapter() }

    // the variable that get args from the bundle
    private val args: AllOrdersFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationView()

        setupOrdersRv()

        lifecycleScope.launchWhenCreated {
            viewModel.allOrders.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        allOrdersAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        allOrdersAdapter.onClick = {
            // the boolean variable that determine why we are in All orders fragments
            val showDetails = args.showDetails

            if (showDetails) {
                val action =
                    AllOrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(it)
                findNavController().navigate(action)
            } else {
                if (it.orderStatus != OrderStatus.Cancelled.status
                    && it.orderStatus != OrderStatus.Returned.status
                    && it.orderStatus != OrderStatus.Delivered.status
                        ){
                    if (it.address.city.isNotBlank()) {
                        showDirection("Cairo-Egypt", it.address.city + "-" + it.address.state)
                    }
                }
                else{
                    Toast.makeText(requireContext(), "No Track found for this order", Toast.LENGTH_SHORT).show()
                }


            }

        }

        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setupOrdersRv() {

        binding.rvAllOrders.apply {
            adapter = allOrdersAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    // redirecting you to google maps app and show direction
    // if you don't have google maps app you will be directed to google play so you can download google maps app
    private fun showDirection(from: String, to: String) {
        try {
            val uri: Uri = Uri.parse("https://www.google.com/maps/dir/$from/$to")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }catch (e: ActivityNotFoundException){
            val uri: Uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

}