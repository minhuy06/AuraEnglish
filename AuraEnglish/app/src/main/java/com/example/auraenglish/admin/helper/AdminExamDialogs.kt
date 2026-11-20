package com.example.auraenglish.admin.helper

import com.example.auraenglish.admin.data.ExamSetUiModel

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.auraenglish.R
import com.example.auraenglish.databinding.DialogConfirmDeleteBinding
import com.example.auraenglish.databinding.DialogConfirmStopSellingBinding
import com.example.auraenglish.databinding.DialogCreateExamSetBinding
import com.example.auraenglish.databinding.DialogEditExamSetBinding
import com.example.auraenglish.databinding.DialogEditPriceBinding

// Dialog CRUD bộ đề admin
class AdminExamDialogs(private val activity: AppCompatActivity) {

    fun showCreateExamSet(onCreated: (String) -> Unit) {
        val binding = DialogCreateExamSetBinding.inflate(LayoutInflater.from(activity))
        val dialog = createDialog(binding.root)

        fun selectPaid(isPaid: Boolean) {
            binding.optionPaid.isSelected = isPaid
            binding.optionFree.isSelected = !isPaid
            binding.optionPaid.setTextColor(
                activity.getColor(if (isPaid) R.color.admin_primary else R.color.admin_text_muted)
            )
            binding.optionFree.setTextColor(
                activity.getColor(if (!isPaid) R.color.admin_primary else R.color.admin_text_muted)
            )
            binding.priceRow.isVisible = isPaid
        }

        selectPaid(true)
        binding.optionPaid.setOnClickListener { selectPaid(true) }
        binding.optionFree.setOnClickListener { selectPaid(false) }
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        binding.btnConfirm.setOnClickListener {
            val name = binding.etExamName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                Toast.makeText(activity, R.string.hint_exam_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onCreated(name)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showEditPrice(item: ExamSetUiModel, onSaved: (String) -> Unit) {
        val binding = DialogEditPriceBinding.inflate(LayoutInflater.from(activity))
        val dialog = createDialog(binding.root)
        binding.tvExamName.text = item.title
        binding.etPrice.setText(item.priceLabel?.replace(".", "")?.replace("đ", "").orEmpty())
        binding.etDescription.setText(item.description)
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        binding.btnConfirm.setOnClickListener {
            val price = binding.etPrice.text?.toString()?.trim().orEmpty()
            if (price.isEmpty()) {
                Toast.makeText(activity, R.string.hint_price, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onSaved(price)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showEditExam(item: ExamSetUiModel, onSaved: (String) -> Unit) {
        val binding = DialogEditExamSetBinding.inflate(LayoutInflater.from(activity))
        val dialog = createDialog(binding.root)
        binding.etExamName.setText(item.title)
        binding.etExamCount.setText(item.examCount.takeIf { it > 0 }?.toString().orEmpty())
        binding.etDescription.setText(item.description)
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        binding.btnConfirm.setOnClickListener {
            val name = binding.etExamName.text?.toString()?.trim().orEmpty()
            if (name.isEmpty()) {
                Toast.makeText(activity, R.string.hint_exam_name, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onSaved(name)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showStopSelling(item: ExamSetUiModel, onConfirmed: () -> Unit) {
        val binding = DialogConfirmStopSellingBinding.inflate(LayoutInflater.from(activity))
        val dialog = createDialog(binding.root)
        binding.tvMessage.text = activity.getString(R.string.popup_stop_selling_message, item.title)
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        binding.btnConfirm.setOnClickListener {
            onConfirmed()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showDelete(item: ExamSetUiModel, onConfirmed: () -> Unit) {
        val binding = DialogConfirmDeleteBinding.inflate(LayoutInflater.from(activity))
        val dialog = createDialog(binding.root)
        binding.tvMessage.text = activity.getString(R.string.popup_delete_message, item.title)
        binding.btnCancel.setOnClickListener { dialog.dismiss() }
        binding.btnConfirm.setOnClickListener {
            onConfirmed()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun createDialog(content: android.view.View): Dialog {
        return Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(content)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.9f).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}
