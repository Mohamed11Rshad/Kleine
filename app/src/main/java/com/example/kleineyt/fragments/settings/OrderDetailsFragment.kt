package com.example.kleineyt.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kleineyt.adapters.BillingProductsAdapter
import com.example.kleineyt.data.order.OrderStatus
import com.example.kleineyt.data.order.getOrderStatus
import com.example.kleineyt.databinding.FragmentOrderDetailBinding
import com.example.kleineyt.util.HorizontalItemDecoration
import com.example.kleineyt.util.VerticalItemDecoration
import com.example.kleineyt.util.formatPrice
import com.example.kleineyt.util.hideNavigationView

class OrderDetailsFragment : Fragment()  {

    lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order


        setupOrderRv()

        billingProductsAdapter.differ.submitList(order.products)

        binding.apply {

            tvOrderId.text = "Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,

                )
            )

            val currentOrderStatus = when(getOrderStatus(order.orderStatus)){
                OrderStatus.Ordered -> 0
                OrderStatus.Confirmed -> 1
                OrderStatus.Shipped -> 2
                OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderStatus,true)

            if(currentOrderStatus == 3) {
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.state} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = order.totalPrice.formatPrice()

            imageCloseOrder.setOnClickListener { findNavController().navigateUp() }

        }

    }

    private fun setupOrderRv() {

        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
            addItemDecoration(HorizontalItemDecoration())
        }



    }

}