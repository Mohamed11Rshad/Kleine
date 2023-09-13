package com.example.kleineyt.fragments.shopping

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
import com.example.kleineyt.data.Address
import com.example.kleineyt.databinding.FragmentAddressBinding
import com.example.kleineyt.util.Resource
import com.example.kleineyt.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment() {

 private lateinit var binding: FragmentAddressBinding
 private val viewModel by viewModels<AddressViewModel>()
 val args by navArgs<AddressFragmentArgs>()

     override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
     ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
     }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteAddress.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
                binding.progressbarAddress.visibility = View.INVISIBLE
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = args.address
        if(address == null){
            binding.apply {
                buttonDelelte.visibility = View.GONE
            }
        }else{
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edStreet.setText(address.street)
                edPhone.setText(address.phone)
                edCity.setText(address.city)
                edState.setText(address.state)


            }
        }


        binding.apply {
            buttonSave.setOnClickListener{
                val addressTitle = binding.edAddressTitle.text.toString()
                val fullName = binding.edFullName.text.toString()
                val street = binding.edStreet.text.toString()
                val phone = binding.edPhone.text.toString()
                val city = binding.edCity.text.toString()
                val state = binding.edState.text.toString()

                val address = Address(addressTitle,fullName,street,phone,city,state)
                viewModel.AddAddress(address)

            }
        }

        binding.buttonDelelte.setOnClickListener {
            val address = args.address
            if(address != null){
                viewModel.deleteAddress(address)
            }
        }

        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }




    }



}