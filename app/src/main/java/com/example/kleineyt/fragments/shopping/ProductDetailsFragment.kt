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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kleineyt.adapters.ColorsAdapter
import com.example.kleineyt.adapters.SizesAdapter
import com.example.kleineyt.adapters.ViewPager2Images
import com.example.kleineyt.data.CartProduct
import com.example.kleineyt.databinding.FragmentProductDetailsBinding
import com.example.kleineyt.helper.getProductPrice
import com.example.kleineyt.util.Resource
import com.example.kleineyt.util.formatPrice
import com.example.kleineyt.util.hideNavigationView
import com.example.kleineyt.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment(){
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding : FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor : Int? = null
    private var selectedSize : String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewPager()

        binding.apply {

            imageClose.setOnClickListener { findNavController().navigateUp() }

            sizesAdapter.onItemClick = {size ->
                selectedSize = size
            }

            colorsAdapter.onItemClick = {color ->
                selectedColor = color
            }

            binding.buttonAddToCart.setOnClickListener {
                viewModel.addUpdateProductInCart(CartProduct(product,1 , selectedColor , selectedSize))
            }

            lifecycleScope.launchWhenStarted {
                viewModel.addToCart.collectLatest {
                    when(it){
                        is Resource.Loading -> {
                            binding.buttonAddToCart.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.buttonAddToCart.revertAnimation()
                            Toast.makeText(requireContext(), "Product Added", Toast.LENGTH_SHORT).show()

                        }
                        is Resource.Error -> {
                            binding.buttonAddToCart.revertAnimation()
                            Snackbar.make(requireView(), "Failed To Add the product", Snackbar.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }

            tvProductName.text = product.name
            tvProductPrice.text = product.offerPercentage.getProductPrice(product.price).formatPrice()
            tvProductDescription.text = product.description

            if(product.colors.isNullOrEmpty()){
                tvProductColors.visibility = View.INVISIBLE
            }

            if(product.sizes.isNullOrEmpty()){
                tvProductSizes.visibility = View.INVISIBLE
            }

        }

        viewPagerAdapter.differ.submitList(product.images)
        sizesAdapter.differ.submitList(product.sizes)
        colorsAdapter.differ.submitList(product.colors)
        product.colors?.let { colorsAdapter.differ.submitList(it) }
        product.sizes?.let { sizesAdapter.differ.submitList(it) }



    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
            TabLayoutMediator(indicator,viewPagerProductImages){tab,position ->

            }.attach()
        }
    }

    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
        }
    }

}