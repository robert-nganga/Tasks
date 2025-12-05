package com.robert.tasks.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class DateUtilsTest {

    private val fixedNow = LocalDateTime.of(2025, 12, 5, 12, 0) // December 5, 2025, 12:00 PM

    @Test
    fun `parseDate should correctly parse valid date string`() {
        val dateString = "2025-12-25T10:30"
        val expected = LocalDateTime.of(2025, 12, 25, 10, 30)
        assertEquals(expected, DateUtils.parseDate(dateString))
    }

    @Test
    fun `parseDate should return null for invalid date string`() {
        val dateString = "invalid-date"
        assertEquals(null, DateUtils.parseDate(dateString))
    }

    @Test
    fun `formatToMonthDay should format date correctly`() {
        val dateString = "2025-12-25T10:30"
        assertEquals("Dec 25", DateUtils.formatToMonthDay(dateString))
    }

    @Test
    fun `formatToFullDate should format date correctly`() {
        val dateString = "2025-12-25T10:30"
        assertEquals("December 25, 2025", DateUtils.formatToFullDate(dateString))
    }

    @Test
    fun `formatToTime should format time correctly`() {
        val dateString = "2025-12-25T10:30"
        assertEquals("10:30", DateUtils.formatToTime(dateString))
    }

    @Test
    fun `isOverdue should return true for past dates`() {
        val dateString = "2025-12-04T12:00" // Yesterday
        assertTrue(DateUtils.isOverdue(dateString, fixedNow))
    }

    @Test
    fun `isOverdue should return false for future dates`() {
        val dateString = "2025-12-06T12:00" // Tomorrow
        assertFalse(DateUtils.isOverdue(dateString, fixedNow))
    }

    @Test
    fun `isOverdue should return false for current time`() {
        val dateString = "2025-12-05T12:00" // Now
        assertFalse(DateUtils.isOverdue(dateString, fixedNow))
    }

    @Test
    fun `isDueToday should return true for today's date`() {
        val dateString = "2025-12-05T10:00" // Today, different time
        assertTrue(DateUtils.isDueToday(dateString, fixedNow))
    }

    @Test
    fun `isDueToday should return false for other dates`() {
        val dateString = "2025-12-06T10:00" // Tomorrow
        assertFalse(DateUtils.isDueToday(dateString, fixedNow))
    }

    @Test
    fun `isDueTomorrow should return true for tomorrow's date`() {
        val dateString = "2025-12-06T10:00" // Tomorrow
        assertTrue(DateUtils.isDueTomorrow(dateString, fixedNow))
    }

    @Test
    fun `isDueTomorrow should return false for other dates`() {
        val dateString = "2025-12-05T10:00" // Today
        assertFalse(DateUtils.isDueTomorrow(dateString, fixedNow))
    }

    @Test
    fun `getDueDateText should return Overdue for past dates`() {
        val dateString = "2025-12-04T12:00"
        assertEquals("Overdue", DateUtils.getDueDateText(dateString, fixedNow))
    }

    @Test
    fun `getDueDateText should return Today for today's date`() {
        val dateString = "2025-12-05T10:00"
        assertEquals("Today", DateUtils.getDueDateText(dateString, fixedNow))
    }

    @Test
    fun `getDueDateText should return Tomorrow for tomorrow's date`() {
        val dateString = "2025-12-06T10:00"
        assertEquals("Tomorrow", DateUtils.getDueDateText(dateString, fixedNow))
    }

    @Test
    fun `getDueDateText should return full date for future dates`() {
        val dateString = "2025-12-10T10:00"
        assertEquals("December 10, 2025", DateUtils.getDueDateText(dateString, fixedNow))
    }

    @Test
    fun `getDaysUntilDue should return negative for past dates`() {
        val dateString = "2025-12-04T12:00"
        assertEquals(-1L, DateUtils.getDaysUntilDue(dateString, fixedNow))
    }

    @Test
    fun `getDaysUntilDue should return zero for today's date`() {
        val dateString = "2025-12-05T10:00"
        assertEquals(0L, DateUtils.getDaysUntilDue(dateString, fixedNow))
    }

    @Test
    fun `getDaysUntilDue should return positive for future dates`() {
        val dateString = "2025-12-06T10:00"
        assertEquals(1L, DateUtils.getDaysUntilDue(dateString, fixedNow))
    }

    @Test
    fun `getRelativeTimeText should return days ago for past dates`() {
        val dateString = "2025-12-03T12:00" // 2 days ago
        assertEquals("2 days ago", DateUtils.getRelativeTimeText(dateString, fixedNow))
    }

    @Test
    fun `getRelativeTimeText should return Today for current date`() {
        val dateString = "2025-12-05T10:00"
        assertEquals("Today", DateUtils.getRelativeTimeText(dateString, fixedNow))
    }

    @Test
    fun `getRelativeTimeText should return Tomorrow for next day`() {
        val dateString = "2025-12-06T10:00"
        assertEquals("Tomorrow", DateUtils.getRelativeTimeText(dateString, fixedNow))
    }

    @Test
    fun `getRelativeTimeText should return in X days for upcoming week`() {
        val dateString = "2025-12-08T10:00" // 3 days from now
        assertEquals("in 3 days", DateUtils.getRelativeTimeText(dateString, fixedNow))
    }

    @Test
    fun `getRelativeTimeText should return in X weeks for upcoming month`() {
        val dateString = "2025-12-20T10:00" // 15 days from now (2 weeks)
        assertEquals("in 2 weeks", DateUtils.getRelativeTimeText(dateString, fixedNow))
    }

    @Test
    fun `getRelativeTimeText should return month day for far future`() {
        val dateString = "2026-01-15T10:00"
        assertEquals("Jan 15", DateUtils.getRelativeTimeText(dateString, fixedNow))
    }

    @Test
    fun `isDueSoon should return true for dates within 3 days`() {
        val dateString = "2025-12-07T12:00" // 2 days from now
        assertTrue(DateUtils.isDueSoon(dateString, fixedNow))
    }

    @Test
    fun `isDueSoon should return false for dates beyond 3 days`() {
        val dateString = "2025-12-09T12:00" // 4 days from now
        assertFalse(DateUtils.isDueSoon(dateString, fixedNow))
    }

    @Test
    fun `getCurrentDateTime should return formatted current date time`() {
        val expected = "2025-12-05T12:00"
        assertEquals(expected, DateUtils.getCurrentDateTime(fixedNow))
    }

    @Test
    fun `createDateString should return formatted date string`() {
        val expected = "2025-01-01T09:30"
        assertEquals(expected, DateUtils.createDateString(2025, 1, 1, 9, 30))
    }
}