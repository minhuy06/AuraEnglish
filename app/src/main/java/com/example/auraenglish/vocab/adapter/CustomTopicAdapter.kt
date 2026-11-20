package com.example.auraenglish.vocab.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemCustomTopicBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.vocab.data.CustomTopicUiModel

class CustomTopicAdapter(
    private val onTopicClick: (CustomTopicUiModel) -> Unit,
    private val onMoreClick: (CustomTopicUiModel, View) -> Unit
) : ListAdapter<CustomTopicUiModel, CustomTopicAdapter.TopicViewHolder>(TopicDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemCustomTopicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TopicViewHolder(
        private val binding: ItemCustomTopicBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomTopicUiModel) = with(binding) {
            tvTopicItemName.text = item.name
            tvTopicItemMeta.text = root.context.getString(
                R.string.vocab_topic_meta_format,
                item.wordCount,
                item.createdDate
            )
            pbTopicProgress.progress = item.progressPercent

            cardCustomTopicItem.setOnClickDebounced { onTopicClick(item) }
            btnTopicItemMore.setOnClickListener { onMoreClick(item, it) }
        }
    }

    private object TopicDiffCallback : DiffUtil.ItemCallback<CustomTopicUiModel>() {
        override fun areItemsTheSame(oldItem: CustomTopicUiModel, newItem: CustomTopicUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CustomTopicUiModel, newItem: CustomTopicUiModel): Boolean =
            oldItem == newItem
    }
}
