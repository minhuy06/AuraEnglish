package com.example.auraenglish.vocab.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityCustomTopicDetailBinding
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.vocab.adapter.VocabWordAdapter
import com.example.auraenglish.vocab.data.VocabFilter
import com.example.auraenglish.vocab.helper.VocabDialogHelper
import com.example.auraenglish.vocab.viewmodel.TopicDetailUiState
import com.example.auraenglish.vocab.viewmodel.VocabViewModel

class CustomTopicDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomTopicDetailBinding
    private val viewModel: VocabViewModel by viewModels()
    private lateinit var wordAdapter: VocabWordAdapter
    private val topicId: String by lazy { intent.getStringExtra(EXTRA_TOPIC_ID).orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCustomTopicDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (topicId.isBlank()) {
            finish()
            return
        }

        setupViews()
        observeViewModel()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTopicDetail(topicId)
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootCustomTopicDetail) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        wordAdapter = VocabWordAdapter(
            onWordClick = { word -> viewModel.toggleWordLearned(word) },
            onSpeakClick = { word ->
                showToast(getString(R.string.vocab_toast_speaking_format, word.word))
            }
        )

        rvTopicWords.apply {
            layoutManager = LinearLayoutManager(this@CustomTopicDetailActivity)
            setHasFixedSize(true)
            adapter = wordAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.detailState.observe(this) { state ->
            bindDetailState(state)
        }
    }

    private fun bindDetailState(state: TopicDetailUiState) = with(binding) {
        tvDetailTopicName.text = state.topic.name
        tvDetailTopicSubtitle.text = getString(R.string.vocab_detail_subtitle_format, state.allCount)
        tvSummaryTotalWords.text = getString(R.string.vocab_summary_total_format, state.allCount)
        tvSummaryLearnedWords.text = getString(R.string.vocab_summary_learned_format, state.learnedCount)
        pbDetailTopicProgress.progress = state.topic.progressPercent

        tabFilterAll.text = getString(R.string.vocab_tab_all_format, state.allCount)
        tabFilterUnlearned.text = getString(R.string.vocab_tab_unlearned_format, state.unlearnedCount)
        tabFilterLearned.text = getString(R.string.vocab_tab_learned_format, state.learnedCount)

        styleTab(tabFilterAll, state.filter == VocabFilter.ALL)
        styleTab(tabFilterUnlearned, state.filter == VocabFilter.UNLEARNED)
        styleTab(tabFilterLearned, state.filter == VocabFilter.LEARNED)

        wordAdapter.submitList(state.filteredWords)
        tvEmptyWords.visibleIf(state.filteredWords.isEmpty())
    }

    private fun styleTab(tab: TextView, active: Boolean) {
        tab.setBackgroundResource(if (active) R.drawable.bg_vocab_tab_active else R.drawable.bg_vocab_tab_inactive)
        tab.setTextColor(
            ContextCompat.getColor(
                this,
                if (active) R.color.white else R.color.vocab_text_secondary
            )
        )
    }

    private fun setupListeners() = with(binding) {
        btnBackTopicDetail.setOnClickListener { finish() }

        edtSearchWord.doAfterTextChanged { text ->
            viewModel.setWordSearchQuery(text?.toString().orEmpty())
        }

        tabFilterAll.setOnClickListener { viewModel.setWordFilter(VocabFilter.ALL) }
        tabFilterUnlearned.setOnClickListener { viewModel.setWordFilter(VocabFilter.UNLEARNED) }
        tabFilterLearned.setOnClickListener { viewModel.setWordFilter(VocabFilter.LEARNED) }

        btnStudyNow.setOnClickDebounced {
            val total = viewModel.detailState.value?.allCount ?: 0
            if (total == 0) {
                showToast(R.string.vocab_toast_no_words_to_study)
                return@setOnClickDebounced
            }
            startActivity(
                Intent(this@CustomTopicDetailActivity, FlashcardStudyActivity::class.java)
                    .putExtra(FlashcardStudyActivity.EXTRA_TOPIC_ID, topicId)
            )
        }

        btnAddNewWord.setOnClickDebounced {
            val currentTopic = viewModel.detailState.value?.topic ?: return@setOnClickDebounced
            VocabDialogHelper.showAddWordDialog(
                activity = this@CustomTopicDetailActivity,
                defaultTopic = currentTopic,
                onCreateNewTopic = { newName -> viewModel.createTopic(newName) },
                onSaveWord = { targetTopicId, word, meaning, phonetic, posTag ->
                    viewModel.addWord(targetTopicId, word, meaning, phonetic, posTag)
                    showToast(getString(R.string.vocab_toast_word_added, word))
                }
            )
        }
    }

    companion object {
        const val EXTRA_TOPIC_ID = "extra_vocab_topic_id"
    }
}
