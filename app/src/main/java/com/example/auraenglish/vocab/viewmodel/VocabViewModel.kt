package com.example.auraenglish.vocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.vocab.data.CustomTopicUiModel
import com.example.auraenglish.vocab.data.VocabFilter
import com.example.auraenglish.vocab.data.VocabSessionStore
import com.example.auraenglish.vocab.data.VocabWordUiModel

data class TopicDetailUiState(
    val topic: CustomTopicUiModel,
    val filter: VocabFilter = VocabFilter.ALL,
    val filteredWords: List<VocabWordUiModel> = emptyList(),
    val allCount: Int = 0,
    val unlearnedCount: Int = 0,
    val learnedCount: Int = 0
)

class VocabViewModel : ViewModel() {

    private val _topics = MutableLiveData<List<CustomTopicUiModel>>()
    val topics: LiveData<List<CustomTopicUiModel>> = _topics

    private val _detailState = MutableLiveData<TopicDetailUiState>()
    val detailState: LiveData<TopicDetailUiState> = _detailState

    private var topicQuery: String = ""
    private var activeTopicId: String = ""
    private var wordQuery: String = ""
    private var activeFilter: VocabFilter = VocabFilter.ALL

    fun loadTopics() {
        val all = VocabSessionStore.getTopics()
        val q = topicQuery.trim()
        _topics.value = if (q.isBlank()) {
            all
        } else {
            all.filter { it.name.contains(q, ignoreCase = true) }
        }
    }

    fun setTopicSearchQuery(query: String) {
        topicQuery = query
        loadTopics()
    }

    fun createTopic(name: String): CustomTopicUiModel {
        val created = VocabSessionStore.addTopic(name)
        loadTopics()
        return created
    }

    fun renameTopic(topicId: String, newName: String) {
        VocabSessionStore.renameTopic(topicId, newName)
        loadTopics()
    }

    fun deleteTopic(topicId: String) {
        VocabSessionStore.deleteTopic(topicId)
        loadTopics()
    }

    fun loadTopicDetail(topicId: String) {
        activeTopicId = topicId
        emitTopicDetail()
    }

    fun setWordSearchQuery(query: String) {
        wordQuery = query
        emitTopicDetail()
    }

    fun setWordFilter(filter: VocabFilter) {
        activeFilter = filter
        emitTopicDetail()
    }

    fun addWord(topicId: String, word: String, meaning: String, phonetic: String, posTag: String) {
        VocabSessionStore.addWord(topicId, word, meaning, phonetic, posTag)
        if (activeTopicId == topicId) emitTopicDetail()
        loadTopics()
    }

    fun toggleWordLearned(word: VocabWordUiModel) {
        VocabSessionStore.setWordLearned(word.id, !word.isLearned)
        emitTopicDetail()
    }

    private fun emitTopicDetail() {
        val topic = VocabSessionStore.getTopicById(activeTopicId) ?: return
        val allWords = VocabSessionStore.getWordsByTopic(activeTopicId)
        val q = wordQuery.trim()

        val filtered = allWords.filter { item ->
            val matchFilter = when (activeFilter) {
                VocabFilter.ALL -> true
                VocabFilter.UNLEARNED -> !item.isLearned
                VocabFilter.LEARNED -> item.isLearned
            }
            val matchQuery = q.isBlank() ||
                item.word.contains(q, ignoreCase = true) ||
                item.meaning.contains(q, ignoreCase = true)
            matchFilter && matchQuery
        }

        _detailState.value = TopicDetailUiState(
            topic = topic,
            filter = activeFilter,
            filteredWords = filtered,
            allCount = allWords.size,
            unlearnedCount = allWords.count { !it.isLearned },
            learnedCount = allWords.count { it.isLearned }
        )
    }
}
