package com.example.auraenglish.vocab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemVocabWordBinding
import com.example.auraenglish.vocab.data.VocabWordUiModel

class VocabWordAdapter(
    private val onWordClick: (VocabWordUiModel) -> Unit,
    private val onSpeakClick: (VocabWordUiModel) -> Unit
) : ListAdapter<VocabWordUiModel, VocabWordAdapter.WordViewHolder>(WordDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemVocabWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WordViewHolder(
        private val binding: ItemVocabWordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VocabWordUiModel) = with(binding) {
            val ctx = root.context
            tvWordEnglish.text = item.word
            tvWordPhonetic.text = item.phonetic
            tvWordPosTag.text = item.posTag
            tvWordMeaning.text = item.meaning

            if (item.isLearned) {
                tvWordStatusBadge.text = ctx.getString(R.string.vocab_status_learned)
                tvWordStatusBadge.setBackgroundResource(R.drawable.bg_vocab_badge_learned)
                tvWordStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.vocab_learned_text))
            } else {
                tvWordStatusBadge.text = ctx.getString(R.string.vocab_status_unlearned)
                tvWordStatusBadge.setBackgroundResource(R.drawable.bg_vocab_badge_unlearned)
                tvWordStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.vocab_unlearned_text))
            }

            cardWordItem.setOnClickListener { onWordClick(item) }
            btnWordSpeak.setOnClickListener { onSpeakClick(item) }
        }
    }

    private object WordDiffCallback : DiffUtil.ItemCallback<VocabWordUiModel>() {
        override fun areItemsTheSame(oldItem: VocabWordUiModel, newItem: VocabWordUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: VocabWordUiModel, newItem: VocabWordUiModel): Boolean =
            oldItem == newItem
    }
}
