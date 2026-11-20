package com.example.auraenglish.admin.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.auraenglish.R
import com.example.auraenglish.admin.data.AdminStudentStatus
import com.example.auraenglish.admin.data.AdminStudentUiModel
import com.example.auraenglish.databinding.ItemAdminStudentBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

class AdminStudentAdapter(
    private val onProfileClick: (AdminStudentUiModel) -> Unit,
    private val onStatusAction: (AdminStudentUiModel) -> Unit
) : ListAdapter<AdminStudentUiModel, AdminStudentAdapter.StudentViewHolder>(StudentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemAdminStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(
        private val binding: ItemAdminStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: AdminStudentUiModel) = with(binding) {
            val context = root.context
            tvStudentAvatar.text = student.initials
            tvStudentAvatar.contentDescription = context.getString(
                R.string.admin_student_avatar_description,
                student.fullName
            )
            tvStudentName.text = student.fullName
            tvStudentEmail.text = student.email
            tvStudentCourse.text = context.getString(
                R.string.admin_student_course_fmt,
                student.currentCourse
            )
            tvStudentProgress.text = context.getString(
                R.string.admin_student_progress_fmt,
                student.progressPercent
            )
            pbStudentProgress.progress = student.progressPercent
            tvStudentLearningMeta.text = context.getString(
                R.string.admin_student_learning_meta_fmt,
                student.completedLessons,
                student.totalLessons,
                student.streakDays
            )
            tvStudentLastActive.text = context.getString(
                R.string.admin_student_last_active_fmt,
                student.lastActive
            )

            bindStatus(student.status)
            bindStatusAction(student)

            btnStudentProfile.setOnClickDebounced { onProfileClick(student) }
            btnStudentStatusAction.setOnClickDebounced { onStatusAction(student) }
        }

        private fun bindStatus(status: AdminStudentStatus) = with(binding) {
            val context = root.context
            val (labelRes, backgroundColorRes, textColorRes) = when (status) {
                AdminStudentStatus.ACTIVE -> Triple(
                    R.string.admin_student_status_active,
                    R.color.admin_success_soft,
                    R.color.admin_success
                )
                AdminStudentStatus.AT_RISK -> Triple(
                    R.string.admin_student_status_at_risk,
                    R.color.admin_warning_soft,
                    R.color.admin_price_text
                )
                AdminStudentStatus.SUSPENDED -> Triple(
                    R.string.admin_student_status_suspended,
                    R.color.admin_danger_soft,
                    R.color.admin_danger
                )
            }
            tvStudentStatus.setText(labelRes)
            tvStudentStatus.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, backgroundColorRes)
            )
            tvStudentStatus.setTextColor(ContextCompat.getColor(context, textColorRes))

            val progressColorRes = when (status) {
                AdminStudentStatus.ACTIVE -> R.color.admin_primary
                AdminStudentStatus.AT_RISK -> R.color.admin_warning
                AdminStudentStatus.SUSPENDED -> R.color.admin_nav_inactive
            }
            pbStudentProgress.progressTintList = ColorStateList.valueOf(
                ContextCompat.getColor(context, progressColorRes)
            )
        }

        private fun bindStatusAction(student: AdminStudentUiModel) = with(binding.btnStudentStatusAction) {
            val context = binding.root.context
            if (student.isSuspended) {
                setText(R.string.admin_student_action_reactivate)
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.admin_success_soft)
                )
                strokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.admin_success)
                )
                setTextColor(ContextCompat.getColor(context, R.color.admin_success))
            } else {
                setText(R.string.admin_student_action_suspend)
                backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.admin_danger_soft)
                )
                strokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.admin_danger_border)
                )
                setTextColor(ContextCompat.getColor(context, R.color.admin_danger))
            }
        }
    }

    private object StudentDiffCallback : DiffUtil.ItemCallback<AdminStudentUiModel>() {
        override fun areItemsTheSame(
            oldItem: AdminStudentUiModel,
            newItem: AdminStudentUiModel
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: AdminStudentUiModel,
            newItem: AdminStudentUiModel
        ): Boolean = oldItem == newItem
    }
}
