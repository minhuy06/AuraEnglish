package com.example.auraenglish.practice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemPracticeExamBinding
import com.example.auraenglish.practice.data.ExamStatus
import com.example.auraenglish.practice.data.PracticeExam

// Adapter danh sách đề (+ icon status + ETA)
class PracticeExamAdapter(
    private val onExamClick: (PracticeExam) -> Unit
) : ListAdapter<PracticeExam, PracticeExamAdapter.ExamVH>(Diff) {

    object Diff : DiffUtil.ItemCallback<PracticeExam>() {
        override fun areItemsTheSame(oldItem: PracticeExam, newItem: PracticeExam): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PracticeExam, newItem: PracticeExam): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamVH {
        val binding = ItemPracticeExamBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ExamVH(binding, onExamClick)
    }

    override fun onBindViewHolder(holder: ExamVH, position: Int) {
        holder.bind(getItem(position))
    }

    class ExamVH(
        private val binding: ItemPracticeExamBinding,
        private val onExamClick: (PracticeExam) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(exam: PracticeExam) = with(binding) {
            tvIndex.text = exam.index
            tvTitle.text = exam.title
            tvMeta.text = exam.meta
            val context = root.context
            when (exam.status) {
                ExamStatus.TODO -> {
                    tvStatus.text = context.getString(R.string.practice_status_todo)
                    tvStatus.setBackgroundResource(R.drawable.bg_practice_status_todo)
                    tvStatus.setTextColor(context.getColor(R.color.practice_text_secondary))
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_practice_status_todo, 0, 0, 0
                    )
                    tvEta.text = context.getString(R.string.practice_eta_full)
                }
                ExamStatus.DOING -> {
                    tvStatus.text = context.getString(
                        R.string.practice_status_doing,
                        exam.answeredCount
                    )
                    tvStatus.setBackgroundResource(R.drawable.bg_practice_status_doing)
                    tvStatus.setTextColor(context.getColor(R.color.practice_chip_progress_text))
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_practice_status_doing, 0, 0, 0
                    )
                    val left = ((200 - exam.answeredCount).coerceAtLeast(0) * 0.6)
                        .toInt().coerceAtLeast(1)
                    tvEta.text = context.getString(R.string.practice_eta_left, left)
                }
                ExamStatus.DONE -> {
                    tvStatus.text = context.getString(R.string.practice_status_done, exam.score)
                    tvStatus.setBackgroundResource(R.drawable.bg_practice_status_done)
                    tvStatus.setTextColor(context.getColor(R.color.practice_chip_done_text))
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_practice_status_done, 0, 0, 0
                    )
                    tvEta.text = ""
                }
                ExamStatus.ALL -> Unit
            }
            root.alpha = 0f
            root.translationY = 10f
            root.animate().alpha(1f).translationY(0f).setDuration(220).start()
            root.setOnClickListener { onExamClick(exam) }
        }
    }
}
