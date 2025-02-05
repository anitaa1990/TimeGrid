package com.an.timeleft.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TimeGridUtilTest {
    private val systemZone = ZoneId.systemDefault() // Get system's time zone
    private val now = LocalDate.now(systemZone) // Normalize to system timezone

    @Test
    fun `test getTotalDaysInYear`() {
        val expectedDays = if (TimeGridUtil.getCurrentYear() % 4 == 0) 366 else 365
        assertEquals(expectedDays.toLong(), TimeGridUtil.getTotalDaysInYear())
    }

    @Test
    fun `test getDaysCompletedInYear`() {
        val expectedCompletedDays = now.dayOfYear.toLong()
        assertEquals(expectedCompletedDays, TimeGridUtil.getDaysCompletedInYear())
    }

    @Test
    fun `test getDaysLeftInYear`() {
        val expectedDaysLeft = (TimeGridUtil.getTotalDaysInYear() - TimeGridUtil.getDaysCompletedInYear())
        assertEquals(expectedDaysLeft, TimeGridUtil.getDaysLeftInYear())
    }

    @Test
    fun `test getPercentageDaysLeftInYear`() {
        val expectedPercentage = ((TimeGridUtil.getDaysLeftInYear().toDouble() / TimeGridUtil.getTotalDaysInYear().toDouble()) * 100).toLong()
        assertEquals(expectedPercentage, TimeGridUtil.getPercentageDaysLeftInYear())
    }

    @Test
    fun `test getTotalDaysInMonth`() {
        val expectedDays = now.lengthOfMonth().toLong()
        assertEquals(expectedDays, TimeGridUtil.getTotalDaysInMonth())
    }

    @Test
    fun `test getDaysCompleted`() {
        val expectedDaysCompleted = now.dayOfMonth.toLong() - 1
        assertEquals(expectedDaysCompleted, TimeGridUtil.getDaysCompletedInMonth())
    }

    @Test
    fun `test getDaysLeft`() {
        val expectedDaysLeft = (TimeGridUtil.getTotalDaysInMonth() - TimeGridUtil.getDaysCompletedInMonth())
        assertEquals(expectedDaysLeft, TimeGridUtil.getDaysLeftInMonth())
    }

    @Test
    fun `test getPercentageDaysLeft`() {
        val expectedPercentage = ((TimeGridUtil.getDaysLeftInMonth().toDouble() / TimeGridUtil.getTotalDaysInMonth().toDouble()) * 100).toLong()
        assertEquals(expectedPercentage, TimeGridUtil.getPercentageDaysLeftInMonth())
    }

    @Test
    fun `test getTotalMonthsLeft with past birthdate`() {
        val birthDate = LocalDate.of(1995, 6, 15)
        val expectedMonthsLived = ChronoUnit.MONTHS.between(birthDate, now)
        val expectedMonthsLeft = (1000 - expectedMonthsLived).coerceAtLeast(0)
        assertEquals(expectedMonthsLeft.toLong(), TimeGridUtil.getTotalMonthsLeftInLife(birthDate))
    }

    @Test
    fun `test getTotalMonthsLeft with future birthdate`() {
        val birthDate = now.plusYears(10) // Future birthdate (invalid case)
        assertEquals(1000, TimeGridUtil.getTotalMonthsLeftInLife(birthDate))
    }

    @Test
    fun `test getPercentageMonthsLeft`() {
        val birthDate = LocalDate.of(1995, 6, 15)
        val expectedPercentage = ((TimeGridUtil.getTotalMonthsLeftInLife(birthDate).toDouble() / 1000.0) * 100).toLong()
        assertEquals(expectedPercentage, TimeGridUtil.getPercentageMonthsLeftInLife(birthDate))
    }
}