package com.lincelx.sysadmtools.util

import java.time.LocalDate

fun formatDateGenitive(date: LocalDate, includeYear: Boolean = false): String {
    val monthName = when (date.monthValue) {
        1 -> "января"
        2 -> "февраля"
        3 -> "марта"
        4 -> "апреля"
        5 -> "мая"
        6 -> "июня"
        7 -> "июля"
        8 -> "августа"
        9 -> "сентября"
        10 -> "октября"
        11 -> "ноября"
        12 -> "декабря"
        else -> ""
    }
    return if (includeYear) {
        "${date.dayOfMonth} $monthName ${date.year}"
    } else {
        "${date.dayOfMonth} $monthName"
    }
}
