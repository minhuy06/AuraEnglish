package com.example.auraenglish.goal.helper

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R
import com.example.auraenglish.databinding.DialogGoalExitBinding
import com.example.auraenglish.databinding.DialogGoalSubmitBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced

object GoalDialogHelper {

    fun showExitAssessment(
        activity: AppCompatActivity,
        onConfirmExit: () -> Unit
    ) {
        val binding = DialogGoalExitBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.9f).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
        }

        binding.btnDialogExitClose.setOnClickListener { dialog.dismiss() }
        binding.btnDialogExitContinue.setOnClickDebounced { dialog.dismiss() }
        binding.btnDialogExitConfirm.setOnClickDebounced {
            dialog.dismiss()
            onConfirmExit()
        }

        dialog.show()
    }

    fun showSubmitAssessment(
        activity: AppCompatActivity,
        answeredCount: Int,
        skippedCount: Int,
        onConfirmSubmit: () -> Unit
    ) {
        val binding = DialogGoalSubmitBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.9f).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCancelable(true)
        }

        binding.tvDialogAnsweredCount.text = activity.getString(R.string.goal_submit_done_format, answeredCount, 20)
        binding.tvDialogSkippedCount.text = activity.getString(R.string.goal_submit_skip_format, skippedCount)

        binding.btnDialogSubmitClose.setOnClickListener { dialog.dismiss() }
        binding.btnDialogSubmitContinue.setOnClickDebounced { dialog.dismiss() }
        binding.btnDialogSubmitConfirm.setOnClickDebounced {
            dialog.dismiss()
            onConfirmSubmit()
        }

        dialog.show()
    }
}
