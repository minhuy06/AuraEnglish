package com.example.auraenglish.vocab.helper

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.example.auraenglish.R
import com.example.auraenglish.databinding.DialogPracticeCreateTopicBinding
import com.example.auraenglish.databinding.DialogVocabAddWordBinding
import com.example.auraenglish.practice.utils.gone
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.visible
import com.example.auraenglish.vocab.data.CustomTopicUiModel
import com.example.auraenglish.vocab.data.VocabSessionStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object VocabDialogHelper {

    fun showCreateTopicDialog(
        activity: Activity,
        initialName: String = "",
        onSave: (String) -> Unit
    ) {
        val binding = DialogPracticeCreateTopicBinding.inflate(LayoutInflater.from(activity))
        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(binding) {
            if (initialName.isNotBlank()) {
                etTopicName.setText(initialName)
                etTopicName.setSelection(initialName.length)
            }
            etTopicName.doAfterTextChanged { tvTopicError.gone() }
            btnCloseTopic.setOnClickListener { dialog.dismiss() }
            btnSaveTopic.setOnClickDebounced {
                val name = etTopicName.text?.toString()?.trim().orEmpty()
                if (name.isBlank()) {
                    tvTopicError.visible()
                    etTopicName.requestFocus()
                    return@setOnClickDebounced
                }
                dialog.dismiss()
                onSave(name)
            }
        }
        dialog.show()
    }

    fun showAddWordDialog(
        activity: Activity,
        defaultTopic: CustomTopicUiModel,
        onCreateNewTopic: (String) -> CustomTopicUiModel,
        onSaveWord: (topicId: String, word: String, meaning: String, phonetic: String, posTag: String) -> Unit
    ) {
        val binding = DialogVocabAddWordBinding.inflate(LayoutInflater.from(activity))
        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var selectedTopic = defaultTopic
        var selectedPosTag = "(n) Danh từ"

        val posChips = listOf(
            binding.chipPosNoun to "(n) Danh từ",
            binding.chipPosVerb to "(v) Động từ",
            binding.chipPosAdj to "(adj) Tính từ",
            binding.chipPosAdv to "(adv) Trạng từ"
        )

        fun updatePosSelection(activeChip: TextView) {
            posChips.forEach { (chip, _) ->
                val isActive = chip == activeChip
                chip.setBackgroundResource(
                    if (isActive) R.drawable.bg_vocab_tab_active else R.drawable.bg_vocab_tab_inactive
                )
                chip.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        if (isActive) R.color.white else R.color.vocab_text_secondary
                    )
                )
            }
        }

        with(binding) {
            tvSelectedTopicDropdown.text = selectedTopic.name
            etVocabEnglish.doAfterTextChanged { tvAddWordError.gone() }
            etVocabMeaning.doAfterTextChanged { tvAddWordError.gone() }
            btnCloseAddWord.setOnClickListener { dialog.dismiss() }

            posChips.forEach { (chip, posTag) ->
                chip.setOnClickListener {
                    selectedPosTag = posTag
                    updatePosSelection(chip)
                }
            }

            tvSelectedTopicDropdown.setOnClickListener {
                val topics = VocabSessionStore.getTopics()
                val names = topics.map { it.name }.toTypedArray()
                MaterialAlertDialogBuilder(activity)
                    .setItems(names) { _, which ->
                        selectedTopic = topics[which]
                        tvSelectedTopicDropdown.text = selectedTopic.name
                    }
                    .show()
            }

            btnInlineCreateTopic.setOnClickDebounced {
                showCreateTopicDialog(activity) { newTopicName ->
                    selectedTopic = onCreateNewTopic(newTopicName)
                    tvSelectedTopicDropdown.text = selectedTopic.name
                }
            }

            btnConfirmAddWord.setOnClickDebounced {
                val english = etVocabEnglish.text?.toString()?.trim().orEmpty()
                val meaning = etVocabMeaning.text?.toString()?.trim().orEmpty()
                val phonetic = etVocabPhonetic.text?.toString()?.trim().orEmpty()

                when {
                    english.isBlank() -> {
                        tvAddWordError.text = activity.getString(R.string.vocab_err_word_empty)
                        tvAddWordError.visible()
                        etVocabEnglish.requestFocus()
                    }
                    meaning.isBlank() -> {
                        tvAddWordError.text = activity.getString(R.string.vocab_err_meaning_empty)
                        tvAddWordError.visible()
                        etVocabMeaning.requestFocus()
                    }
                    else -> {
                        dialog.dismiss()
                        onSaveWord(selectedTopic.id, english, meaning, phonetic, selectedPosTag)
                    }
                }
            }
        }
        dialog.show()
    }
}
