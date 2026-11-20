package com.example.auraenglish.ranking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.ranking.data.RankEntry
import com.example.auraenglish.ranking.data.RankPeriod
import com.example.auraenglish.ranking.data.RankingData

// State UI: podium + list + sticky
data class RankUiState(
    val period: RankPeriod = RankPeriod.WEEK,
    val podium: List<RankEntry> = emptyList(), // top 1–3
    val listRows: List<RankEntry> = emptyList(), // hạng 4+
    val me: RankEntry? = null,
    val aboveMe: RankEntry? = null // tính gap XP
)

// Đổi period → rebuild uiState
class RankingViewModel : ViewModel() {

    private val _uiState = MutableLiveData(RankUiState())
    val uiState: LiveData<RankUiState> = _uiState

    init {
        setPeriod(RankPeriod.WEEK) // mặc định chip Tuần
    }

    fun setPeriod(period: RankPeriod) {
        val entries = RankingData.entries(period)
        val me = entries.firstOrNull { it.isCurrentUser }
        val above = me?.let { entries.firstOrNull { r -> r.rank == it.rank - 1 } }
        _uiState.value = RankUiState(
            period = period,
            podium = entries.take(3),
            listRows = entries.drop(3),
            me = me,
            aboveMe = above
        )
    }
}
