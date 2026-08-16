package com.lincelx.sysadmtools.ui.screens.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthCalendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    collapsed: Boolean,
    visitCountForDate: (LocalDate) -> Int,
    isPastDate: (LocalDate) -> Boolean,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier,
    locale: Locale = Locale("ru"),
) {
    val today = LocalDate.now()
    val monthTitle = yearMonth.atDay(1)
        .month
        .getDisplayName(TextStyle.FULL_STANDALONE, locale)
        .replaceFirstChar { it.titlecase(locale) } + " ${yearMonth.year}"

    val firstDayOffset = remember(yearMonth) {
        yearMonth.atDay(1).dayOfWeek.value - DayOfWeek.MONDAY.value
    }
    val daysInMonth = remember(yearMonth) { yearMonth.lengthOfMonth() }
    val totalCells = remember(yearMonth) {
        ((firstDayOffset + daysInMonth + 6) / 7) * 7
    }
    val totalWeekRows = totalCells / 7

    val visibleWeekRange = remember(yearMonth, selectedDate, collapsed, totalWeekRows) {
        if (!collapsed || selectedDate == null || !YearMonth.from(selectedDate).equals(yearMonth)) {
            0 until totalWeekRows
        } else {
            val dayNumber = selectedDate.dayOfMonth
            val selectedWeekRow = (firstDayOffset + dayNumber - 1) / 7
            val visibleWeekRows = (totalWeekRows - 1).coerceAtLeast(2)
            val startWeek = (selectedWeekRow - (visibleWeekRows - 2))
                .coerceIn(0, (totalWeekRows - visibleWeekRows).coerceAtLeast(0))
            startWeek until (startWeek + visibleWeekRows)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Предыдущий месяц",
                )
            }
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Следующий месяц",
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            for (weekIndex in visibleWeekRange) {
                val weekStart = weekIndex * 7
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (cellIndex in weekStart until weekStart + 7) {
                        val dayNumber = cellIndex - firstDayOffset + 1
                        if (dayNumber in 1..daysInMonth) {
                            val date = yearMonth.atDay(dayNumber)
                            val isPast = isPastDate(date)
                            CalendarDayCell(
                                day = dayNumber,
                                visitCount = visitCountForDate(date),
                                isSelected = date == selectedDate,
                                isToday = date == today,
                                isPast = isPast,
                                onClick = { onDateSelected(date) },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    visitCount: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isPast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = when {
        isPast -> MaterialTheme.colorScheme.surface
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    val countColor = when {
        isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "День $day" },
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.align(Alignment.Center),
        )
        if (visitCount > 0) {
            Text(
                text = visitCount.toString(),
                fontSize = 11.sp,
                color = countColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
