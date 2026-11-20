package com.example.auraenglish.practice.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemPracticeRankRowBinding
import com.example.auraenglish.practice.RankEntry
import com.example.auraenglish.practice.PracticeRankData

class PracticeRankAdapter : ListAdapter<RankEntry, PracticeRankAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<RankEntry>() {
        override fun areItemsTheSame(oldItem: RankEntry, newItem: RankEntry): Boolean =
            oldItem.rank == newItem.rank && oldItem.initials == newItem.initials

        override fun areContentsTheSame(oldItem: RankEntry, newItem: RankEntry): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPracticeRankRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemPracticeRankRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: RankEntry) = with(binding) {
            val context = root.context
            tvRank.text = entry.rank.toString()
            tvAvatar.text = entry.initials
            tintAvatar(tvAvatar, entry.avatarColorRes)
            tvName.text = if (entry.isCurrentUser) {
                context.getString(R.string.practice_rank_you_suffix, entry.name)
            } else {
                entry.name
            }
            tvXp.text = context.getString(
                R.string.practice_rank_xp,
                PracticeRankData.formatXp(entry.xp)
            )
            root.setBackgroundResource(
                if (entry.isCurrentUser) R.drawable.bg_practice_rank_pinned
                else R.drawable.bg_practice_rank_row
            )
        }

        private fun tintAvatar(view: TextView, colorRes: Int) {
            view.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ContextCompat.getColor(view.context, colorRes))
            }
        }
    }
}
