package com.an.timeleft.util

import java.time.LocalDate
import java.time.Year
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

object TimeGridUtil {
    private val currentYear = Year.now().value
    private val firstDayOfYear = LocalDate.of(currentYear, 1, 1)
    private val lastDayOfYear = LocalDate.of(currentYear, 12, 31)
    private val currentDate = LocalDate.now(ZoneOffset.UTC)

    private val totalDaysInYear = ChronoUnit.DAYS.between(firstDayOfYear, lastDayOfYear) + 1
    private val daysCompletedInYear = ChronoUnit.DAYS.between(firstDayOfYear, currentDate) + 1
    private val daysLeftInYear = totalDaysInYear - daysCompletedInYear

    private val currentMonth = currentDate.month
    private val firstDayOfMonth: LocalDate = currentDate.withDayOfMonth(1)
    private val lastDayOfMonth: LocalDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth())

    private val totalDaysInMonth = ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth) + 1
    private val daysCompleted = ChronoUnit.DAYS.between(firstDayOfMonth, currentDate)
    private val daysLeft = totalDaysInMonth - daysCompleted

    private const val totalMonthsInLife = 1000 // Assuming a lifespan of 1000 months

    fun getCurrentYear() = currentYear
    fun getTotalDaysInYear(): Long = totalDaysInYear
    fun getDaysCompletedInYear(): Long = daysCompletedInYear
    fun getDaysLeftInYear(): Long = daysLeftInYear

    fun getPercentageDaysLeftInYear() =
        ((daysLeftInYear.toDouble() / totalDaysInYear.toDouble()) * 100).toLong()

    fun getCurrentMonth() = currentMonth
    fun getTotalDaysInMonth(): Long = totalDaysInMonth
    fun getDaysCompletedInMonth(): Long = daysCompleted
    fun getDaysLeftInMonth(): Long = daysLeft

    fun getPercentageDaysLeftInMonth() = ((daysLeft.toDouble() / totalDaysInMonth.toDouble()) * 100).toLong()

    fun getTotalMonthsInLife() = totalMonthsInLife.toLong()

    fun getTotalMonthsCompletedInLife(birthDate: LocalDate): Long {
        val monthsLived = ChronoUnit.MONTHS.between(birthDate, currentDate)
        return when {
            monthsLived < 0 -> 0 // If birth date is in the future, return 0
            else -> monthsLived
        }
    }

    fun getTotalMonthsLeftInLife(birthDate: LocalDate): Long {
        val monthsLived = ChronoUnit.MONTHS.between(birthDate, currentDate)
        return when {
            monthsLived < 0 -> totalMonthsInLife.toLong() // Birthdate is in the future, return 1000
            else -> (totalMonthsInLife - monthsLived).coerceAtLeast(0) // Ensure it never goes negative
        }
    }

    fun getPercentageMonthsLeftInLife(birthDate: LocalDate) =
        ((getTotalMonthsLeftInLife(birthDate).toDouble() / totalMonthsInLife.toDouble()) * 100).toLong()
}