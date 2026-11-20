package com.example.auraenglish.grammar.data

data class GrammarQuestionUiModel(
    val id: Int,
    val number: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationRule: String,
    val explanationExample: String
)

data class GrammarNavItemUiModel(
    val questionIndex: Int,
    val number: Int,
    val isCorrect: Boolean,
    val isSelected: Boolean
)

object GrammarSessionStore {

    const val EXAM_TITLE = "Đề số 01"

    val questions: List<GrammarQuestionUiModel> = listOf(
        GrammarQuestionUiModel(
            id = 1,
            number = 1,
            question = "If I ___ more time, I would travel around the world.",
            options = listOf("have", "had", "will have", "would have"),
            correctIndex = 1,
            explanationRule = "Câu điều kiện loại 2 diễn tả giả định không có thật ở hiện tại: If + S + V2/ed (quá khứ đơn), S + would + V nguyên mẫu.",
            explanationExample = "VD: If I had enough money, I would buy a new laptop."
        ),
        GrammarQuestionUiModel(
            id = 2,
            number = 2,
            question = "She has worked at this company ___ 2021.",
            options = listOf("for", "since", "during", "from"),
            correctIndex = 1,
            explanationRule = "Thì hiện tại hoàn thành dùng 'since' trước một mốc thời gian cụ thể (2021), còn 'for' dùng trước khoảng thời gian.",
            explanationExample = "VD: They have lived in Da Nang since 2019."
        ),
        GrammarQuestionUiModel(
            id = 3,
            number = 3,
            question = "The quarterly report ___ by the marketing team yesterday.",
            options = listOf("prepared", "was prepared", "is preparing", "has prepared"),
            correctIndex = 1,
            explanationRule = "Câu bị động ở thì quá khứ đơn (có dấu hiệu 'yesterday' và 'by...'): S + was/were + V3/ed.",
            explanationExample = "VD: The email was sent to all clients this morning."
        ),
        GrammarQuestionUiModel(
            id = 4,
            number = 4,
            question = "Despite ___ heavily, we arrived at the conference on time.",
            options = listOf("it rained", "rain", "raining", "rained"),
            correctIndex = 2,
            explanationRule = "Sau giới từ 'Despite / In spite of' ta dùng Danh từ (Noun) hoặc Danh động từ (V-ing), không dùng mệnh đề.",
            explanationExample = "VD: Despite feeling tired, she finished the assignment."
        ),
        GrammarQuestionUiModel(
            id = 5,
            number = 5,
            question = "Mr. Minh is the manager ___ approved our new project proposal.",
            options = listOf("which", "whom", "who", "whose"),
            correctIndex = 2,
            explanationRule = "Đại từ quan hệ 'who' thay thế cho danh từ chỉ người (the manager) và đóng vai trò chủ ngữ trước động từ 'approved'.",
            explanationExample = "VD: The engineer who designed this app won first prize."
        )
    )

    val userAnswers: MutableMap<Int, Int> = mutableMapOf()

    fun resetSession() {
        userAnswers.clear()
    }
}
