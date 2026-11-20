package com.example.auraenglish.vocab.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.auraenglish.R
import com.example.auraenglish.databinding.ActivityCustomTopicListBinding
import com.example.auraenglish.practice.helper.PracticeNavHelper
import com.example.auraenglish.practice.helper.PracticeTab
import com.example.auraenglish.practice.utils.setOnClickDebounced
import com.example.auraenglish.practice.utils.showToast
import com.example.auraenglish.practice.utils.visibleIf
import com.example.auraenglish.vocab.adapter.CustomTopicAdapter
import com.example.auraenglish.vocab.data.CustomTopicUiModel
import com.example.auraenglish.vocab.helper.VocabDialogHelper
import com.example.auraenglish.vocab.viewmodel.VocabViewModel

class CustomTopicListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomTopicListBinding
    private val viewModel: VocabViewModel by viewModels()
    private lateinit var topicAdapter: CustomTopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCustomTopicListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTopics()
    }

    private fun setupViews() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(rootCustomTopicList) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        PracticeNavHelper.bind(this@CustomTopicListActivity, includeBottomNav, PracticeTab.LIBRARY)

        topicAdapter = CustomTopicAdapter(
            onTopicClick = { topic ->
                startActivity(
                    Intent(this@CustomTopicListActivity, CustomTopicDetailActivity::class.java)
                        .putExtra(CustomTopicDetailActivity.EXTRA_TOPIC_ID, topic.id)
                )
            },
            onMoreClick = { topic, anchor -> showTopicMenu(topic, anchor) }
        )

        rvCustomTopics.apply {
            layoutManager = LinearLayoutManager(this@CustomTopicListActivity)
            setHasFixedSize(true)
            adapter = topicAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.topics.observe(this) { list ->
            topicAdapter.submitList(list)
            binding.tvEmptyTopics.visibleIf(list.isEmpty())
        }
    }

    private fun setupListeners() = with(binding) {
        edtSearchTopic.doAfterTextChanged { text ->
            viewModel.setTopicSearchQuery(text?.toString().orEmpty())
        }

        btnCreateNewTopic.setOnClickDebounced {
            VocabDialogHelper.showCreateTopicDialog(this@CustomTopicListActivity) { name ->
                viewModel.createTopic(name)
                showToast(getString(R.string.vocab_toast_topic_created, name))
            }
        }
    }

    private fun showTopicMenu(topic: CustomTopicUiModel, anchor: View) {
        PopupMenu(this, anchor).apply {
            menu.add(0, 1, 0, R.string.vocab_menu_rename_topic)
            menu.add(0, 2, 1, R.string.vocab_menu_delete_topic)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        VocabDialogHelper.showCreateTopicDialog(this@CustomTopicListActivity, topic.name) { newName ->
                            viewModel.renameTopic(topic.id, newName)
                        }
                        true
                    }
                    2 -> {
                        viewModel.deleteTopic(topic.id)
                        showToast(getString(R.string.vocab_toast_topic_deleted, topic.name))
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }
}
