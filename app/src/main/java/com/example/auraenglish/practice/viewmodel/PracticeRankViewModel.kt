package com.example.auraenglish.practice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.practice.data.PracticeRankData
import com.example.auraenglish.practice.data.RankEntry
import com.example.auraenglish.practice.data.RankPeriod

data class RankUiState(
    val period: RankPeriod = RankPeriod.WEEK,
    val podium: List<RankEntry> = emptyList(),
    val listRows: List<RankEntry> = emptyList(),
    val me: RankEntry? = null,
    val aboveMe: RankEntry? = null
)

class PracticeRankViewModel : ViewModel() {

    private val _uiState = MutableLiveData(RankUiState())
    val uiState: LiveData<RankUiState> = _uiState

    init {
        setPeriod(RankPeriod.WEEK)
    }

    fun setPeriod(period: RankPeriod) {
        val entries = PracticeRankData.entries(period)
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
