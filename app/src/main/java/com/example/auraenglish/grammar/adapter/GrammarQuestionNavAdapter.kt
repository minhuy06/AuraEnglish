package com.example.auraenglish.grammar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemGrammarQuestionNavBinding
import com.example.auraenglish.grammar.data.GrammarNavItemUiModel

class GrammarQuestionNavAdapter(
    private val onSelectQuestion: (Int) -> Unit
) : ListAdapter<GrammarNavItemUiModel, GrammarQuestionNavAdapter.NavViewHolder>(NavDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val binding = ItemGrammarQuestionNavBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NavViewHolder(
        private val binding: ItemGrammarQuestionNavBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GrammarNavItemUiModel) = with(binding.tvQuestionNavPill) {
            val ctx = context
            text = ctx.getString(R.string.grammar_review_nav_item_format, item.number)

            when {
                item.isSelected -> {
                    setBackgroundResource(R.drawable.bg_grammar_nav_active)
                    setTextColor(ContextCompat.getColor(ctx, R.color.white))
                }
                item.isCorrect -> {
                    setBackgroundResource(R.drawable.bg_grammar_nav_correct)
                    setTextColor(ContextCompat.getColor(ctx, R.color.grammar_correct_text))
                }
                else -> {
                    setBackgroundResource(R.drawable.bg_grammar_nav_wrong)
                    setTextColor(ContextCompat.getColor(ctx, R.color.grammar_wrong_text))
                }
            }

            setOnClickListener { onSelectQuestion(item.questionIndex) }
        }
    }

    private object NavDiffCallback : DiffUtil.ItemCallback<GrammarNavItemUiModel>() {
        override fun areItemsTheSame(oldItem: GrammarNavItemUiModel, newItem: GrammarNavItemUiModel): Boolean =
            oldItem.questionIndex == newItem.questionIndex

        override fun areContentsTheSame(oldItem: GrammarNavItemUiModel, newItem: GrammarNavItemUiModel): Boolean =
            oldItem == newItem
    }
}
