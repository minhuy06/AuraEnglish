package com.example.auraenglish.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.data.DashStoreItem
import com.example.auraenglish.databinding.ItemDashboardStoreBinding

// Adapter cửa hàng bộ đề
class DashboardStoreAdapter(
    private val onAction: (DashStoreItem) -> Unit
) : ListAdapter<DashStoreItem, DashboardStoreAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<DashStoreItem>() {
        override fun areItemsTheSame(oldItem: DashStoreItem, newItem: DashStoreItem) =
            oldItem.index == newItem.index

        override fun areContentsTheSame(oldItem: DashStoreItem, newItem: DashStoreItem) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDashboardStoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onAction)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemDashboardStoreBinding,
        private val onAction: (DashStoreItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashStoreItem) = with(binding) {
            val ctx = root.context
            tvIndex.text = item.index
            tvTitle.text = item.title
            tvMeta.text = item.meta
            if (item.owned) {
                tvStatus.text = ctx.getString(R.string.dash_store_owned)
                tvStatus.setBackgroundResource(R.drawable.bg_dash_owned_pill)
                tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.dash_owned_text))
                btnAction.text = ctx.getString(R.string.dash_practice_cta)
            } else {
                tvStatus.text = ctx.getString(R.string.dash_price_fmt, item.price)
                tvStatus.setBackgroundResource(android.R.color.transparent)
                tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.dash_primary_mid))
                btnAction.text = ctx.getString(R.string.dash_buy)
            }
            btnAction.setOnClickListener { onAction(item) }
        }
    }
}
