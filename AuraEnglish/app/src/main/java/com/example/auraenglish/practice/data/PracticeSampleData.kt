package com.example.auraenglish.practice.data

// Trạng thái đề / filter chip.
enum class ExamStatus { ALL, TODO, DOING, DONE }

// Model 1 đề TOEIC (list + overview).
data class PracticeExam(
    val id: String,
    val index: String,
    val title: String,
    val meta: String = "200 câu - 120 phút Listening + Reading",
    var status: ExamStatus,
    var answeredCount: Int = 0,
    var score: Int = 0,
    val parts: List<PracticePart>
)

// 1 Part trong đề
data class PracticePart(
    val number: Int,
    val title: String,
    val done: Int,
    val total: Int,
    val colorResName: String
)

// 1 câu Part 5.
data class PracticeQuestion(
    val number: Int,
    val tag: String,
    val stem: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val translation: String,
    var userAnswerIndex: Int? = null,
    var flagged: Boolean = false
) {
    val isAnswered: Boolean get() = userAnswerIndex != null
}

// Từ đã lưu khi bôi đen trên stem.
data class SavedWord(
    val word: String,
    val fromQuestion: Int,
    val topic: String = "Từ vựng công sở",
    val phonetic: String = "",
    val wordType: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

// Kho dữ liệu mẫu in-memory (chưa có database).
object PracticeSampleData {

    const val EXTRA_EXAM_ID = "extra_exam_id"
    const val EXTRA_QUESTION_NUMBER = "extra_question_number"
    const val EXTRA_PART_NUMBER = "extra_part_number"

    var currentExamId: String = "exam_01"

    // Sổ từ vựng in-memory
    private val _savedWords = linkedMapOf<String, SavedWord>()
    val savedWords: List<SavedWord> get() = _savedWords.values.toList().asReversed()

    private val _topics = linkedSetOf("Từ vựng công sở", "Ngữ pháp TOEIC", "Luyện đề ngữ pháp")
    val topics: List<String> get() = _topics.toList()

    fun addTopic(raw: String): Boolean {
        val name = raw.trim().replace(Regex("\\s+"), " ")
        if (name.isEmpty()) return false
        val exists = _topics.any { it.equals(name, ignoreCase = true) }
        if (exists) return false
        _topics.add(name)
        return true
    }

    fun addWord(
        raw: String,
        fromQuestion: Int,
        topic: String = topics.first(),
        phonetic: String = "",
        wordType: String = ""
    ): Boolean {
        val word = raw.trim().replace(Regex("\\s+"), " ")
        if (word.isEmpty()) return false
        val key = word.lowercase()
        if (_savedWords.containsKey(key)) return false
        val topicName = topic.ifBlank { topics.first() }
        if (_topics.none { it.equals(topicName, ignoreCase = true) }) {
            _topics.add(topicName)
        }
        _savedWords[key] = SavedWord(
            word = word,
            fromQuestion = fromQuestion,
            topic = topicName,
            phonetic = phonetic.trim(),
            wordType = wordType.trim()
        )
        return true
    }

    val exams: List<PracticeExam> = listOf(
        PracticeExam(
            id = "exam_01",
            index = "01",
            title = "ĐỀ THI THỬ 01",
            status = ExamStatus.DOING,
            answeredCount = 45,
            parts = defaultParts(doneBias = true)
        ),
        PracticeExam(
            id = "exam_03",
            index = "03",
            title = "ĐỀ THI THỬ 03",
            status = ExamStatus.DOING,
            answeredCount = 45,
            parts = defaultParts(doneBias = true)
        ),
        PracticeExam(
            id = "exam_04",
            index = "04",
            title = "ĐỀ THI THỬ 04",
            status = ExamStatus.TODO,
            answeredCount = 0,
            parts = defaultParts(doneBias = false)
        ),
        PracticeExam(
            id = "exam_05",
            index = "05",
            title = "ĐỀ THI THỬ 05",
            status = ExamStatus.TODO,
            answeredCount = 0,
            parts = defaultParts(doneBias = false)
        ),
        PracticeExam(
            id = "exam_06",
            index = "06",
            title = "ĐỀ THI THỬ 06",
            status = ExamStatus.DONE,
            answeredCount = 200,
            score = 720,
            parts = defaultParts(doneBias = true, allDone = true)
        )
    )

    // Part 5 sample questions (số câu theo Figma quanh 9–15)
    val part5Questions: MutableList<PracticeQuestion> = mutableListOf(
        PracticeQuestion(
            number = 9,
            tag = "Ngữ pháp - Từ loại",
            stem = "The manager asked everyone to submit the report ______ Friday.",
            options = listOf("by", "on", "at", "in"),
            correctIndex = 0,
            explanation = "\"By Friday\" dùng để chỉ hạn chót.",
            translation = "Quản lý yêu cầu mọi người nộp báo cáo trước thứ Sáu.",
            userAnswerIndex = 0,
            flagged = false
        ),
        PracticeQuestion(
            number = 10,
            tag = "Từ vựng - Động từ",
            stem = "Our company plans to ______ its services to overseas markets.",
            options = listOf("expand", "expanding", "expanded", "expansion"),
            correctIndex = 0,
            explanation = "Sau \"to\" là động từ nguyên mẫu: expand.",
            translation = "Công ty chúng tôi dự định mở rộng dịch vụ ra thị trường nước ngoài.",
            userAnswerIndex = 0,
            flagged = false
        ),
        PracticeQuestion(
            number = 11,
            tag = "Ngữ pháp - Từ loại",
            stem = "The new policy has been ______ by all department managers.",
            options = listOf("approved", "approving", "approve", "approval"),
            correctIndex = 0,
            explanation = "Bị động hiện tại hoàn thành: has been + V3 → approved.",
            translation = "Chính sách mới đã được tất cả trưởng phòng phê duyệt.",
            userAnswerIndex = 2,
            flagged = true
        ),
        PracticeQuestion(
            number = 12,
            tag = "Ngữ pháp - Động từ",
            stem = "The marketing team will ______ the new product to our clients next Monday.",
            options = listOf("present", "presents", "presenting", "presented"),
            correctIndex = 0,
            explanation = "Sau will dùng V nguyên mẫu: present.",
            translation = "Đội marketing sẽ trình bày sản phẩm mới cho khách hàng vào thứ Hai tới.",
            userAnswerIndex = 0,
            flagged = true
        ),
        PracticeQuestion(
            number = 13,
            tag = "Từ vựng - Danh từ",
            stem = "Please check the ______ before signing the contract.",
            options = listOf("details", "detailing", "detailed", "detailer"),
            correctIndex = 0,
            explanation = "Cần danh từ số nhiều: details.",
            translation = "Vui lòng kiểm tra các chi tiết trước khi ký hợp đồng.",
            userAnswerIndex = null,
            flagged = false
        ),
        PracticeQuestion(
            number = 14,
            tag = "Từ vựng - giới từ",
            stem = "Please submit your expense report ______ Friday afternoon.",
            options = listOf("by", "until", "since", "during"),
            correctIndex = 0,
            explanation = "\"By Friday afternoon\" = trước hoặc vào chiều thứ Sáu.",
            translation = "Vui lòng nộp báo cáo chi phí trước chiều thứ Sáu.",
            userAnswerIndex = null,
            flagged = true
        ),
        PracticeQuestion(
            number = 15,
            tag = "Ngữ pháp - Thì",
            stem = "She ______ in this company for five years.",
            options = listOf("has worked", "works", "working", "worked"),
            correctIndex = 0,
            explanation = "Hiện tại hoàn thành với for + khoảng thời gian: has worked.",
            translation = "Cô ấy đã làm việc ở công ty này được năm năm.",
            userAnswerIndex = null,
            flagged = false
        )
    )

    data class ResultSummary(
        val score: Int = 685,
        val correct: Int = 148,
        val wrong: Int = 42,
        val skipped: Int = 10,
        val total: Int = 200,
        val listening: Int = 340,
        val reading: Int = 345,
        val timeText: String = "108:32",
        val partResults: List<Pair<Int, Int>> = listOf(
            5 to 6, 21 to 25, 28 to 39, 22 to 30, 24 to 30, 11 to 16, 37 to 54
        )
    )

    val result = ResultSummary()

    // Tìm đề theo id (fallback đề đầu)
    fun examById(id: String): PracticeExam =
        exams.firstOrNull { it.id == id } ?: exams.first()

    fun filteredExams(status: ExamStatus): List<PracticeExam> =
        if (status == ExamStatus.ALL) exams else exams.filter { it.status == status }

    // Câu đã flag; filter TODO = chưa trả lời, DONE = đã trả lời.
    fun flaggedQuestions(filter: ExamStatus = ExamStatus.ALL): List<PracticeQuestion> {
        val flagged = part5Questions.filter { it.flagged }
        return when (filter) {
            ExamStatus.TODO -> flagged.filter { !it.isAnswered }
            ExamStatus.DONE -> flagged.filter { it.isAnswered }
            else -> flagged
        }
    }

    fun questionByNumber(number: Int): PracticeQuestion? =
        part5Questions.firstOrNull { it.number == number }

    // Lưu đáp án user (0=A … 3=D).
    fun setAnswer(number: Int, optionIndex: Int) {
        part5Questions.find { it.number == number }?.userAnswerIndex = optionIndex
    }

    // Bật/tắt flag; trả về trạng thái sau khi toggle.
    fun toggleFlag(number: Int): Boolean {
        val q = part5Questions.find { it.number == number } ?: return false
        q.flagged = !q.flagged
        return q.flagged
    }

    // Tạo 7 Part mẫu; doneBias / allDone điều khiển progress demo.
    private fun defaultParts(doneBias: Boolean, allDone: Boolean = false): List<PracticePart> {
        val totals = listOf(6, 25, 39, 30, 30, 15, 54)
        val titles = listOf(
            "Part 1: Mô tả hình ảnh",
            "Part 2: Hỏi - đáp",
            "Part 3: Đoạn hội thoại",
            "Part 4: Bài nói ngắn",
            "Part 5: Hoàn thành câu",
            "Part 6: Hoàn thành đoạn văn",
            "Part 7: Đọc hiểu"
        )
        val colors = listOf(
            "practice_part1", "practice_part2", "practice_part3", "practice_part4",
            "practice_part5", "practice_part6", "practice_part7"
        )
        val dones = when {
            allDone -> totals
            doneBias -> listOf(6, 18, 12, 0, 11, 0, 5)
            else -> List(7) { 0 }
        }
        return totals.indices.map { i ->
            PracticePart(i + 1, titles[i], dones[i], totals[i], colors[i])
        }
    }
}
