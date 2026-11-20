package com.example.auraenglish.vocab.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityFlashcardStudyBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.vocab.data.VocabSessionStore
import com.example.auraenglish.vocab.data.VocabWordUiModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class FlashcardStudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardStudyBinding
    private val topicId: String by lazy { intent.getStringExtra(EXTRA_TOPIC_ID).orEmpty() }
    private var studyList: List<VocabWordUiModel> = emptyList()
    private var currentIndex: Int = 0
    private var isFlipped: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFlashcardStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studyList = VocabSessionStore.getWordsByTopic(topicId)
        if (studyList.isEmpty()) {
            showToast(R.string.vocab_toast_no_words_to_study)
            finish()
            return
        }

        currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        isFlipped = savedInstanceState?.getBoolean(KEY_FLIPPED, false) ?: false

        setupViews()
        bindCurrentCard()
        setupListeners()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, currentIndex)
        outState.putBoolean(KEY_FLIPPED, isFlipped)
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootFlashcardStudy) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        tvFlashcardTopicTitle.text = VocabSessionStore.getTopicById(topicId)?.name.orEmpty()
    }

    private fun bindCurrentCard() = with(binding) {
        val word = studyList[currentIndex]
        val step = currentIndex + 1
        val total = studyList.size

        tvFlashcardCounter.text = getString(R.string.vocab_flashcard_progress_format, step, total)
        pbFlashcardProgress.progress = ((step * 100f) / total).toInt()

        tvFlashcardWord.text = word.word
        tvFlashcardPhonetic.text = word.phonetic
        tvFlashcardPosTag.text = word.posTag
        tvIllustrationEmoji.text = word.emoji
        tvIllustrationCaption.text = word.word
        tvFlashcardMeaning.text = word.meaning
        tvFlashcardExample.text = word.example

        updateFlipState()
    }

    private fun updateFlipState() = with(binding) {
        layoutFrontIllustration.visibleIf(!isFlipped)
        layoutBackMeaning.visibleIf(isFlipped)
        tvFlipHint.setText(
            if (isFlipped) R.string.vocab_flashcard_tap_to_front
            else R.string.vocab_flashcard_tap_to_flip
        )
    }

    private fun setupListeners() = with(binding) {
        btnBackFlashcard.setOnClickListener { finish() }

        btnFlashcardSpeak.setOnClickListener {
            val currentWord = studyList[currentIndex].word
            showToast(getString(R.string.vocab_toast_speaking_format, currentWord))
        }

        cardFlashcardMain.setOnClickListener {
            isFlipped = !isFlipped
            updateFlipState()
        }

        btnSrsUnlearned.setOnClickDebounced(300L) { handleSrsDecision(learned = false) }
        btnSrsLearned.setOnClickDebounced(300L) { handleSrsDecision(learned = true) }
    }

    private fun handleSrsDecision(learned: Boolean) {
        val current = studyList[currentIndex]
        VocabSessionStore.setWordLearned(current.id, learned)

        if (currentIndex + 1 < studyList.size) {
            currentIndex++
            isFlipped = false
            bindCurrentCard()
        } else {
            showCompletedDialog()
        }
    }

    private fun showCompletedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.vocab_flashcard_completed_title)
            .setMessage(R.string.vocab_flashcard_completed_msg)
            .setCancelable(false)
            .setNegativeButton(R.string.vocab_flashcard_btn_restart) { dialog, _ ->
                dialog.dismiss()
                currentIndex = 0
                isFlipped = false
                bindCurrentCard()
            }
            .setPositiveButton(R.string.vocab_flashcard_btn_finish) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    companion object {
        const val EXTRA_TOPIC_ID = "extra_flashcard_topic_id"
        private const val KEY_INDEX = "key_flashcard_index"
        private const val KEY_FLIPPED = "key_flashcard_flipped"
    }
}
