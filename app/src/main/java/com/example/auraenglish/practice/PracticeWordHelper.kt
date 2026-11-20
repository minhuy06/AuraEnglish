package com.example.auraenglish.practice

import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R
import com.example.auraenglish.databinding.DialogPracticeAddWordBinding
import com.example.auraenglish.databinding.PopupPracticeWordActionsBinding

object PracticeWordHelper {

    fun enableSelection(
        activity: AppCompatActivity,
        textView: TextView,
        questionNumber: () -> Int,
        onWordAdded: (() -> Unit)? = null
    ) {
        textView.setTextIsSelectable(true)
        // Giữ selection handles, ẩn toolbar hệ thống → chỉ dùng popup Figma
        val hideSystemMenu = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.clear()
                return true
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = false

            override fun onDestroyActionMode(mode: ActionMode) = Unit
        }
        textView.customSelectionActionModeCallback = hideSystemMenu
        textView.customInsertionActionModeCallback = hideSystemMenu

        textView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                textView.post {
                    if (selectedText(textView).isNotEmpty()) {
                        showActionPopup(activity, textView, questionNumber, onWordAdded)
                    }
                }
            }
            false
        }
    }

    fun selectedText(textView: TextView): String {
        val start = textView.selectionStart
        val end = textView.selectionEnd
        if (start < 0 || end < 0 || start == end) return ""
        val from = minOf(start, end)
        val to = maxOf(start, end)
        return textView.text.subSequence(from, to).toString().trim()
    }

    fun showActionPopup(
        activity: AppCompatActivity,
        textView: TextView,
        questionNumber: () -> Int,
        onWordAdded: (() -> Unit)? = null
    ) {
        val selected = selectedText(textView)
        if (selected.isEmpty()) return

        val binding = PopupPracticeWordActionsBinding.inflate(LayoutInflater.from(activity))
        val popup = PopupWindow(
            binding.root,
            activity.resources.getDimensionPixelSize(R.dimen.practice_word_popup_width),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = 12f
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        binding.btnCopy.setOnClickListener {
            val clipboard = activity.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE)
                as android.content.ClipboardManager
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("word", selected))
            Toast.makeText(activity, R.string.practice_copied_word, Toast.LENGTH_SHORT).show()
            popup.dismiss()
            clearSelection(textView)
        }
        binding.btnAddWord.setOnClickListener {
            popup.dismiss()
            showAddWordDialog(activity, selected, questionNumber(), onWordAdded)
        }

        val anchor = selectionAnchor(textView)
        popup.showAsDropDown(textView, anchor.first, anchor.second)
    }

    fun showAddWordDialog(
        activity: AppCompatActivity,
        initialWord: String,
        questionNumber: Int,
        onWordAdded: (() -> Unit)? = null
    ) {
        val binding = DialogPracticeAddWordBinding.inflate(LayoutInflater.from(activity))
        binding.etWord.setText(initialWord)
        binding.etWord.setSelection(initialWord.length)

        val dialog = Dialog(activity).apply {
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.9f).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val chips = listOf(binding.chipNoun, binding.chipVerb, binding.chipAdj, binding.chipAdv)
        fun selectChip(selected: TextView) {
            chips.forEach { chip ->
                val active = chip == selected
                chip.setBackgroundResource(
                    if (active) R.drawable.bg_practice_chip_active else R.drawable.bg_practice_chip_inactive
                )
                chip.setTextColor(
                    activity.getColor(if (active) R.color.white else R.color.practice_text_secondary)
                )
            }
        }
        chips.forEach { chip -> chip.setOnClickListener { selectChip(chip) } }

        binding.btnCloseDialog.setOnClickListener { dialog.dismiss() }
        binding.btnNewTopic.setOnClickListener {
            Toast.makeText(activity, R.string.practice_word_new_topic, Toast.LENGTH_SHORT).show()
        }
        binding.btnSaveWord.setOnClickListener {
            val word = binding.etWord.text?.toString().orEmpty().trim()
            if (word.isEmpty()) {
                Toast.makeText(activity, R.string.practice_word_empty_error, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val added = PracticeSampleData.addWord(word, questionNumber)
            Toast.makeText(
                activity,
                if (added) activity.getString(R.string.practice_word_added_named, word)
                else activity.getString(R.string.practice_word_exists, word),
                Toast.LENGTH_SHORT
            ).show()
            if (added) onWordAdded?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun highlightSavedWords(textView: TextView, source: String) {
        val spannable = SpannableString(source)
        val highlight = textView.context.getColor(R.color.practice_word_highlight)
        PracticeSampleData.savedWords.forEach { saved ->
            val target = saved.word
            if (target.isBlank()) return@forEach
            var index = source.indexOf(target, ignoreCase = true)
            while (index >= 0) {
                spannable.setSpan(
                    BackgroundColorSpan(highlight),
                    index,
                    index + target.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                index = source.indexOf(target, index + target.length, ignoreCase = true)
            }
        }
        textView.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    private fun clearSelection(textView: TextView) {
        textView.clearFocus()
    }

    /** Offset popup gần đoạn đang bôi đen, không làm đổi kích thước card. */
    private fun selectionAnchor(textView: TextView): Pair<Int, Int> {
        val start = textView.selectionStart.coerceAtLeast(0)
        val layout = textView.layout ?: return 0 to textView.height
        val line = layout.getLineForOffset(start)
        val x = layout.getPrimaryHorizontal(start).toInt()
        val y = layout.getLineBottom(line)
        val rect = Rect()
        textView.getLocalVisibleRect(rect)
        return x.coerceIn(0, (textView.width - 160).coerceAtLeast(0)) to y
    }
}
