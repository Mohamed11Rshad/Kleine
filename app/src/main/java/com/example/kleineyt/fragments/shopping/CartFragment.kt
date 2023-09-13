package com.example.kleineyt.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kleineyt.R
import com.example.kleineyt.adapters.CartProductAdapter
import com.example.kleineyt.databinding.FragmentCartBinding
import com.example.kleineyt.firebase.FirebaseCommon
import com.example.kleineyt.util.Resource
import com.example.kleineyt.util.VerticalItemDecoration
import com.example.kleineyt.util.formatPrice
import com.example.kleineyt.util.hideNavigationView
import com.example.kleineyt.util.showNavigationView
import com.example.kleineyt.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment(R.layout.fragment_cart){

    private lateinit var binding : FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var totalPrice = 0f

        setupCartRv()



        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest {price ->
                price?.let{
                    totalPrice = it
                    binding.tvTotalPrice.text = it.formatPrice()
                }

            }
        }

        cartAdapter.onProductClick = {
            val b = Bundle().apply{putParcelable("product" , it.product)}
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment , b)
        }


        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it , FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it , FirebaseCommon.QuantityChanging.DECREASE)
        }



        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.observe(viewLifecycleOwner) {
                if(it!=null) {
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete item from cart")
                        setMessage("Are you sure you want to delete this item from cart?")
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            viewModel.makeDeleteDialogNull()
                        }
                        setPositiveButton("Delete") { dialog, _ ->
                            viewModel.deleteProduct(it)
                            dialog.dismiss()
                            viewModel.makeDeleteDialogNull()
                        }
                    }.create()
                    alertDialog.show()
                }

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        }else{
                            hideEmptyCart()
                            cartAdapter.differ.submitList(it.data)
                            showOtherViews()
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.buttonCheckout.setOnClickListener{
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray(),true)
            findNavController().navigate(action)
        }

    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }

    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext() , RecyclerView.VERTICAL , false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }

    }

    override fun onResume() {
        super.onResume()
        showNavigationView()
    }

}