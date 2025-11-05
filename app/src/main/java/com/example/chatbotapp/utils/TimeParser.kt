package com.example.chatbotapp.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object TimeParser {
    fun parseNaturalTime(input: String): Long? {
        val now = Calendar.getInstance()

        // Match "in X minutes/hours/days"
        val pattern = Pattern.compile("in (\\d+) (minute|hour|day|minutes|hours|days)", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(input)
        if (matcher.find()) {
            val amount = matcher.group(1).toInt()
            val unit = matcher.group(2).lowercase(Locale.getDefault())
            when {
                unit.startsWith("minute") -> now.add(Calendar.MINUTE, amount)
                unit.startsWith("hour") -> now.add(Calendar.HOUR, amount)
                unit.startsWith("day") -> now.add(Calendar.DAY_OF_YEAR, amount)
            }
            return now.timeInMillis
        }

        // Match “tomorrow at HH:MM” or “today at HH:MM”
        val timePattern = Pattern.compile("(today|tomorrow) at (\\d{1,2})(?::(\\d{2}))? ?(am|pm)?", Pattern.CASE_INSENSITIVE)
        val timeMatcher = timePattern.matcher(input)
        if (timeMatcher.find()) {
            val target = timeMatcher.group(1)?.lowercase()
            val hour = timeMatcher.group(2)?.toIntOrNull() ?: 0
            val minute = timeMatcher.group(3)?.toIntOrNull() ?: 0
            val ampm = timeMatcher.group(4)?.lowercase()

            if (target == "tomorrow") now.add(Calendar.DAY_OF_YEAR, 1)
            now.set(Calendar.HOUR, hour)
            now.set(Calendar.MINUTE, minute)
            now.set(Calendar.SECOND, 0)
            now.set(Calendar.AM_PM, if (ampm == "pm") Calendar.PM else Calendar.AM)
            return now.timeInMillis
        }

        // Match “at 18:30” (24-hour format)
        val clockPattern = Pattern.compile("at (\\d{1,2}):(\\d{2})")
        val clockMatcher = clockPattern.matcher(input)
        if (clockMatcher.find()) {
            now.set(Calendar.HOUR_OF_DAY, clockMatcher.group(1).toInt())
            now.set(Calendar.MINUTE, clockMatcher.group(2).toInt())
            now.set(Calendar.SECOND, 0)
            return now.timeInMillis
        }

        return null
    }
}