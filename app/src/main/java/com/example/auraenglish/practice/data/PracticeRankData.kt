package com.example.auraenglish.practice.data

import com.example.auraenglish.R

enum class RankPeriod { DAY, WEEK, MONTH, YEAR }

data class RankEntry(
    val rank: Int,
    val name: String,
    val initials: String,
    val xp: Int,
    val avatarColorRes: Int,
    val isCurrentUser: Boolean = false
)

object PracticeRankData {

    fun entries(period: RankPeriod): List<RankEntry> {
        val boost = when (period) {
            RankPeriod.DAY -> 0
            RankPeriod.WEEK -> 120
            RankPeriod.MONTH -> 480
            RankPeriod.YEAR -> 2100
        }
        return listOf(
            RankEntry(1, "Tuấn Long", "TL", 2950 + boost, R.color.practice_avatar_tl),
            RankEntry(2, "Minh Anh", "MA", 2180 + boost, R.color.practice_avatar_ma),
            RankEntry(3, "Quốc Bảo", "QB", 2000 + boost, R.color.practice_avatar_qb),
            RankEntry(4, "Gia Huy", "GH", 2120 + boost, R.color.practice_avatar_gh),
            RankEntry(5, "Ngọc Trâm", "NT", 2020 + boost, R.color.practice_avatar_nt),
            RankEntry(
                rank = 6,
                name = "Duy Hưng",
                initials = "DH",
                xp = 1920 + boost,
                avatarColorRes = R.color.practice_avatar_dh,
                isCurrentUser = true
            ),
            RankEntry(7, "Quốc Khánh", "QK", 1420 + boost, R.color.practice_avatar_qk)
        )
    }

    fun formatXp(xp: Int): String =
        xp.toString()
            .reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
}
