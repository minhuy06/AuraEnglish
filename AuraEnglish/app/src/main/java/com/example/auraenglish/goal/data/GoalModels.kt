package com.example.auraenglish.goal.data

enum class QuestionType {
    GRAMMAR,
    VOCAB,
    READING,
    LISTENING
}

data class GoalItem(
    val id: String,
    val letter: String,
    val title: String,
    val description: String,
    val letterColorRes: Int,
    var isSelected: Boolean = false
)

data class AssessmentQuestion(
    val id: Int,
    val number: Int,
    val type: QuestionType,
    val question: String,
    val passage: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    var selectedOptionIndex: Int? = null
)

data class AssessmentResultSummary(
    val totalQuestions: Int = 20,
    val correctCount: Int = 15,
    val wrongCount: Int = 3,
    val skippedCount: Int = 2,
    val score: Int = 72,
    val level: String = "B1 - Trình độ trung cấp",
    val vocabScore: Int = 75,
    val grammarScore: Int = 60,
    val listeningScore: Int = 55,
    val readingScore: Int = 68,
    val recommendedRoadmap: String = "Tiếng Anh công sở",
    val recommendedDuration: String = "Bắt đầu từ B1 · 8 tuần",
    val strengthText: String = "Từ vựng · Đọc hiểu\nNắm vững ý chính và vốn từ khá tốt.",
    val improveText: String = "Nghe · Ngữ pháp\nLuyện nghe 10 phút và ôn cấu trúc câu mỗi ngày."
)

object AssessmentSessionStore {

    var selectedGoalId: String = "daily"

    val questions: List<AssessmentQuestion> = listOf(
        // Câu 1 - Ngữ pháp
        AssessmentQuestion(
            id = 1,
            number = 1,
            type = QuestionType.GRAMMAR,
            question = "If I ___ more time, I would travel around the world.",
            options = listOf("have", "had", "will have", "would have"),
            correctIndex = 1
        ),
        // Câu 2 - Từ vựng
        AssessmentQuestion(
            id = 2,
            number = 2,
            type = QuestionType.VOCAB,
            question = "The company is looking for a candidate with excellent communication ___.",
            options = listOf("skills", "tools", "habits", "methods"),
            correctIndex = 0
        ),
        // Câu 3 - Ngữ pháp
        AssessmentQuestion(
            id = 3,
            number = 3,
            type = QuestionType.GRAMMAR,
            question = "She has worked at this company ___ 2021.",
            options = listOf("for", "since", "during", "from"),
            correctIndex = 1
        ),
        // Câu 4 - Từ vựng
        AssessmentQuestion(
            id = 4,
            number = 4,
            type = QuestionType.VOCAB,
            question = "Please submit the financial report before the upcoming ___.",
            options = listOf("deadline", "finish", "ending", "delay"),
            correctIndex = 0
        ),
        // Câu 5 - Ngữ pháp
        AssessmentQuestion(
            id = 5,
            number = 5,
            type = QuestionType.GRAMMAR,
            question = "The quarterly report ___ by the marketing team yesterday.",
            options = listOf("prepared", "was prepared", "is preparing", "has prepared"),
            correctIndex = 1
        ),
        // Câu 6 - Từ vựng
        AssessmentQuestion(
            id = 6,
            number = 6,
            type = QuestionType.VOCAB,
            question = "We need to ___ our strategy to reach more customers this quarter.",
            options = listOf("adjust", "refuse", "cancel", "postpone"),
            correctIndex = 0
        ),
        // Câu 7 - Ngữ pháp (như mockup)
        AssessmentQuestion(
            id = 7,
            number = 7,
            type = QuestionType.GRAMMAR,
            question = "She __ to work by bus every day.",
            options = listOf("go", "goes", "going", "gone"),
            correctIndex = 1
        ),
        // Câu 8 - Từ vựng
        AssessmentQuestion(
            id = 8,
            number = 8,
            type = QuestionType.VOCAB,
            question = "The manager offered an apology for the unexpected ___ in delivery.",
            options = listOf("delay", "slow", "wait", "hold"),
            correctIndex = 0
        ),
        // Câu 9 - Ngữ pháp
        AssessmentQuestion(
            id = 9,
            number = 9,
            type = QuestionType.GRAMMAR,
            question = "Despite ___ heavily, we arrived at the conference on time.",
            options = listOf("it rained", "rain", "raining", "rained"),
            correctIndex = 2
        ),
        // Câu 10 - Từ vựng
        AssessmentQuestion(
            id = 10,
            number = 10,
            type = QuestionType.VOCAB,
            question = "All employees are entitled to 14 days of paid annual ___.",
            options = listOf("leave", "vacation", "rest", "stop"),
            correctIndex = 0
        ),
        // Câu 11 - Nghe / Từ vựng
        AssessmentQuestion(
            id = 11,
            number = 11,
            type = QuestionType.LISTENING,
            question = "What time does the morning flight to Da Nang depart?",
            options = listOf("At 8:30 AM", "At 9:45 AM", "Tomorrow afternoon", "In terminal 2"),
            correctIndex = 0
        ),
        // Câu 12 - Ngữ pháp
        AssessmentQuestion(
            id = 12,
            number = 12,
            type = QuestionType.GRAMMAR,
            question = "Mr. Minh is the manager ___ approved our new project proposal.",
            options = listOf("which", "whom", "who", "whose"),
            correctIndex = 2
        ),
        // Câu 13 - Từ vựng
        AssessmentQuestion(
            id = 13,
            number = 13,
            type = QuestionType.VOCAB,
            question = "Regular exercise and a balanced diet help ___ your immune system.",
            options = listOf("boost", "increase", "rise", "grow"),
            correctIndex = 0
        ),
        // Câu 14 - Ngữ pháp
        AssessmentQuestion(
            id = 14,
            number = 14,
            type = QuestionType.GRAMMAR,
            question = "You ___ wear a helmet when riding a motorbike in Vietnam.",
            options = listOf("must", "might", "could", "would"),
            correctIndex = 0
        ),
        // Câu 15 - Ngữ pháp
        AssessmentQuestion(
            id = 15,
            number = 15,
            type = QuestionType.GRAMMAR,
            question = "Neither John nor his colleagues ___ available for the meeting today.",
            options = listOf("is", "are", "be", "was"),
            correctIndex = 1
        ),
        // Câu 16 - Đọc hiểu
        AssessmentQuestion(
            id = 16,
            number = 16,
            type = QuestionType.READING,
            question = "What is the main purpose of this email notice?",
            passage = "Dear Team, please be reminded that our office will undergo system maintenance this Saturday from 9 AM to 3 PM. Network services will be temporarily unavailable.",
            options = listOf(
                "To announce office maintenance",
                "To reschedule a meeting",
                "To introduce a new employee",
                "To celebrate a company anniversary"
            ),
            correctIndex = 0
        ),
        // Câu 17 - Đọc hiểu
        AssessmentQuestion(
            id = 17,
            number = 17,
            type = QuestionType.READING,
            question = "When will the network services be unavailable?",
            passage = "Dear Team, please be reminded that our office will undergo system maintenance this Saturday from 9 AM to 3 PM. Network services will be temporarily unavailable.",
            options = listOf(
                "Saturday 9 AM to 3 PM",
                "Friday all day",
                "Sunday morning",
                "Next Monday afternoon"
            ),
            correctIndex = 0
        ),
        // Câu 18 - Đọc hiểu
        AssessmentQuestion(
            id = 18,
            number = 18,
            type = QuestionType.READING,
            question = "What is requested of all workshop attendees?",
            passage = "Welcome to the Digital Marketing Workshop. All attendees are kindly requested to bring their laptops and install the required software before 8:30 AM.",
            options = listOf(
                "Bring laptops with required software",
                "Pay the entrance fee in cash",
                "Submit a printed presentation",
                "Arrive after 9:00 AM"
            ),
            correctIndex = 0
        ),
        // Câu 19 - Đọc hiểu (như mockup)
        AssessmentQuestion(
            id = 19,
            number = 19,
            type = QuestionType.READING,
            question = "Why does Emma study English?",
            passage = "Emma studies English every evening because she wants to work abroad.",
            options = listOf(
                "To work abroad",
                "To meet her friends",
                "To watch television",
                "To learn mathematics"
            ),
            correctIndex = 0
        ),
        // Câu 20 - Đọc hiểu
        AssessmentQuestion(
            id = 20,
            number = 20,
            type = QuestionType.READING,
            question = "How often does Emma study English?",
            passage = "Emma studies English every evening because she wants to work abroad.",
            options = listOf(
                "Every evening",
                "Only on weekends",
                "Once a month",
                "Every morning"
            ),
            correctIndex = 0
        )
    )

    val userAnswers: MutableMap<Int, Int> = mutableMapOf(
        // Pre-fill some answers so it feels active and matches the 18/20 answered popup in mockup
        1 to 1, 2 to 0, 3 to 1, 4 to 0, 5 to 1, 6 to 0, 7 to 1,
        8 to 0, 9 to 2, 10 to 0, 11 to 0, 12 to 2, 13 to 0, 14 to 0,
        15 to 1, 16 to 0, 17 to 0, 19 to 0
        // Notice questions 18 and 20 are unanswered (2 skipped, 18 answered)
    )

    fun resetSession() {
        userAnswers.clear()
        // Prepopulate slightly to demonstrate mockup flow
        userAnswers.putAll(
            mapOf(
                1 to 1, 2 to 0, 3 to 1, 4 to 0, 5 to 1, 6 to 0, 7 to 1,
                8 to 0, 9 to 2, 10 to 0, 11 to 0, 12 to 2, 13 to 0, 14 to 0,
                15 to 1, 16 to 0, 17 to 0, 19 to 0
            )
        )
    }

    fun getResultSummary(): AssessmentResultSummary {
        val total = questions.size
        var correct = 0
        var wrong = 0
        var skipped = 0

        questions.forEach { q ->
            val ans = userAnswers[q.id]
            when {
                ans == null -> skipped++
                ans == q.correctIndex -> correct++
                else -> wrong++
            }
        }

        val score = ((correct.toFloat() / total) * 100).toInt()

        return AssessmentResultSummary(
            totalQuestions = total,
            correctCount = correct,
            wrongCount = wrong,
            skippedCount = skipped,
            score = score,
            level = if (score >= 80) "B2 - Trình độ nâng cao" else if (score >= 60) "B1 - Trình độ trung cấp" else "A2 - Trình độ cơ bản",
            vocabScore = 75,
            grammarScore = 60,
            listeningScore = 55,
            readingScore = 68
        )
    }
}
