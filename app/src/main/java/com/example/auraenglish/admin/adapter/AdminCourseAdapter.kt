package com.example.auraenglish.admin.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.admin.data.AdminCourseSkill
import com.example.auraenglish.admin.data.AdminCourseUiModel
import com.example.auraenglish.databinding.ItemAdminCourseBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class AdminCourseAdapter(
    private val onPrimaryAction: (AdminCourseUiModel) -> Unit,
    private val onEditClick: (AdminCourseUiModel) -> Unit,
    private val onDangerAction: (AdminCourseUiModel) -> Unit
) : ListAdapter<AdminCourseUiModel, AdminCourseAdapter.CourseViewHolder>(CourseDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemAdminCourseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(
        private val binding: ItemAdminCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AdminCourseUiModel) = with(binding) {
            val ctx = root.context
            tvCourseSkillTag.text = item.skillTag
            tvCourseName.text = item.title
            tvCourseStats.text = if (item.isPublished) {
                item.metaStats
            } else {
                ctx.getString(R.string.admin_course_unpublished_meta)
            }

            if (item.isPublished) {
                tvCourseStatusBadge.text = ctx.getString(R.string.admin_course_status_published)
                tvCourseStatusBadge.setBackgroundResource(R.drawable.bg_admin_free_badge)
                tvCourseStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.admin_success))

                val manageLabelRes = when (item.skill) {
                    AdminCourseSkill.VOCAB -> R.string.admin_course_action_manage_vocab
                    AdminCourseSkill.GRAMMAR -> R.string.admin_course_action_manage_grammar
                    else -> R.string.admin_course_action_manage_listen
                }
                btnCoursePrimaryAction.setText(manageLabelRes)
                btnCoursePrimaryAction.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.admin_primary_soft))
                btnCoursePrimaryAction.setTextColor(ContextCompat.getColor(ctx, R.color.admin_primary))

                btnCourseDangerAction.setText(R.string.admin_course_action_hide)
            } else {
                tvCourseStatusBadge.text = ctx.getString(R.string.admin_course_status_draft)
                tvCourseStatusBadge.setBackgroundResource(R.drawable.bg_admin_price_badge)
                tvCourseStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.admin_price_text))

                btnCoursePrimaryAction.setText(R.string.admin_course_action_publish)
                btnCoursePrimaryAction.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.admin_primary))
                btnCoursePrimaryAction.setTextColor(ContextCompat.getColor(ctx, R.color.white))

                btnCourseDangerAction.setText(R.string.admin_course_action_delete)
            }

            btnCoursePrimaryAction.setOnClickDebounced { onPrimaryAction(item) }
            btnCourseEdit.setOnClickDebounced { onEditClick(item) }
            btnCourseDangerAction.setOnClickDebounced { onDangerAction(item) }
        }
    }

    private object CourseDiffCallback : DiffUtil.ItemCallback<AdminCourseUiModel>() {
        override fun areItemsTheSame(oldItem: AdminCourseUiModel, newItem: AdminCourseUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AdminCourseUiModel, newItem: AdminCourseUiModel): Boolean =
            oldItem == newItem
    }
}
