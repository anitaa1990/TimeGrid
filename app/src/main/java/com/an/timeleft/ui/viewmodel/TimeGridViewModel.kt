package com.an.timeleft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.timeleft.R
import com.an.timeleft.data.LeftCategory
import com.an.timeleft.data.TimeGridDataStore
import com.an.timeleft.data.LeftUiModel
import com.an.timeleft.data.UiString
import com.an.timeleft.data.UiString.ResourceString
import com.an.timeleft.data.UiString.ResourceStringWithArgs
import com.an.timeleft.util.TimeGridUtil
import com.an.timeleft.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TimeGridViewModel @Inject constructor(
    private val dataStore: TimeGridDataStore
): ViewModel() {
    private val _currentUiState = MutableStateFlow<LeftUiModel>(getLeftUiModelForYear())
    val currentUiState = _currentUiState.asStateFlow()

    private var birthDateInMillis: Long? = null
    private var birthDate: LocalDate? = null

    // Private flag to track of format (Days or Percentage)
    private var isTimeLeftInPercentageFormat = false

    private val _showDatePicker = MutableStateFlow(false) // UI event to trigger bottom sheet
    val showDatePicker = _showDatePicker.asStateFlow()

    init {
        viewModelScope.launch {
            updateDate(dataStore.birthDate.firstOrNull()?.toLong())
        }
    }

    fun getBirthDate() = birthDateInMillis

    fun onBirthDateSelected(dateInMillis: Long?) {
        dateInMillis?.let {
            updateDate(it)
            _currentUiState.update { getLeftUiModelForLife() }
            viewModelScope.launch { dataStore.storeBirthDate(it.toString()) }
        }
    }

    fun onDatePickerDismissed() {
        _showDatePicker.update { false }
    }

    fun onTitleClicked() {
        _currentUiState.update { currentState ->
            when (currentState.category) {
                LeftCategory.Year -> { getLeftUiModelForMonth() }
                LeftCategory.Month -> { getLeftUiModelForLife() }
                LeftCategory.Life -> { getLeftUiModelForYear() }
            }
        }
    }

    fun onTimeLeftClicked() {
        _currentUiState.update { currentState ->
            // If birthDate is null and category is Life, prompt user to add their birthday
            if (currentState.category == LeftCategory.Life && birthDateInMillis == null) {
                viewModelScope.launch { _showDatePicker.emit(true) } // Trigger UI event to show bottom sheet
                return@update currentState // Return current state without changes
            }

            isTimeLeftInPercentageFormat = !isTimeLeftInPercentageFormat

            val newTimeLeftString = when (currentState.category) {
                LeftCategory.Year -> { getTimeLeftInYear() }
                LeftCategory.Month -> { getTimeLeftInMonth() }
                LeftCategory.Life -> { getTimeLeftInLife() }
            }

            currentState.copy(timeLeftString = newTimeLeftString)
        }
    }

    private fun getLeftUiModelForYear() = LeftUiModel(
        category = LeftCategory.Year,
        title = TimeGridUtil.getCurrentYear().toString(),
        totalTime = TimeGridUtil.getTotalDaysInYear(),
        timeCompleted = TimeGridUtil.getDaysCompletedInYear(),
        timeLeftString = getTimeLeftInYear()
    )

    private fun getLeftUiModelForMonth() = LeftUiModel(
        category = LeftCategory.Month,
        title = TimeGridUtil.getCurrentMonth().toString(),
        totalTime = TimeGridUtil.getTotalDaysInMonth(),
        timeCompleted = TimeGridUtil.getDaysCompletedInMonth(),
        timeLeftString = getTimeLeftInMonth()
    )

    private fun getLeftUiModelForLife() = LeftUiModel(
        category = LeftCategory.Life,
        title = LeftCategory.Life.name,
        totalTime = TimeGridUtil.getTotalMonthsInLife(),
        timeCompleted = getTimeCompletedInLife(),
        timeLeftString = getTimeLeftInLife()
    )

    private fun updateDate(dateInMillis: Long?) {
        birthDateInMillis = dateInMillis
        birthDate = birthDateInMillis?.toString()?.toLocalDate()
    }

    private fun getTimeLeftInYear() = if (isTimeLeftInPercentageFormat) {
        ResourceStringWithArgs(R.string.time_left_in_percent, TimeGridUtil.getPercentageDaysLeftInYear())
    } else ResourceStringWithArgs(R.string.time_left_in_days, TimeGridUtil.getDaysLeftInYear())

    private fun getTimeLeftInMonth() = if (isTimeLeftInPercentageFormat) {
        ResourceStringWithArgs(R.string.time_left_in_percent, TimeGridUtil.getPercentageDaysLeftInMonth())
    } else ResourceStringWithArgs(R.string.time_left_in_days, TimeGridUtil.getDaysLeftInMonth())

    private fun getTimeCompletedInLife() = birthDate?.let {
        TimeGridUtil.getTotalMonthsCompletedInLife(it)
    } ?: TimeGridUtil.getTotalMonthsInLife()

    private fun getTimeLeftInLife(): UiString {
        return birthDate?.let {
            if (isTimeLeftInPercentageFormat) {
                ResourceStringWithArgs(R.string.time_left_in_percent, TimeGridUtil.getPercentageMonthsLeftInLife(it))
            } else ResourceStringWithArgs(R.string.time_left_in_months, TimeGridUtil.getTotalMonthsLeftInLife(it))
        } ?: ResourceString(R.string.text_birth_date)
    }
}