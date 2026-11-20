package com.example.auraenglish.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.databinding.ItemDashboardSectionHeaderBinding

// Header section trong danh mục (vd. Luyện đề)
class DashboardSectionAdapter(
    private var title: String = ""
) : RecyclerView.Adapter<DashboardSectionAdapter.VH>() {

    fun setTitle(value: String) {
        title = value
        notifyItemChanged(0)
    }

    override fun getItemCount() = if (title.isBlank()) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDashboardSectionHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvSection.text = title
    }

    class VH(val binding: ItemDashboardSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)
}
