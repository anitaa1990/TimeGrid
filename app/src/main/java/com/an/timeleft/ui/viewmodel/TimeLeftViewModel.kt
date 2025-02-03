package com.an.timeleft.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.an.timeleft.data.LeftUiModel
import com.an.timeleft.util.TimeLeftUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimeLeftViewModel: ViewModel() {
    private val daysLeftInYearModel: LeftUiModel = LeftUiModel(
        title = TimeLeftUtil.getCurrentYear().toString(),
        totalTime = TimeLeftUtil.getTotalDaysInYear(),
        timeCompleted = TimeLeftUtil.getDaysCompletedInYear(),
        timeLeft = TimeLeftUtil.getDaysLeftInYear(),
        timeLeftInPercentage = TimeLeftUtil.getPercentageDaysLeftInYear(),
        isTimeLeftInDays = true
    )

    private val daysLeftInMonthModel: LeftUiModel = LeftUiModel(
        title = TimeLeftUtil.getCurrentMonth().toString(),
        totalTime = TimeLeftUtil.getTotalDaysInMonth(),
        timeCompleted = TimeLeftUtil.getDaysCompletedInMonth(),
        timeLeft = TimeLeftUtil.getDaysLeftInMonth(),
        timeLeftInPercentage = TimeLeftUtil.getPercentageDaysLeftInMonth(),
        isTimeLeftInDays = true
    )

    private val _currentUiState = MutableStateFlow<LeftUiModel>(daysLeftInYearModel)
    val currentUiState = _currentUiState.asStateFlow()

    fun onTitleClicked() {
        when (_currentUiState.value.title) {
            daysLeftInYearModel.title -> {
                _currentUiState.update { daysLeftInMonthModel }
            }
            daysLeftInMonthModel.title -> {
                _currentUiState.update { daysLeftInYearModel }
            }
        }
    }

    fun onTimeLeftClicked() {
        _currentUiState.value = _currentUiState.value.copy(
            isTimeLeftInDays = !_currentUiState.value.isTimeLeftInDays
        )
    }
}