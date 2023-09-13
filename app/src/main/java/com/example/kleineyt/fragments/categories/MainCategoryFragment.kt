package com.example.kleineyt.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kleineyt.R
import com.example.kleineyt.adapters.BestDealsAdapter
import com.example.kleineyt.adapters.BestProductsAdapter
import com.example.kleineyt.adapters.SpecialProductsAdapter
import com.example.kleineyt.data.CartProduct
import com.example.kleineyt.databinding.FragmentMainCategoryBinding
import com.example.kleineyt.util.Resource
import com.example.kleineyt.util.showNavigationView
import com.example.kleineyt.viewmodel.MainCategoryViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


private val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category){
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()

        specialProductsAdapter.onClick = {
            val productBundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, productBundle)
        }

        specialProductsAdapter.onAddToCartClick = {
            viewModel.addToCart(CartProduct(it,1))
        }

        bestDealsAdapter.onClick = {
            val productBundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, productBundle)
        }

        bestDealsAdapter.onSeeProductClick = {
            val productBundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, productBundle)
        }

        bestProductsAdapter.onClick = {
            val productBundle = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, productBundle)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.specialProductsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        binding.specialProductsProgressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.specialProductsProgressBar.visibility = View.GONE
                        Log.e(TAG, it.message.toString())
                        error()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.bestDealsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        binding.bestDealsProgressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.bestDealsProgressBar.visibility = View.GONE
                        error()
                        Log.e(TAG, it.message.toString())
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.bestProductsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.bestProductsProgressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.bestProductsProgressBar.visibility = View.GONE
                        Log.e(TAG, it.message.toString())
                        error()
                    }
                    else -> Unit
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
                v, _, scrollY, _, _ ->
            if(v.getChildAt(0).bottom<= (v.height + scrollY)){
                viewModel.fetchBestProducts()
            }
        })

        binding.rvSpecialProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                    viewModel.fetchSpecialProducts()
                }
            }
        })

        binding.rvBestDealsProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollHorizontally(1) && dx != 0) {
                    viewModel.fetchBestDealsProducts()
                }
            }
        })


    }


    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

    private fun setupBestProductsRv() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2,LinearLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun error() {
        Snackbar.make(requireView(), "Check your connection", Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        showNavigationView()
    }


}