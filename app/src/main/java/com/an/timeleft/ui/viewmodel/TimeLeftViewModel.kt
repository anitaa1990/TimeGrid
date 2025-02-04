package com.an.timeleft.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.an.timeleft.R
import com.an.timeleft.data.LeftUiModel
import com.an.timeleft.data.UiString.ResourceStringWithArgs
import com.an.timeleft.util.TimeLeftUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TimeLeftViewModel: ViewModel() {
    private val daysLeftInYearModel: LeftUiModel = LeftUiModel(
        title = TimeLeftUtil.getCurrentYear().toString(),
        totalTime = TimeLeftUtil.getTotalDaysInYear(),
        timeCompleted = TimeLeftUtil.getDaysCompletedInYear(),
        timeLeftString = ResourceStringWithArgs(
            R.string.time_left_in_days, TimeLeftUtil.getDaysLeftInYear()
        )
    )

    private val daysLeftInMonthModel: LeftUiModel = LeftUiModel(
        title = TimeLeftUtil.getCurrentMonth().toString(),
        totalTime = TimeLeftUtil.getTotalDaysInMonth(),
        timeCompleted = TimeLeftUtil.getDaysCompletedInMonth(),
        timeLeftString = ResourceStringWithArgs(
            R.string.time_left_in_days, TimeLeftUtil.getDaysLeftInMonth()
        )
    )

    private val _currentUiState = MutableStateFlow<LeftUiModel>(daysLeftInYearModel)
    val currentUiState = _currentUiState.asStateFlow()

    // Private flag to track of format (Days or Percentage)
    private var isTimeLeftInPercentageFormat = false

    fun onTitleClicked() {
        _currentUiState.update { currentState ->
            if (currentState.title == daysLeftInYearModel.title) {
                daysLeftInMonthModel
            } else daysLeftInYearModel
        }
    }

    fun onTimeLeftClicked() {
        _currentUiState.update { currentState ->
            val newTimeLeftString = if (isTimeLeftInPercentageFormat) {
                // If currently in percentage mode, switch to days mode
                ResourceStringWithArgs(
                    R.string.time_left_in_days,
                    if (currentState.title == daysLeftInYearModel.title) {
                        TimeLeftUtil.getDaysLeftInYear()
                    } else {
                        TimeLeftUtil.getDaysLeftInMonth()
                    }
                )
            } else {
                // If currently in days mode, switch to percentage mode
                ResourceStringWithArgs(
                    R.string.time_left_in_percent,
                    if (currentState.title == daysLeftInYearModel.title) {
                        TimeLeftUtil.getPercentageDaysLeftInYear()
                    } else {
                        TimeLeftUtil.getPercentageDaysLeftInMonth()
                    }
                )
            }

            isTimeLeftInPercentageFormat = !isTimeLeftInPercentageFormat

            currentState.copy(timeLeftString = newTimeLeftString)
        }
    }
}