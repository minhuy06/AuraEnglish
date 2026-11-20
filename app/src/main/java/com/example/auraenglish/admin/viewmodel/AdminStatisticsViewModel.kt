package com.example.auraenglish.admin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.auraenglish.admin.data.AdminStatisticsPeriod
import com.example.auraenglish.admin.data.AdminStatisticsSampleData
import com.example.auraenglish.admin.data.AdminStatisticsUiModel

class AdminStatisticsViewModel : ViewModel() {

    private val _state = MutableLiveData(
        AdminStatisticsSampleData.get(AdminStatisticsPeriod.WEEK)
    )
    val state: LiveData<AdminStatisticsUiModel> = _state

    fun setPeriod(period: AdminStatisticsPeriod) {
        if (_state.value?.period == period) return
        _state.value = AdminStatisticsSampleData.get(period)
    }
}
