package com.example.auraenglish.vocab.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class VocabFilter { ALL, UNLEARNED, LEARNED }

data class VocabWordUiModel(
    val id: String,
    val topicId: String,
    val word: String,
    val phonetic: String,
    val posTag: String,
    val meaning: String,
    val example: String,
    val emoji: String = "📘",
    val isLearned: Boolean = false
)

data class CustomTopicUiModel(
    val id: String,
    val name: String,
    val createdDate: String,
    val wordCount: Int,
    val learnedCount: Int
) {
    val progressPercent: Int
        get() = if (wordCount == 0) 0 else ((learnedCount * 100f) / wordCount).toInt()
}

object VocabSessionStore {

    private data class TopicMeta(val id: String, val name: String, val createdDate: String)

    private val topicMetas = mutableListOf(
        TopicMeta("t_office", "Từ vựng công sở", "12/03/2026"),
        TopicMeta("t_it", "Chuyên ngành IT", "10/03/2026"),
        TopicMeta("t_interview", "Từ vựng phỏng vấn", "05/03/2026"),
        TopicMeta("t_comm", "Tiếng Anh giao tiếp", "01/03/2026")
    )

    private val words = mutableListOf(
        VocabWordUiModel("w1", "t_office", "Negotiate", "/nɪˈɡəʊʃieɪt/", "(v) Động từ", "Đàm phán, thương lượng", "We will negotiate the contract terms tomorrow.", "🤝", false),
        VocabWordUiModel("w2", "t_office", "Schedule", "/ˈʃedʒ.uːl/", "(n) Danh từ", "Lịch trình, thời gian biểu", "Please check the meeting schedule carefully.", "📅", true),
        VocabWordUiModel("w3", "t_office", "Contract", "/ˈkɒn.trækt/", "(n) Danh từ", "Hợp đồng", "Both parties signed the contract yesterday.", "📄", true),
        VocabWordUiModel("w4", "t_office", "Salary", "/ˈsæl.ər.i/", "(n) Danh từ", "Tiền lương", "She received a competitive salary offer.", "💰", false),
        VocabWordUiModel("w5", "t_office", "Sociable", "/ˈsəʊ.ʃə.bəl/", "(adj) Tính từ", "Hòa đồng, thân thiện", "He is very sociable and easy to work with.", "😊", true),
        VocabWordUiModel("w6", "t_office", "Founder", "/ˈfaʊn.dər/", "(n) Danh từ", "Người sáng lập", "The founder shared the company vision.", "🚀", false),
        VocabWordUiModel("w7", "t_it", "Algorithm", "/ˈæl.ɡə.rɪ.ðəm/", "(n) Danh từ", "Thuật toán", "This search algorithm runs in O(log n).", "💻", true),
        VocabWordUiModel("w8", "t_it", "Deploy", "/dɪˈplɔɪ/", "(v) Động từ", "Triển khai hệ thống", "We will deploy the new release tonight.", "☁️", false),
        VocabWordUiModel("w9", "t_interview", "Strength", "/streŋθ/", "(n) Danh từ", "Điểm mạnh", "Problem-solving is my biggest strength.", "⭐", true),
        VocabWordUiModel("w10", "t_comm", "Sorry", "/ˈsɒr.i/", "(adj) Tính từ", "Xin lỗi, lấy làm tiếc", "I am sorry for the late reply.", "🙏", true)
    )

    fun getTopics(): List<CustomTopicUiModel> = topicMetas.map { meta ->
        val topicWords = words.filter { it.topicId == meta.id }
        CustomTopicUiModel(
            id = meta.id,
            name = meta.name,
            createdDate = meta.createdDate,
            wordCount = topicWords.size,
            learnedCount = topicWords.count { it.isLearned }
        )
    }

    fun getTopicById(topicId: String): CustomTopicUiModel? = getTopics().find { it.id == topicId }

    fun getWordsByTopic(topicId: String): List<VocabWordUiModel> = words.filter { it.topicId == topicId }

    fun addTopic(name: String): CustomTopicUiModel {
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val meta = TopicMeta("t_${UUID.randomUUID().toString().take(6)}", name.trim(), date)
        topicMetas.add(0, meta)
        return CustomTopicUiModel(meta.id, meta.name, meta.createdDate, 0, 0)
    }

    fun renameTopic(topicId: String, newName: String) {
        val index = topicMetas.indexOfFirst { it.id == topicId }
        if (index >= 0) {
            topicMetas[index] = topicMetas[index].copy(name = newName.trim())
        }
    }

    fun deleteTopic(topicId: String) {
        topicMetas.removeAll { it.id == topicId }
        words.removeAll { it.topicId == topicId }
    }

    fun addWord(
        topicId: String,
        word: String,
        meaning: String,
        phonetic: String,
        posTag: String
    ): VocabWordUiModel {
        val item = VocabWordUiModel(
            id = "w_${UUID.randomUUID().toString().take(6)}",
            topicId = topicId,
            word = word.trim(),
            phonetic = phonetic.trim().ifBlank { "/.../" },
            posTag = posTag,
            meaning = meaning.trim(),
            example = "Example sentence with \"${word.trim()}\"."
        )
        words.add(0, item)
        return item
    }

    fun setWordLearned(wordId: String, learned: Boolean) {
        val index = words.indexOfFirst { it.id == wordId }
        if (index >= 0) {
            words[index] = words[index].copy(isLearned = learned)
        }
    }
}
