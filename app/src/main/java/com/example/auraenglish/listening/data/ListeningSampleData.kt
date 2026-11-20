package com.example.auraenglish.listening.data

import com.example.auraenglish.R

enum class ListenStatus { TODO, DOING, DONE }

data class ListeningExam(
    val id: String,
    val index: String,
    val title: String,
    val meta: String = "10 câu · Dictation",
    var status: ListenStatus = ListenStatus.TODO,
    var answeredCount: Int = 0,
    var correctCount: Int = 0,
    val questions: List<ListeningQuestion>
)

data class ListeningQuestion(
    val number: Int,
    val audioRes: Int,
    /** Đáp án đúng, từng từ (đã bỏ dấu câu cuối từ nếu cần so khớp). */
    val answerWords: List<String>,
    /** Câu đầy đủ để tạo pattern *** và so khớp. */
    val answerText: String,
    var userAnswer: String = "",
    var submitted: Boolean = false,
    var wordResults: List<Boolean> = emptyList()
) {
    val blankPattern: String
        get() = answerWords.joinToString(" ") { word ->
            val letters = word.filter { it.isLetterOrDigit() }
            val stars = "*".repeat(letters.length.coerceAtLeast(1))
            val punct = word.filterNot { it.isLetterOrDigit() }
            stars + punct
        }
}

object ListeningSampleData {

    const val EXTRA_EXAM_ID = "extra_listen_exam_id"
    const val EXTRA_QUESTION = "extra_listen_question"

    private fun q(number: Int, answer: String) = ListeningQuestion(
        number = number,
        audioRes = R.raw.listen_sample,
        answerWords = tokenize(answer),
        answerText = answer
    )

    private fun tokenize(text: String): List<String> =
        text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

    private fun tenQuestions() = listOf(
        q(1, "This audio program has been produced by YBM."),
        q(2, "All rights reserved."),
        q(3, "This audio program has been produced by YBM."),
        q(4, "All rights reserved."),
        q(5, "This audio program has been produced by YBM."),
        q(6, "All rights reserved."),
        q(7, "This audio program has been produced by YBM."),
        q(8, "All rights reserved."),
        q(9, "This audio program has been produced by YBM."),
        q(10, "All rights reserved.")
    )

    val exams = mutableListOf(
        ListeningExam(
            id = "listen_01",
            index = "01",
            title = "NGHE DICTATION 01",
            status = ListenStatus.TODO,
            questions = tenQuestions()
        ),
        ListeningExam(
            id = "listen_02",
            index = "02",
            title = "NGHE DICTATION 02",
            status = ListenStatus.TODO,
            questions = tenQuestions()
        ),
        ListeningExam(
            id = "listen_03",
            index = "03",
            title = "NGHE DICTATION 03",
            status = ListenStatus.DOING,
            answeredCount = 2,
            questions = tenQuestions().also { list ->
                list[0].userAnswer = "This audio program has been produced by YBM."
                list[0].submitted = true
                list[0].wordResults = list[0].answerWords.map { true }
                list[1].userAnswer = "All right reserved."
                list[1].submitted = true
                list[1].wordResults = listOf(true, false, true)
            }
        )
    )

    fun examById(id: String): ListeningExam =
        exams.firstOrNull { it.id == id } ?: exams.first()

    fun refreshProgress(exam: ListeningExam) {
        exam.answeredCount = exam.questions.count { it.submitted }
        exam.correctCount = exam.questions.count { q ->
            q.submitted && q.wordResults.isNotEmpty() && q.wordResults.all { it }
        }
        exam.status = when {
            exam.answeredCount == 0 -> ListenStatus.TODO
            exam.answeredCount >= exam.questions.size -> ListenStatus.DONE
            else -> ListenStatus.DOING
        }
    }

    /** So khớp từng từ (không phân biệt hoa thường, bỏ dấu câu). */
    fun checkAnswer(question: ListeningQuestion, input: String): List<Boolean> {
        val expected = question.answerWords.map { normalize(it) }
        val actual = tokenize(input).map { normalize(it) }
        return expected.mapIndexed { i, word ->
            actual.getOrNull(i) == word
        }
    }

    private fun normalize(word: String): String =
        word.lowercase().filter { it.isLetterOrDigit() }
}
