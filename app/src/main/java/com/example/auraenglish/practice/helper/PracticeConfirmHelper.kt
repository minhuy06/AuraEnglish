package com.example.auraenglish.practice.helper

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R
import com.example.auraenglish.databinding.DialogPracticeConfirmSubmitBinding

// Dialog xác nhận nộp / thoát bài
object PracticeConfirmHelper {

    fun showSubmitExam(
        activity: AppCompatActivity,
        unansweredCount: Int = 0,
        onConfirm: () -> Unit
    ) {
        show(
            activity = activity,
            title = activity.getString(R.string.practice_submit_title),
            message = if (unansweredCount > 0) {
                activity.getString(R.string.practice_submit_message_unanswered, unansweredCount)
            } else {
                activity.getString(R.string.practice_submit_message)
            },
            cancel = activity.getString(R.string.practice_submit_cancel),
            confirm = activity.getString(R.string.practice_submit_confirm),
            onConfirm = onConfirm
        )
    }

    fun showExitExam(
        activity: AppCompatActivity,
        onConfirm: () -> Unit
    ) {
        show(
            activity = activity,
            title = activity.getString(R.string.practice_exit_title),
            message = activity.getString(R.string.practice_exit_message),
            cancel = activity.getString(R.string.practice_exit_cancel),
            confirm = activity.getString(R.string.practice_exit_confirm),
            onConfirm = onConfirm
        )
    }

    private fun show(
        activity: AppCompatActivity,
        title: String,
        message: String,
        cancel: String,
        confirm: String,
        onConfirm: () -> Unit
    ) {
        val binding = DialogPracticeConfirmSubmitBinding.inflate(LayoutInflater.from(activity))
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

        binding.tvTitle.text = title
        binding.tvMessage.text = message
        binding.btnCancel.text = cancel
        binding.btnConfirm.text = confirm

        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        binding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }
        dialog.show()
    }
}
