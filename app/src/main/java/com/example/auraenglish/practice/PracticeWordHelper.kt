package com.example.auraenglish.practice

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auraenglish.R

object PracticeWordHelper {

    private const val MENU_ADD_WORD = 1001
    private const val MENU_COPY = 1002

    fun enableSelection(
        activity: AppCompatActivity,
        textView: TextView,
        questionNumber: () -> Int,
        onWordAdded: (() -> Unit)? = null
    ) {
        textView.setTextIsSelectable(true)
        textView.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.clear()
                menu.add(0, MENU_COPY, 0, R.string.practice_copy)
                menu.add(0, MENU_ADD_WORD, 1, R.string.practice_add_word)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                val selected = selectedText(textView)
                when (item.itemId) {
                    MENU_COPY -> {
                        if (selected.isEmpty()) {
                            Toast.makeText(activity, R.string.practice_select_word_hint, Toast.LENGTH_SHORT).show()
                            return true
                        }
                        val clipboard = activity.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE)
                            as android.content.ClipboardManager
                        clipboard.setPrimaryClip(
                            android.content.ClipData.newPlainText("word", selected)
                        )
                        Toast.makeText(activity, R.string.practice_copied_word, Toast.LENGTH_SHORT).show()
                        mode.finish()
                        return true
                    }
                    MENU_ADD_WORD -> {
                        addSelectedWord(activity, textView, questionNumber(), onWordAdded)
                        mode.finish()
                        return true
                    }
                }
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) = Unit
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

    fun addSelectedWord(
        activity: AppCompatActivity,
        textView: TextView,
        questionNumber: Int,
        onWordAdded: (() -> Unit)? = null
    ): Boolean {
        val word = selectedText(textView)
        if (word.isEmpty()) {
            Toast.makeText(activity, R.string.practice_select_word_hint, Toast.LENGTH_SHORT).show()
            return false
        }
        val added = PracticeSampleData.addWord(word, questionNumber)
        Toast.makeText(
            activity,
            if (added) activity.getString(R.string.practice_word_added_named, word)
            else activity.getString(R.string.practice_word_exists, word),
            Toast.LENGTH_SHORT
        ).show()
        if (added) onWordAdded?.invoke()
        return added
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
}
