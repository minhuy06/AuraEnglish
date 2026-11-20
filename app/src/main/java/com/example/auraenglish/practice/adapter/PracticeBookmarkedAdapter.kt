package com.example.auraenglish.practice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemPracticeBookmarkedBinding
import com.example.auraenglish.practice.PracticeQuestion

class PracticeBookmarkedAdapter(
    private val onQuestionClick: (PracticeQuestion) -> Unit
) : ListAdapter<PracticeQuestion, PracticeBookmarkedAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<PracticeQuestion>() {
        override fun areItemsTheSame(
            oldItem: PracticeQuestion,
            newItem: PracticeQuestion
        ): Boolean = oldItem.number == newItem.number

        override fun areContentsTheSame(
            oldItem: PracticeQuestion,
            newItem: PracticeQuestion
        ): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPracticeBookmarkedBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onQuestionClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemPracticeBookmarkedBinding,
        private val onQuestionClick: (PracticeQuestion) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(q: PracticeQuestion) = with(binding) {
            val context = root.context
            tvNumber.text = q.number.toString()
            tvTag.text = q.tag
            tvQuestion.text = q.stem
            if (q.isAnswered) {
                val letter = ('A' + (q.userAnswerIndex ?: 0)).toString()
                tvStatus.text = context.getString(R.string.practice_selected_answer, letter)
                tvStatus.setBackgroundResource(R.drawable.bg_practice_status_doing)
                tvStatus.setTextColor(context.getColor(R.color.practice_chip_progress_text))
            } else {
                tvStatus.text = context.getString(R.string.practice_status_todo)
                tvStatus.setBackgroundResource(R.drawable.bg_practice_status_todo)
                tvStatus.setTextColor(context.getColor(R.color.practice_pending_text))
            }
            root.setOnClickListener { onQuestionClick(q) }
        }
    }
}
