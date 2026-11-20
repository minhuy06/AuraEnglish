package com.example.auraenglish.dashboard.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.dashboard.data.DashCourse
import com.example.auraenglish.databinding.ItemDashboardCourseBinding

// Adapter danh mục khóa học
class DashboardCourseAdapter(
    private val onCourseClick: (DashCourse) -> Unit = {}
) :
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
        holder.bind(getItem(position), onCourseClick)
    }

    class VH(private val binding: ItemDashboardCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashCourse, onCourseClick: (DashCourse) -> Unit) = with(binding) {
            root.setOnClickListener { onCourseClick(item) }
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
            ivIcon.setImageResource(item.iconRes)
            root.alpha = 0f
            root.translationY = 10f
            root.animate().alpha(1f).translationY(0f).setDuration(220).start()
            progressFill.post {
                val parentW = (progressFill.parent as android.view.View).width
                val ratio = item.done / item.total.coerceAtLeast(1).toFloat()
                val target = (parentW * ratio).toInt().coerceAtLeast(8)
                ValueAnimator.ofInt(0, target).apply {
                    duration = 420
                    interpolator = DecelerateInterpolator()
                    addUpdateListener {
                        progressFill.layoutParams = progressFill.layoutParams.apply {
                            width = it.animatedValue as Int
                        }
                        progressFill.requestLayout()
                    }
                    start()
                }
            }
        }
    }
}
