package com.kyilmaz.neuronetworkingtitle

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Lightweight time helpers for the UI (no Android dependencies). */
object TimeFormatters {
    private fun shortDateFormatter(): DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    private fun shortTimeFormatter(): DateTimeFormatter =
        DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

    /**
     * Examples:
     * - "Just now"
     * - "5m"
     * - "2h"
     * - "Yesterday"
     * - "Dec 28"
     */
    fun relativeDayOrShort(tsIso: String, now: Instant = Instant.now()): String {
        val ts = runCatching { Instant.parse(tsIso) }.getOrNull() ?: return ""
        val minutes = ChronoUnit.MINUTES.between(ts, now)
        if (minutes < 1) return "Just now"
        if (minutes < 60) return "${minutes}m"

        val hours = ChronoUnit.HOURS.between(ts, now)
        if (hours < 24) return "${hours}h"

        val zone = ZoneId.systemDefault()
        val day = ts.atZone(zone).toLocalDate()
        val today = LocalDate.now(zone)
        return when (ChronoUnit.DAYS.between(day, today)) {
            1L -> "Yesterday"
            else -> shortDateFormatter().format(day)
        }
    }

    fun timeOfDay(tsIso: String): String {
        val ts = runCatching { Instant.parse(tsIso) }.getOrNull() ?: return ""
        return shortTimeFormatter().format(ts.atZone(ZoneId.systemDefault()))
    }
}
