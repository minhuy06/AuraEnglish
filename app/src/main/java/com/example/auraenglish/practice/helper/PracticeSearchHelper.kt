package com.example.auraenglish.practice.helper

import com.example.auraenglish.practice.ui.*

import com.example.auraenglish.practice.data.*

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.databinding.DialogPracticeSearchBinding

// Dialog tìm kiếm dùng chung
object PracticeSearchHelper {

    fun show(
        activity: AppCompatActivity,
        initialQuery: String = "",
        hint: String? = null,
        onApply: (String) -> Unit,
        onClear: () -> Unit
    ) {
        val binding = DialogPracticeSearchBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.9f).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        binding.etSearch.setText(initialQuery)
        binding.etSearch.setSelection(initialQuery.length)
        if (hint != null) binding.etSearch.hint = hint

        fun apply() {
            onApply(binding.etSearch.text?.toString()?.trim().orEmpty())
            dialog.dismiss()
        }

        binding.btnApply.setOnClickListener { apply() }
        binding.btnClear.setOnClickListener {
            binding.etSearch.setText("")
            onClear()
            dialog.dismiss()
        }
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                apply()
                true
            } else false
        }
        dialog.show()
        binding.etSearch.requestFocus()
    }
}
