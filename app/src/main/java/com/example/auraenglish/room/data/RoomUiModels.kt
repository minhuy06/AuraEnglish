package com.example.auraenglish.room.data

import java.util.Locale
import kotlin.random.Random

enum class RoomTopic(val label: String) {
    TOEIC("TOEIC"),
    GRAMMAR("Ngữ pháp")
}

data class RoomMemberUiModel(
    val id: String,
    val name: String,
    val initials: String,
    val isHost: Boolean = false,
    val isCurrentUser: Boolean = false,
    val isReady: Boolean = true
)

sealed class RoomSlotItem {
    data class Filled(val member: RoomMemberUiModel) : RoomSlotItem()
    data class Empty(val slotIndex: Int) : RoomSlotItem()
}

data class RoomQuestionUiModel(
    val id: Int,
    val number: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

data class RoomRankEntryUiModel(
    val rank: Int,
    val member: RoomMemberUiModel,
    val correctCount: Int,
    val totalQuestions: Int,
    val score: Int,
    val elapsedSeconds: Int
) {
    val formattedTime: String
        get() {
            val safeSeconds = elapsedSeconds.coerceAtLeast(0)
            val minutes = safeSeconds / 60
            val seconds = safeSeconds % 60
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
}

data class RoomSessionConfig(
    val code: String,
    val roomName: String,
    val topic: RoomTopic,
    val questionCount: Int,
    val durationMinutes: Int
)

enum class InviteError {
    EMPTY,
    DUPLICATE,
    ROOM_FULL
}

sealed class InviteResult {
    data class Success(val member: RoomMemberUiModel) : InviteResult()
    data class Failure(val error: InviteError) : InviteResult()
}

object RoomSessionStore {

    const val MAX_MEMBERS = 4

    private const val DEFAULT_ROOM_CODE = "AURA88"
    private const val DEFAULT_ROOM_NAME = "Phòng thi của Duy Hưng"
    private const val CURRENT_USER_ID = "user_current"
    private const val CURRENT_USER_NAME = "Duy Hưng"
    private const val CURRENT_USER_INITIALS = "DH"
    private const val ROOM_CODE_LENGTH = 6
    private const val ROOM_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    private const val POINTS_PER_CORRECT = 100

    private var memberIdCounter = 1

    val friendSuggestions: List<String> = listOf(
        "Minh Anh",
        "Gia Bảo",
        "Thanh Trúc",
        "Quốc Khánh",
        "Hoàng Nam"
    )

    var currentRoom: RoomSessionConfig = RoomSessionConfig(
        code = DEFAULT_ROOM_CODE,
        roomName = DEFAULT_ROOM_NAME,
        topic = RoomTopic.TOEIC,
        questionCount = 5,
        durationMinutes = 5
    )

    val members: MutableList<RoomMemberUiModel> = mutableListOf(
        createCurrentUserMember(isHost = true)
    )

    val userAnswers: MutableMap<Int, Int> = mutableMapOf()

    var lastRankingResults: List<RoomRankEntryUiModel> = emptyList()

    fun createRoom(
        roomName: String,
        topic: RoomTopic,
        questionCount: Int,
        durationMinutes: Int
    ): RoomSessionConfig {
        val resolvedName = roomName.trim().ifEmpty { DEFAULT_ROOM_NAME }
        val config = RoomSessionConfig(
            code = generateRoomCode(),
            roomName = resolvedName,
            topic = topic,
            questionCount = questionCount,
            durationMinutes = durationMinutes
        )
        currentRoom = config
        members.clear()
        members.add(createCurrentUserMember(isHost = true))
        userAnswers.clear()
        lastRankingResults = emptyList()
        return config
    }

    fun joinRoomByCode(code: String): RoomSessionConfig {
        val normalizedCode = code.trim().uppercase().ifEmpty { DEFAULT_ROOM_CODE }
        val config = RoomSessionConfig(
            code = normalizedCode,
            roomName = "Phòng luyện TOEIC #$normalizedCode",
            topic = RoomTopic.TOEIC,
            questionCount = 5,
            durationMinutes = 5
        )
        currentRoom = config
        members.clear()
        members.add(
            RoomMemberUiModel(
                id = "host_minh_anh",
                name = "Minh Anh",
                initials = "MA",
                isHost = true,
                isCurrentUser = false,
                isReady = true
            )
        )
        members.add(createCurrentUserMember(isHost = false))
        userAnswers.clear()
        lastRankingResults = emptyList()
        return config
    }

    fun inviteMember(rawUsername: String): InviteResult {
        val trimmed = rawUsername.trim()
        if (trimmed.isEmpty()) {
            return InviteResult.Failure(InviteError.EMPTY)
        }
        if (members.size >= MAX_MEMBERS) {
            return InviteResult.Failure(InviteError.ROOM_FULL)
        }
        if (members.any { it.name.equals(trimmed, ignoreCase = true) }) {
            return InviteResult.Failure(InviteError.DUPLICATE)
        }
        val newMember = RoomMemberUiModel(
            id = "member_${++memberIdCounter}",
            name = trimmed,
            initials = computeInitials(trimmed),
            isHost = false,
            isCurrentUser = false,
            isReady = true
        )
        members.add(newMember)
        return InviteResult.Success(newMember)
    }

    fun removeMember(memberId: String) {
        members.removeAll { it.id == memberId && !it.isHost && !it.isCurrentUser }
    }

    fun buildSlots(): List<RoomSlotItem> {
        val slots = mutableListOf<RoomSlotItem>()
        val filledMembers = members.take(MAX_MEMBERS)
        for (member in filledMembers) {
            slots.add(RoomSlotItem.Filled(member))
        }
        for (index in filledMembers.size until MAX_MEMBERS) {
            slots.add(RoomSlotItem.Empty(slotIndex = index))
        }
        return slots
    }

    fun getQuestionsForCurrentRoom(): List<RoomQuestionUiModel> {
        val bank = when (currentRoom.topic) {
            RoomTopic.TOEIC -> toeicQuestions
            RoomTopic.GRAMMAR -> grammarQuestions
        }
        val limit = currentRoom.questionCount.coerceIn(1, bank.size)
        return bank.take(limit)
    }

    fun resetExamAnswers() {
        userAnswers.clear()
    }

    fun finishExamAndBuildRankings(elapsedSeconds: Int): List<RoomRankEntryUiModel> {
        val questions = getQuestionsForCurrentRoom()
        val totalQuestions = questions.size
        val userCorrectCount = questions.count { userAnswers[it.id] == it.correctIndex }
        val safeUserElapsed = elapsedSeconds.coerceAtLeast(0)
        val maxDurationSeconds = (currentRoom.durationMinutes * 60).coerceAtLeast(60)
        val minSimulatedSeconds = (maxDurationSeconds / 4).coerceAtLeast(25)

        val rawEntries = members.map { member ->
            if (member.isCurrentUser) {
                RoomRankEntryUiModel(
                    rank = 0,
                    member = member,
                    correctCount = userCorrectCount,
                    totalQuestions = totalQuestions,
                    score = userCorrectCount * POINTS_PER_CORRECT,
                    elapsedSeconds = safeUserElapsed
                )
            } else {
                val minCorrect = (totalQuestions / 2).coerceAtLeast(1)
                val simulatedCorrect = Random.nextInt(minCorrect, totalQuestions + 1)
                val simulatedElapsed = Random.nextInt(minSimulatedSeconds, maxDurationSeconds + 1)
                RoomRankEntryUiModel(
                    rank = 0,
                    member = member,
                    correctCount = simulatedCorrect,
                    totalQuestions = totalQuestions,
                    score = simulatedCorrect * POINTS_PER_CORRECT,
                    elapsedSeconds = simulatedElapsed
                )
            }
        }

        val ranked = rawEntries
            .sortedWith(
                compareByDescending<RoomRankEntryUiModel> { it.score }
                    .thenBy { it.elapsedSeconds }
            )
            .mapIndexed { index, entry ->
                entry.copy(rank = index + 1)
            }

        lastRankingResults = ranked
        return ranked
    }

    private fun createCurrentUserMember(isHost: Boolean): RoomMemberUiModel =
        RoomMemberUiModel(
            id = CURRENT_USER_ID,
            name = CURRENT_USER_NAME,
            initials = CURRENT_USER_INITIALS,
            isHost = isHost,
            isCurrentUser = true,
            isReady = true
        )

    private fun generateRoomCode(): String =
        buildString(ROOM_CODE_LENGTH) {
            repeat(ROOM_CODE_LENGTH) {
                append(ROOM_CODE_CHARS[Random.nextInt(ROOM_CODE_CHARS.length)])
            }
        }

    private fun computeInitials(name: String): String {
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts.first().first()}${parts.last().first()}".uppercase()
            parts.size == 1 -> parts.first().take(2).uppercase()
            else -> "?"
        }
    }

    private val toeicQuestions: List<RoomQuestionUiModel> = listOf(
        RoomQuestionUiModel(
            id = 101,
            number = 1,
            question = "All employees must submit their expense reports ___ Friday afternoon.",
            options = listOf("by", "until", "since", "during"),
            correctIndex = 0
        ),
        RoomQuestionUiModel(
            id = 102,
            number = 2,
            question = "The marketing director was very ___ with the results of the product launch.",
            options = listOf("satisfy", "satisfying", "satisfied", "satisfaction"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 103,
            number = 3,
            question = "Please review the attached invoice ___ and notify us of any discrepancies.",
            options = listOf("care", "careful", "carefully", "carefulness"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 104,
            number = 4,
            question = "Because the conference room was fully booked, the meeting was ___ to Tuesday.",
            options = listOf("postponed", "attended", "cancelled", "delivered"),
            correctIndex = 0
        ),
        RoomQuestionUiModel(
            id = 105,
            number = 5,
            question = "Neither the sales manager ___ the regional director could attend the ceremony.",
            options = listOf("or", "and", "nor", "but"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 106,
            number = 6,
            question = "Customers who register before May 15 will receive a 20% ___ on their first order.",
            options = listOf("refund", "discount", "receipt", "deposit"),
            correctIndex = 1
        ),
        RoomQuestionUiModel(
            id = 107,
            number = 7,
            question = "The new software update significantly ___ the speed of data processing.",
            options = listOf("improves", "improving", "improvement", "improvable"),
            correctIndex = 0
        ),
        RoomQuestionUiModel(
            id = 108,
            number = 8,
            question = "Ms. Park is responsible ___ coordinating all international shipments.",
            options = listOf("to", "with", "for", "about"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 109,
            number = 9,
            question = "Although the proposal was well written, the board decided not to ___ it.",
            options = listOf("approve", "approval", "approved", "approving"),
            correctIndex = 0
        ),
        RoomQuestionUiModel(
            id = 110,
            number = 10,
            question = "The renovated lobby will be open to visitors ___ construction is completed next week.",
            options = listOf("once", "despite", "during", "however"),
            correctIndex = 0
        )
    )

    private val grammarQuestions: List<RoomQuestionUiModel> = listOf(
        RoomQuestionUiModel(
            id = 201,
            number = 1,
            question = "If she ___ earlier, she would not have missed the opening keynote.",
            options = listOf("left", "had left", "leaves", "would leave"),
            correctIndex = 1
        ),
        RoomQuestionUiModel(
            id = 202,
            number = 2,
            question = "By the time we arrived at the station, the train ___.",
            options = listOf("already departed", "has already departed", "had already departed", "was departing"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 203,
            number = 3,
            question = "This is the most inspiring documentary I ___ this year.",
            options = listOf("watch", "watched", "have watched", "am watching"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 204,
            number = 4,
            question = "The annual budget report ___ by the finance committee tomorrow morning.",
            options = listOf("will review", "will be reviewed", "reviewed", "is reviewing"),
            correctIndex = 1
        ),
        RoomQuestionUiModel(
            id = 205,
            number = 5,
            question = "Despite ___ a tight deadline, the engineering team delivered the feature on time.",
            options = listOf("face", "faced", "facing", "they faced"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 206,
            number = 6,
            question = "The candidate ___ portfolio impressed the panel was offered the position.",
            options = listOf("who", "whom", "which", "whose"),
            correctIndex = 3
        ),
        RoomQuestionUiModel(
            id = 207,
            number = 7,
            question = "She suggested ___ a short break before starting the final review.",
            options = listOf("take", "to take", "taking", "taken"),
            correctIndex = 2
        ),
        RoomQuestionUiModel(
            id = 208,
            number = 8,
            question = "Not only ___ fluent in English, but he also speaks Japanese.",
            options = listOf("he is", "is he", "he was", "does he"),
            correctIndex = 1
        ),
        RoomQuestionUiModel(
            id = 209,
            number = 9,
            question = "We have been collaborating with that design studio ___ more than three years.",
            options = listOf("since", "for", "during", "from"),
            correctIndex = 1
        ),
        RoomQuestionUiModel(
            id = 210,
            number = 10,
            question = "I wish I ___ enough time to join the weekend workshop with you.",
            options = listOf("have", "had", "will have", "am having"),
            correctIndex = 1
        )
    )
}
