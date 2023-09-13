package com.example.kleineyt.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kleineyt.data.CartProduct
import com.example.kleineyt.databinding.BillingProductsRvItemBinding
import com.example.kleineyt.helper.getProductPrice
import com.example.kleineyt.util.formatPrice

class BillingProductsAdapter : RecyclerView.Adapter<BillingProductsAdapter.BillingProductsViewHolder>(){

    inner class BillingProductsViewHolder(val binding : BillingProductsRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(product.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = product.product.name
                tvBillingProductQuantity.text = product.quantity.toString()

                val priceAfterOffer = product.product.offerPercentage.getProductPrice(product.product.price)
                tvProductCartPrice.text = priceAfterOffer.formatPrice()

                imageCartProductColor.setImageDrawable(ColorDrawable(product.selectedColor?: Color.TRANSPARENT))
                tvCartProductSize.text = product.selectedSize?:"".also { imageCartProductSize.setImageDrawable(
                    ColorDrawable(Color.TRANSPARENT)
                ) }


            }

        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(BillingProductsRvItemBinding
            .inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

}