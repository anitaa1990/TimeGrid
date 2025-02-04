package com.an.timeleft.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TimeLeftUtilTest {
    private val systemZone = ZoneId.systemDefault() // Get system's time zone
    private val now = LocalDate.now(systemZone) // Normalize to system timezone

    @Test
    fun `test getTotalDaysInYear`() {
        val expectedDays = if (TimeLeftUtil.getCurrentYear() % 4 == 0) 366 else 365
        assertEquals(expectedDays.toLong(), TimeLeftUtil.getTotalDaysInYear())
    }

    @Test
    fun `test getDaysCompletedInYear`() {
        val expectedCompletedDays = now.dayOfYear.toLong()
        assertEquals(expectedCompletedDays, TimeLeftUtil.getDaysCompletedInYear())
    }

    @Test
    fun `test getDaysLeftInYear`() {
        val expectedDaysLeft = (TimeLeftUtil.getTotalDaysInYear() - TimeLeftUtil.getDaysCompletedInYear())
        assertEquals(expectedDaysLeft, TimeLeftUtil.getDaysLeftInYear())
    }

    @Test
    fun `test getPercentageDaysLeftInYear`() {
        val expectedPercentage = ((TimeLeftUtil.getDaysLeftInYear().toDouble() / TimeLeftUtil.getTotalDaysInYear().toDouble()) * 100).toLong()
        assertEquals(expectedPercentage, TimeLeftUtil.getPercentageDaysLeftInYear())
    }

    @Test
    fun `test getTotalDaysInMonth`() {
        val expectedDays = now.lengthOfMonth().toLong()
        assertEquals(expectedDays, TimeLeftUtil.getTotalDaysInMonth())
    }

    @Test
    fun `test getDaysCompleted`() {
        val expectedDaysCompleted = now.dayOfMonth.toLong() - 1
        assertEquals(expectedDaysCompleted, TimeLeftUtil.getDaysCompletedInMonth())
    }

    @Test
    fun `test getDaysLeft`() {
        val expectedDaysLeft = (TimeLeftUtil.getTotalDaysInMonth() - TimeLeftUtil.getDaysCompletedInMonth())
        assertEquals(expectedDaysLeft, TimeLeftUtil.getDaysLeftInMonth())
    }

    @Test
    fun `test getPercentageDaysLeft`() {
        val expectedPercentage = ((TimeLeftUtil.getDaysLeftInMonth().toDouble() / TimeLeftUtil.getTotalDaysInMonth().toDouble()) * 100).toLong()
        assertEquals(expectedPercentage, TimeLeftUtil.getPercentageDaysLeftInMonth())
    }

    @Test
    fun `test getTotalMonthsLeft with past birthdate`() {
        val birthDate = LocalDate.of(1995, 6, 15)
        val expectedMonthsLived = ChronoUnit.MONTHS.between(birthDate, now)
        val expectedMonthsLeft = (1000 - expectedMonthsLived).coerceAtLeast(0)
        assertEquals(expectedMonthsLeft.toLong(), TimeLeftUtil.getTotalMonthsLeftInLife(birthDate))
    }

    @Test
    fun `test getTotalMonthsLeft with future birthdate`() {
        val birthDate = now.plusYears(10) // Future birthdate (invalid case)
        assertEquals(1000, TimeLeftUtil.getTotalMonthsLeftInLife(birthDate))
    }

    @Test
    fun `test getPercentageMonthsLeft`() {
        val birthDate = LocalDate.of(1995, 6, 15)
        val expectedPercentage = ((TimeLeftUtil.getTotalMonthsLeftInLife(birthDate).toDouble() / 1000.0) * 100).toLong()
        assertEquals(expectedPercentage, TimeLeftUtil.getPercentageMonthsLeftInLife(birthDate))
    }
}