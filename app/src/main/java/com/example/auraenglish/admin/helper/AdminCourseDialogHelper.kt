package com.example.auraenglish.admin.helper

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.example.auraenglish.R
import com.example.auraenglish.admin.data.AdminCourseSkill
import com.example.auraenglish.admin.data.AdminCourseUiModel
import com.example.auraenglish.databinding.DialogCreateCourseBinding
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AdminCourseDialogHelper {

    private val cefrOptions = listOf(
        "A1 - A2 (Cơ bản)" to "A2",
        "B1 - B2 (Trung cấp - Cao cấp)" to "B1",
        "B2 - C1 (Nâng cao)" to "B2"
    )

    fun showCourseDialog(
        activity: Activity,
        editing: AdminCourseUiModel? = null,
        onSave: (title: String, skill: AdminCourseSkill, cefrShort: String, metaStats: String) -> Unit
    ) {
        val binding = DialogCreateCourseBinding.inflate(LayoutInflater.from(activity))
        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var selectedSkill = editing?.skill ?: AdminCourseSkill.VOCAB
        var selectedCefrPair = cefrOptions.find { it.second == editing?.cefrLevel } ?: cefrOptions[1]

        val skillChips = listOf(
            binding.chipSkillVocab to AdminCourseSkill.VOCAB,
            binding.chipSkillGrammar to AdminCourseSkill.GRAMMAR,
            binding.chipSkillListen to AdminCourseSkill.LISTEN
        )

        fun updateSkillChips(active: TextView) {
            skillChips.forEach { (chip, _) ->
                val isActive = chip == active
                chip.setBackgroundResource(
                    if (isActive) R.drawable.bg_admin_chip_active else R.drawable.bg_admin_chip_inactive
                )
                chip.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        if (isActive) R.color.white else R.color.admin_text_secondary
                    )
                )
            }
        }

        with(binding) {
            tvDialogCourseTitle.setText(
                if (editing == null) R.string.admin_popup_create_course_title
                else R.string.admin_popup_edit_course_title
            )
            editing?.let {
                edtCourseName.setText(it.title)
                edtCourseMeta.setText(it.metaStats)
            }
            tvCourseCefrDropdown.text = selectedCefrPair.first
            updateSkillChips(skillChips.first { it.second == selectedSkill }.first)

            edtCourseName.doAfterTextChanged { tvCourseDialogError.gone() }
            btnCloseCourseDialog.setOnClickListener { dialog.dismiss() }
            tvCancelCourseDialog.setOnClickListener { dialog.dismiss() }

            skillChips.forEach { (chip, skill) ->
                chip.setOnClickListener {
                    selectedSkill = skill
                    updateSkillChips(chip)
                }
            }

            tvCourseCefrDropdown.setOnClickListener {
                val labels = cefrOptions.map { it.first }.toTypedArray()
                MaterialAlertDialogBuilder(activity)
                    .setItems(labels) { _, which ->
                        selectedCefrPair = cefrOptions[which]
                        tvCourseCefrDropdown.text = selectedCefrPair.first
                    }
                    .show()
            }

            btnSaveCourse.setOnClickDebounced {
                val title = edtCourseName.text?.toString()?.trim().orEmpty()
                if (title.isBlank()) {
                    tvCourseDialogError.visible()
                    edtCourseName.requestFocus()
                    return@setOnClickDebounced
                }
                val meta = edtCourseMeta.text?.toString()?.trim().orEmpty()
                dialog.dismiss()
                onSave(title, selectedSkill, selectedCefrPair.second, meta)
            }
        }
        dialog.show()
    }
}
