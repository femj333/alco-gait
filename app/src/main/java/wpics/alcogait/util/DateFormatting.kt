package wpics.alcogait.util

import java.time.ZoneId
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatAsReadableDate(isoTimestamp: String): String {
    val instant = Instant.parse(isoTimestamp)
    val zonedDate = instant.atZone(ZoneId.systemDefault())
    val day = zonedDate.dayOfMonth
    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
    val month = zonedDate.format(DateTimeFormatter.ofPattern("MMMM", Locale.US))
    return "$month $day$suffix"
}