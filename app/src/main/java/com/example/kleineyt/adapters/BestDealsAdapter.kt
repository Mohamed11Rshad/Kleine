package com.example.kleineyt.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kleineyt.data.Product
import com.example.kleineyt.databinding.BestDealsRvItemBinding
import com.example.kleineyt.util.formatPrice

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHoler>() {

    inner class BestDealsViewHoler(val binding: BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                product.offerPercentage?.let {
                    val priceAfterOffer = ((1f - it) * product.price).formatPrice()
                    tvNewPrice.text = priceAfterOffer
                    tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                    tvOldPrice.text = product.price.formatPrice()
                    tvDealProductName.text = product.name

            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHoler {
        return BestDealsViewHoler(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHoler, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }

        holder.binding.btnSeeProduct.setOnClickListener {
            onSeeProductClick?.invoke(product)
        }
    }

    var onClick : ((Product) -> Unit)? = null

    var onSeeProductClick : ((Product) -> Unit)? = null


}