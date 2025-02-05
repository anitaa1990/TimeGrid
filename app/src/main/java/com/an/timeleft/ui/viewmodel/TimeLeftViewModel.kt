package com.an.timeleft.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.timeleft.R
import com.an.timeleft.data.LeftCategory
import com.an.timeleft.data.LeftDataStore
import com.an.timeleft.data.LeftUiModel
import com.an.timeleft.data.UiString
import com.an.timeleft.data.UiString.ResourceString
import com.an.timeleft.data.UiString.ResourceStringWithArgs
import com.an.timeleft.util.TimeLeftUtil
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
class TimeLeftViewModel @Inject constructor(
    private val dataStore: LeftDataStore
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
        title = TimeLeftUtil.getCurrentYear().toString(),
        totalTime = TimeLeftUtil.getTotalDaysInYear(),
        timeCompleted = TimeLeftUtil.getDaysCompletedInYear(),
        timeLeftString = getTimeLeftInYear()
    )

    private fun getLeftUiModelForMonth() = LeftUiModel(
        category = LeftCategory.Month,
        title = TimeLeftUtil.getCurrentMonth().toString(),
        totalTime = TimeLeftUtil.getTotalDaysInMonth(),
        timeCompleted = TimeLeftUtil.getDaysCompletedInMonth(),
        timeLeftString = getTimeLeftInMonth()
    )

    private fun getLeftUiModelForLife() = LeftUiModel(
        category = LeftCategory.Life,
        title = LeftCategory.Life.name,
        totalTime = TimeLeftUtil.getTotalMonthsInLife(),
        timeCompleted = getTimeCompletedInLife(),
        timeLeftString = getTimeLeftInLife()
    )

    private fun updateDate(dateInMillis: Long?) {
        birthDateInMillis = dateInMillis
        birthDate = birthDateInMillis?.toString()?.toLocalDate()
    }

    private fun getTimeLeftInYear() = if (isTimeLeftInPercentageFormat) {
        ResourceStringWithArgs(R.string.time_left_in_percent, TimeLeftUtil.getPercentageDaysLeftInYear())
    } else ResourceStringWithArgs(R.string.time_left_in_days, TimeLeftUtil.getDaysLeftInYear())

    private fun getTimeLeftInMonth() = if (isTimeLeftInPercentageFormat) {
        ResourceStringWithArgs(R.string.time_left_in_percent, TimeLeftUtil.getPercentageDaysLeftInMonth())
    } else ResourceStringWithArgs(R.string.time_left_in_days, TimeLeftUtil.getDaysLeftInMonth())

    private fun getTimeCompletedInLife() = birthDate?.let {
        TimeLeftUtil.getTotalMonthsCompletedInLife(it)
    } ?: TimeLeftUtil.getTotalMonthsInLife()

    private fun getTimeLeftInLife(): UiString {
        return birthDate?.let {
            if (isTimeLeftInPercentageFormat) {
                ResourceStringWithArgs(R.string.time_left_in_percent, TimeLeftUtil.getPercentageMonthsLeftInLife(it))
            } else ResourceStringWithArgs(R.string.time_left_in_months, TimeLeftUtil.getTotalMonthsLeftInLife(it))
        } ?: ResourceString(R.string.text_birth_date)
    }
}