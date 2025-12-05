package com.robert.tasks.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {

    // Input format from your API: "2025-12-15T06:00"
    private val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    // Output formats
    private val monthDayFormatter = DateTimeFormatter.ofPattern("MMM d")
    private val fullDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    /**
     * Parses the ISO date string to LocalDateTime
     * @param dateString Format: "2025-12-15T06:00"
     * @return LocalDateTime or null if parsing fails
     */
    fun parseDate(dateString: String?): LocalDateTime? {
        return try {
            if (dateString.isNullOrBlank()) null
            else LocalDateTime.parse(dateString, inputFormatter)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Formats date to "May 5" format
     * @param dateString Format: "2025-12-15T06:00"
     * @return Formatted string like "Dec 15" or "Invalid date"
     */
    fun formatToMonthDay(dateString: String?): String {
        val date = parseDate(dateString) ?: return "Invalid date"
        return date.format(monthDayFormatter)
    }

    /**
     * Formats date to "December 15, 2025" format
     * @param dateString Format: "2025-12-15T06:00"
     * @return Formatted string or "Invalid date"
     */
    fun formatToFullDate(dateString: String?): String {
        val date = parseDate(dateString) ?: return "Invalid date"
        return date.format(fullDateFormatter)
    }

    /**
     * Formats time to "6:00 AM" format
     * @param dateString Format: "2025-12-15T06:00"
     * @return Formatted time string or empty
     */
    fun formatToTime(dateString: String?): String {
        val date = parseDate(dateString) ?: return ""
        return date.format(timeFormatter)
    }

    /**
     * Checks if the task is overdue
     * @param dateString Format: "2025-12-15T06:00"
     * @return true if the due date has passed
     */
    fun isOverdue(dateString: String?): Boolean {
        val dueDate = parseDate(dateString) ?: return false
        val now = LocalDateTime.now()
        return dueDate.isBefore(now)
    }

    /**
     * Checks if the task is due today
     * @param dateString Format: "2025-12-15T06:00"
     * @return true if due date is today
     */
    fun isDueToday(dateString: String?): Boolean {
        val dueDate = parseDate(dateString) ?: return false
        val now = LocalDateTime.now()
        return dueDate.toLocalDate() == now.toLocalDate()
    }

    /**
     * Checks if the task is due tomorrow
     * @param dateString Format: "2025-12-15T06:00"
     * @return true if due date is tomorrow
     */
    fun isDueTomorrow(dateString: String?): Boolean {
        val dueDate = parseDate(dateString) ?: return false
        val tomorrow = LocalDateTime.now().plusDays(1)
        return dueDate.toLocalDate() == tomorrow.toLocalDate()
    }

    /**
     * Gets a human-readable due date text
     * @param dateString Format: "2025-12-15T06:00"
     * @return "today", "tomorrow", "Overdue", or formatted date like "Dec 15"
     */
    fun getDueDateText(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "No due date"

        return when {
            isOverdue(dateString) -> "Overdue"
            isDueToday(dateString) -> "today"
            isDueTomorrow(dateString) -> "tomorrow"
            else -> formatToMonthDay(dateString)
        }
    }

    /**
     * Gets the number of days until due date
     * @param dateString Format: "2025-12-15T06:00"
     * @return Negative if overdue, positive if upcoming, null if invalid
     */
    fun getDaysUntilDue(dateString: String?): Long? {
        val dueDate = parseDate(dateString) ?: return null
        val now = LocalDateTime.now()
        return ChronoUnit.DAYS.between(now.toLocalDate(), dueDate.toLocalDate())
    }

    /**
     * Gets relative time string (e.g., "in 3 days", "2 days ago")
     * @param dateString Format: "2025-12-15T06:00"
     * @return Human-readable relative time
     */
    fun getRelativeTimeText(dateString: String?): String {
        val days = getDaysUntilDue(dateString) ?: return "Invalid date"

        return when {
            days < 0 -> {
                val absDays = kotlin.math.abs(days)
                when (absDays) {
                    0L -> "Today"
                    1L -> "Yesterday"
                    else -> "$absDays days ago"
                }
            }
            days == 0L -> "Today"
            days == 1L -> "Tomorrow"
            days <= 7 -> "in $days days"
            days <= 30 -> "in ${days / 7} weeks"
            else -> formatToMonthDay(dateString)
        }
    }

    /**
     * Checks if task is due soon (within next 3 days)
     * @param dateString Format: "2025-12-15T06:00"
     * @return true if due within 3 days and not overdue
     */
    fun isDueSoon(dateString: String?): Boolean {
        val days = getDaysUntilDue(dateString) ?: return false
        return days in 0..3
    }

    /**
     * Gets the current date time as a formatted string
     * @return Current date in "yyyy-MM-dd'T'HH:mm" format
     */
    fun getCurrentDateTime(): String {
        return LocalDateTime.now().format(inputFormatter)
    }

    /**
     * Creates a date string from components
     * @param year The year
     * @param month The month (1-12)
     * @param day The day of month
     * @param hour The hour (0-23)
     * @param minute The minute (0-59)
     * @return Formatted date string "yyyy-MM-dd'T'HH:mm"
     */
    fun createDateString(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0): String {
        val dateTime = LocalDateTime.of(year, month, day, hour, minute)
        return dateTime.format(inputFormatter)
    }
}