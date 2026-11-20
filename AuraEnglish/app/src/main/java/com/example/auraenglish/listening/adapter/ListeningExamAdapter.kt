package com.example.auraenglish.listening.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ItemListeningExamBinding
import com.example.auraenglish.listening.data.ListenStatus
import com.example.auraenglish.listening.data.ListeningExam

// Adapter danh sách đề luyện nghe
class ListeningExamAdapter(
    private val onClick: (ListeningExam) -> Unit
) : ListAdapter<ListeningExam, ListeningExamAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<ListeningExam>() {
        override fun areItemsTheSame(a: ListeningExam, b: ListeningExam) = a.id == b.id
        override fun areContentsTheSame(a: ListeningExam, b: ListeningExam) = a == b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemListeningExamBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(
        private val binding: ItemListeningExamBinding,
        private val onClick: (ListeningExam) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListeningExam) = with(binding) {
            val ctx = root.context
            tvIndex.text = item.index
            tvTitle.text = item.title
            tvMeta.text = item.meta
            when (item.status) {
                ListenStatus.TODO -> {
                    tvStatus.text = ctx.getString(R.string.listen_status_todo)
                    tvStatus.setBackgroundResource(R.drawable.bg_practice_status_todo)
                    tvStatus.setTextColor(ctx.getColor(R.color.practice_text_secondary))
                }
                ListenStatus.DOING -> {
                    tvStatus.text = ctx.getString(R.string.listen_status_doing, item.answeredCount)
                    tvStatus.setBackgroundResource(R.drawable.bg_practice_status_doing)
                    tvStatus.setTextColor(ctx.getColor(R.color.practice_chip_progress_text))
                }
                ListenStatus.DONE -> {
                    tvStatus.text = ctx.getString(R.string.listen_status_done, item.correctCount)
                    tvStatus.setBackgroundResource(R.drawable.bg_practice_status_done)
                    tvStatus.setTextColor(ctx.getColor(R.color.practice_chip_done_text))
                }
            }
            root.setOnClickListener { onClick(item) }
        }
    }
}
