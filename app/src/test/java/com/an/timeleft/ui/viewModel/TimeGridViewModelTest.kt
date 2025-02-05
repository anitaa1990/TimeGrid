package com.an.timeleft.ui.viewModel

import com.an.timeleft.BaseUnitTest
import com.an.timeleft.data.LeftCategory
import com.an.timeleft.data.TimeGridDataStore
import com.an.timeleft.data.UiString.ResourceStringWithArgs
import com.an.timeleft.ui.viewmodel.TimeGridViewModel
import com.an.timeleft.util.TimeGridUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.an.timeleft.R
import com.an.timeleft.data.UiString.ResourceString
import com.an.timeleft.util.toLocalDate
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import java.time.LocalDate

@ExperimentalCoroutinesApi
class TimeGridViewModelTest: BaseUnitTest() {

    private lateinit var viewModel: TimeGridViewModel
    private val dataStore: TimeGridDataStore = mock()

    @Before
    fun setup() {
        whenever(dataStore.birthDate).thenReturn(flowOf(null))
        viewModel = TimeGridViewModel(dataStore)
    }

    @Test
    fun `initial state is correct`() = runTest {
        val initialState = viewModel.currentUiState.first()
        assertEquals(LeftCategory.Year, initialState.category)
        assertEquals(TimeGridUtil.getCurrentYear().toString(), initialState.title)
        assertEquals(TimeGridUtil.getTotalDaysInYear(), initialState.totalTime)
        assertEquals(TimeGridUtil.getDaysCompletedInYear(), initialState.timeCompleted)
        assertEquals(ResourceStringWithArgs(
            resId = R.string.time_left_in_days,
            args = TimeGridUtil.getDaysLeftInYear()
        ), initialState.timeLeftString)
        assertEquals(false, viewModel.showDatePicker.first())
    }

    @Test
    fun `onBirthDateSelected updates birth date and UI state correctly`() = runTest {
        assertNull(viewModel.getBirthDate()) // default birthDate is null

        val birthDateInMillis = 946684800000L // Jan 1, 2000
        val birthDate = LocalDate.ofEpochDay(birthDateInMillis / 86400000L)

        viewModel.onBirthDateSelected(birthDateInMillis)

        val updatedState = viewModel.currentUiState.first()
        assertEquals(LeftCategory.Life, updatedState.category)
        assertEquals(LeftCategory.Life.name, updatedState.title)
        assertEquals(TimeGridUtil.getTotalMonthsInLife(), updatedState.totalTime)
        assertEquals(TimeGridUtil.getTotalMonthsCompletedInLife(birthDate), updatedState.timeCompleted)
        assertEquals(
            ResourceStringWithArgs(
                resId = R.string.time_left_in_months,
                args = TimeGridUtil.getTotalMonthsLeftInLife(birthDate)
            ), updatedState.timeLeftString)
        assertNotNull(viewModel.getBirthDate())
        verify(dataStore).storeBirthDate(birthDateInMillis.toString())
    }

    @Test
    fun `onTitleClicked cycles through categories`() = runTest {
        viewModel.onTitleClicked() // Year -> Month
        var currentState = viewModel.currentUiState.first()
        assertEquals(LeftCategory.Month, currentState.category)
        assertEquals(TimeGridUtil.getCurrentMonth().toString(), currentState.title)
        assertEquals(TimeGridUtil.getTotalDaysInMonth(), currentState.totalTime)
        assertEquals(TimeGridUtil.getDaysCompletedInMonth(), currentState.timeCompleted)
        assertEquals(ResourceStringWithArgs(
            R.string.time_left_in_days,
            TimeGridUtil.getDaysLeftInMonth()
        ), currentState.timeLeftString)

        viewModel.onTitleClicked() // Month -> Life
        currentState = viewModel.currentUiState.first()
        assertEquals(LeftCategory.Life, currentState.category)
        assertEquals(LeftCategory.Life.name, currentState.title)
        assertEquals(TimeGridUtil.getTotalMonthsInLife(), currentState.totalTime)
        assertEquals(TimeGridUtil.getTotalMonthsInLife(), currentState.timeCompleted)
        assertEquals(ResourceString(R.string.text_birth_date), currentState.timeLeftString)

        viewModel.onTitleClicked() // Life -> Year
        currentState = viewModel.currentUiState.first()
        assertEquals(LeftCategory.Year, currentState.category)
        assertEquals(TimeGridUtil.getCurrentYear().toString(), currentState.title)
        assertEquals(TimeGridUtil.getTotalDaysInYear(), currentState.totalTime)
        assertEquals(TimeGridUtil.getDaysCompletedInYear(), currentState.timeCompleted)
        assertEquals(ResourceStringWithArgs(
            resId = R.string.time_left_in_days,
            args = TimeGridUtil.getDaysLeftInYear()
        ), currentState.timeLeftString)
    }

    @Test
    fun `onTimeLeftClicked toggles percentage format in all categories`() = runTest {
        var initialTimeLeft = viewModel.currentUiState.first().timeLeftString
        viewModel.onTimeLeftClicked()
        var toggledTimeLeft = viewModel.currentUiState.first().timeLeftString

        assertEquals(
            ResourceStringWithArgs(
                R.string.time_left_in_days,
                TimeGridUtil.getDaysLeftInYear()
            ), initialTimeLeft
        )
        assertEquals(
            ResourceStringWithArgs(
                R.string.time_left_in_percent,
                TimeGridUtil.getPercentageDaysLeftInYear()
            ), toggledTimeLeft)

        viewModel.onTitleClicked() // Move to the Month category

        initialTimeLeft = viewModel.currentUiState.first().timeLeftString
        viewModel.onTimeLeftClicked()
        toggledTimeLeft = viewModel.currentUiState.first().timeLeftString

        assertEquals(
            ResourceStringWithArgs(
                R.string.time_left_in_percent,
                TimeGridUtil.getPercentageDaysLeftInMonth()
            ), initialTimeLeft
        )
        assertEquals(
            ResourceStringWithArgs(
                R.string.time_left_in_days,
                TimeGridUtil.getDaysLeftInMonth()
            ), toggledTimeLeft)

        // Move to the Life category
        val birthDateInMillis = 946684800000L // Jan 1, 2000
        val birthDate = birthDateInMillis.toString().toLocalDate()
        viewModel.onBirthDateSelected(birthDateInMillis)

        initialTimeLeft = viewModel.currentUiState.first().timeLeftString
        viewModel.onTimeLeftClicked()
        toggledTimeLeft = viewModel.currentUiState.first().timeLeftString

        assertEquals(
            ResourceStringWithArgs(
                R.string.time_left_in_months,
                TimeGridUtil.getTotalMonthsLeftInLife(birthDate)
            ), initialTimeLeft
        )
        assertEquals(
            ResourceStringWithArgs(
                R.string.time_left_in_percent,
                TimeGridUtil.getPercentageMonthsLeftInLife(birthDate)
            ), toggledTimeLeft)
    }

    @Test
    fun `onTimeLeftClicked prompts date picker when birth date is null`() = runTest {
        viewModel.onTitleClicked() // Year -> Month
        viewModel.onTitleClicked() // Month -> Life
        viewModel.onTimeLeftClicked()

        // Now that the Category is Life and birthDate is null,
        // datePicker should be displayed when TimeLeft is clicked
        assertTrue(viewModel.showDatePicker.first())
    }

    @Test
    fun `onDatePickerDismissed hides date picker`() = runTest {
        viewModel.onDatePickerDismissed()
        assertFalse(viewModel.showDatePicker.first())
    }
}
