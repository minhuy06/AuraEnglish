package com.example.auraenglish.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.data.DashCourse
import com.example.auraenglish.databinding.ItemDashboardCourseBinding

// Adapter danh mục khóa học
class DashboardCourseAdapter :
    ListAdapter<DashCourse, DashboardCourseAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<DashCourse>() {
        override fun areItemsTheSame(oldItem: DashCourse, newItem: DashCourse) =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: DashCourse, newItem: DashCourse) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDashboardCourseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(private val binding: ItemDashboardCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashCourse) = with(binding) {
            val ctx = root.context
            tvTitle.text = item.title
            tvMeta.text = if (item.topics > 0) {
                ctx.getString(R.string.dash_meta_topics, item.topics, item.lessons)
            } else {
                ctx.getString(R.string.dash_meta_questions, item.total)
            }
            tvProgress.text = if (item.doneLabel) {
                ctx.getString(R.string.dash_done_fmt, item.done, item.total)
            } else {
                ctx.getString(R.string.dash_progress_fmt, item.done, item.total)
            }
            accent.setBackgroundResource(item.accentRes)
            progressFill.post {
                val parentW = (progressFill.parent as android.view.View).width
                val ratio = item.done / item.total.coerceAtLeast(1).toFloat()
                progressFill.layoutParams = progressFill.layoutParams.apply {
                    width = (parentW * ratio).toInt().coerceAtLeast(8)
                }
                progressFill.requestLayout()
            }
        }
    }
}
